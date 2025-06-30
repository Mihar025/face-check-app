package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FillForm940SA {

    private final AmazonS3Service amazonS3Service;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;

    // Константы для NY 2024
    private static final BigDecimal NY_2024_REDUCTION_RATE = new BigDecimal("0.009"); // 0.9%

    public byte[] generateFilledPdf(Integer companyId, int year) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f940sa.pdf";

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(src),
                new PdfWriter(baos)
        );

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        log.info("==== Заполняем Form 940 Schedule A для {} за 2024 год (NY) ====",
                company.getCompanyName());

        // 1. Заполняем EIN
        fillEIN(fields, company.getEmployerEIN());

        // 2. Заполляем название компании
        fill(fields, "topmostSubform[0].Page1[0].f1_10[0]", company.getCompanyName());

        // 3. Вычисляем FUTA taxable wages (line 7 из Form 940)
        BigDecimal futaTaxableWages = calculateFUTATaxableWages(companyId, year);

        // 4. Вычисляем NY credit reduction
        BigDecimal creditReduction = futaTaxableWages.multiply(NY_2024_REDUCTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Заполняем NY данные
        fillNYData(fields, futaTaxableWages, creditReduction);

        // 6. Заполляем итоговую сумму
        fillTotalCreditReduction(fields, creditReduction);

        form.flattenFields();
        pdfDoc.close();

        byte[] pdfBytes = baos.toByteArray();
        uploadToS3(company, companyId, year, pdfBytes);

        log.info("✅ Schedule A создана: FUTA Wages=${}, NY Credit Reduction=${}",
                futaTaxableWages, creditReduction);

        return pdfBytes;
    }

    /**
     * Заполняем EIN по цифрам
     */
    private void fillEIN(Map<String, PdfFormField> fields, String ein) {
        String rawEin = ein.replaceAll("\\D", "");
        String paddedEin = String.format("%9s", rawEin).replace(' ', '0');

        for (int i = 0; i < 9; i++) {
            char digit = paddedEin.charAt(i);
            String fieldName = String.format("topmostSubform[0].Page1[0].f1_%d[0]", i + 1);
            fill(fields, fieldName, String.valueOf(digit));
        }
    }

    /**
     * Заполляем данные для New York
     */
    private void fillNYData(Map<String, PdfFormField> fields, BigDecimal futaTaxableWages,
                            BigDecimal creditReduction) {

        // Checkbox NY
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NYPostal[0].c1_35[0]", "1");

        // FUTA TAXABLE WAGES (разбиваем на доллары и центы)
        String[] futaWagesSplit = splitAmount(futaTaxableWages);
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NYFUTA[0].f1_147[0]", futaWagesSplit[0]);
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NYFUTA[0].f1_148[0]", futaWagesSplit[1]);

        // Reduction Rate 0.9%
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NY_ReductionRate[0]", "0.9");

        // Credit reduction (разбиваем на доллары и центы)
        String[] creditSplit = splitAmount(creditReduction);
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NYCredit[0].f1_149[0]", creditSplit[0]);
        fill(fields, "topmostSubform[0].Page1[0].Column2[0].BodyRow35[0].NYCredit[0].f1_150[0]", creditSplit[1]);
    }

    /**
     * Заполняем итоговую сумму credit reduction
     */
    private void fillTotalCreditReduction(Map<String, PdfFormField> fields, BigDecimal totalCredit) {
        String[] totalSplit = splitAmount(totalCredit);
        fill(fields, "topmostSubform[0].Page1[0].f1_223[0]", totalSplit[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_224[0]", totalSplit[1]);
    }

    /**
     * Вычисляем FUTA taxable wages (то же что line 7 в Form 940)
     */
    private BigDecimal calculateFUTATaxableWages(Integer companyId, int year) {
        // line 3: Total gross wages
        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);

        // line 5: Exemptions (wages over $7000 per employee)
        BigDecimal line5 = calculateLine5(companyId, year);

        // line 7: FUTA taxable wages = line3 - line5
        return line3.subtract(line5);
    }

    /**
     * Вычисляем line 5 (exemptions) из Form 940
     */
    private BigDecimal calculateLine5(Integer companyId, int year) {
        List<Object[]> rows = employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(companyId, year);
        BigDecimal result = BigDecimal.ZERO;

        for (Object[] row : rows) {
            BigDecimal yearlyTotal = (BigDecimal) row[1];
            BigDecimal excess = yearlyTotal.subtract(new BigDecimal("7000"));
            result = result.add(excess);
        }

        return result;
    }

    /**
     * Получить NY credit reduction для интеграции с Form 940
     */
    public BigDecimal getNYCreditReduction(Integer companyId, int year) {
        BigDecimal futaTaxableWages = calculateFUTATaxableWages(companyId, year);
        return futaTaxableWages.multiply(NY_2024_REDUCTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Загрузка в S3
     */
    private void uploadToS3(Object company, Integer companyId, int year, byte[] pdfBytes) {
        String companyKeyPart = ((com.zikpak.facecheck.entity.Company)company).getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f940SA_%d_%d.pdf", companyId, year);
        String key = String.format("%s/%d/940SApdf/%s", companyKeyPart, companyId, fileName);

        amazonS3Service.uploadPdfToS3(pdfBytes, key);
        log.info("✅ Form 940 Schedule A uploaded to S3: {}", key);
    }

    // Helper methods
    private void fill(Map<String, PdfFormField> fields, String name, String value) {
        PdfFormField field = fields.get(name);
        if (field == null) {
            log.warn("⚠️ Field not found: {}", name);
        } else {
            try {
                field.setValue(value);
            } catch (Exception e) {
                log.error("❌ Cannot set value for field: {} | Reason: {}", name, e.getMessage());
            }
        }
    }

    private String[] splitAmount(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        String[] parts = scaled.toPlainString().split("\\.");
        return new String[] {
                parts[0],                    // до точки
                parts.length > 1 ? parts[1] : "00" // после точки
        };
    }
}