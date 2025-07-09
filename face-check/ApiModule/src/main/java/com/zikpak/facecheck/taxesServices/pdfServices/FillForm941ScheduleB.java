package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FillForm941ScheduleB {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private final AmazonS3Service amazonS3Service;

    public byte[] generateFilledPdf(Integer userId, Integer companyId, int year, int quarter) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/assets/f941sb22.pdf");


        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(inputStream),
                new PdfWriter(baos)
        );
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        // 1. Fill EIN digits f1_01..f1_09
        String einDigits = company.getEmployerEIN().replaceAll("\\D+", "");
        if (einDigits.length() != 9) {
            throw new IllegalArgumentException("EIN должно состоять из 9 цифр, а у вас: " + einDigits);
        }
        for (int i = 0; i < 9; i++) {
            String num = String.format("%02d", i + 1);
            String fieldName = String.format("topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_%s", num);
            fill(fields, fieldName, String.valueOf(einDigits.charAt(i)));
        }
        // 2. Company name in f1_10
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_10", company.getCompanyName());
        // 3. Year in f1_11..f1_14
        String strYear = String.format("%04d", year);
        for (int i = 0; i < 4; i++) {
            String num = String.format("%02d", 11 + i);
            fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_" + num, String.valueOf(strYear.charAt(i)));
        }
        // 4. Quarter checkbox c1_1[0..3]
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Квартал должен быть от 1 до 4");
        }
        String qField = String.format("topmostSubform[0].Page1[0].c1_1[%d]", quarter - 1);
        fill(fields, qField, "On");

        // 5. Load payments for quarter
        List<PaymentHistoryIrs> payments = paymentHistoryIrsRepository
                .findAllByCompany_IdAndYearAndQuarter(companyId, year, quarter, Pageable.unpaged())
                .getContent();

        int startMonth = (quarter - 1) * 3 + 1;
        // 6a. Initialize all daily slots to zero
        for (int monthNum = 1; monthNum <= 3; monthNum++) {
            for (int day = 1; day <= 31; day++) {
                int blockOffset = (monthNum - 1) * 64;
                int base;
                String group;
                if (day <= 8) {
                    group = String.format("Day1-8_Month%d", monthNum);
                    base = 15 + blockOffset + 2 * (day - 1);
                } else if (day <= 16) {
                    group = String.format("Day9-16_Month%d", monthNum);
                    base = 31 + blockOffset + 2 * (day - 9);
                } else if (day <= 24) {
                    group = String.format("Day17-24_Month%d", monthNum);
                    base = 47 + blockOffset + 2 * (day - 17);
                } else {
                    group = String.format("Day25-31_Month%d", monthNum);
                    base = 63 + blockOffset + 2 * (day - 25);
                }
                String intField = String.format("%02d", base);
                String fracField = String.format("%02d", base + 1);
                String prefix = String.format("topmostSubform[0].Page1[0].%s[0].", group);
                // fill integer part
                fill(fields, prefix + "f1_" + intField, "00");
                // fill fraction or group-level for Month2 Day1
                if (monthNum == 2 && day == 1) {
                    String groupName = String.format("topmostSubform[0].Page1[0].%s[0]", group);
                    fill(fields, groupName, "00");
                } else {
                    fill(fields, prefix + "f1_" + fracField, "00");
                }
            }
        }
        // 6b. Fill actual payments (overwrites zeros)
        for (PaymentHistoryIrs p : payments) {
            LocalDate dt = p.getPaymentDate();
            int monthOffset = dt.getMonthValue() - startMonth;
            if (monthOffset < 0 || monthOffset > 2) continue;
            int monthNum = monthOffset + 1;
            int day = dt.getDayOfMonth();
            int blockOffset = (monthNum - 1) * 64;
            String group;
            int base;
            if (day <= 8) {
                group = String.format("Day1-8_Month%d", monthNum);
                base = 15 + blockOffset + 2 * (day - 1);
            } else if (day <= 16) {
                group = String.format("Day9-16_Month%d", monthNum);
                base = 31 + blockOffset + 2 * (day - 9);
            } else if (day <= 24) {
                group = String.format("Day17-24_Month%d", monthNum);
                base = 47 + blockOffset + 2 * (day - 17);
            } else {
                group = String.format("Day25-31_Month%d", monthNum);
                base = 63 + blockOffset + 2 * (day - 25);
            }
            String intField = String.format("%02d", base);
            String fracField = String.format("%02d", base + 1);
            String prefix = String.format("topmostSubform[0].Page1[0].%s[0].", group);
            String[] parts = splitAmount(p.getAmount());

            // fill integer part
            fill(fields, prefix + "f1_" + intField, parts[0]);
            // дробная часть
            fill(fields, prefix + "f1_" + fracField, parts[1]);
        }
        // 7. Monthly liabilities (tax liability fields)
        BigDecimal monthSum1 = payments.stream()
                .filter(p -> p.getPaymentDate().getMonthValue() == startMonth)
                .map(PaymentHistoryIrs::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthSum2 = payments.stream()
                .filter(p -> p.getPaymentDate().getMonthValue() == startMonth + 1)
                .map(PaymentHistoryIrs::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthSum3 = payments.stream()
                .filter(p -> p.getPaymentDate().getMonthValue() == startMonth + 2)
                .map(PaymentHistoryIrs::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String[] m1 = splitAmount(monthSum1);
        fill(fields, "topmostSubform[0].Page1[0].f1_77", m1[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_78", m1[1]);
        String[] m2 = splitAmount(monthSum2);
        fill(fields, "topmostSubform[0].Page1[0].f1_141", m2[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_142", m2[1]);
        String[] m3 = splitAmount(monthSum3);
        fill(fields, "topmostSubform[0].Page1[0].f1_205", m3[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_206", m3[1]);
        // 8. Total deposits fields f1_207,f1_208
        BigDecimal total = monthSum1.add(monthSum2).add(monthSum3);
        String[] totalParts = splitAmount(total);
        fill(fields, "topmostSubform[0].Page1[0].f1_207", totalParts[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_208", totalParts[1]);

        form.flattenFields();
        pdfDoc.close();


        byte[] pdfBytes = baos.toByteArray();


        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f941sb_%d_%d_%d.pdf",
                companyId, year, quarter);

        String key = String.format("%s/%d/941sbform/941Pdf/%d/%d/%s",
                companyKeyPart,
                companyId,
                year,
                quarter,
                fileName
        );

        amazonS3Service.uploadPdfToS3(pdfBytes, key);
        return  pdfBytes;
    }

    private void fill(Map<String, PdfFormField> fields, String name, String value) {
        PdfFormField f = fields.get(name + "[0]");
        if (f == null) {
            f = fields.get(name);
        }
        if (f == null) {
            System.err.println("⚠️ Field not found: " + name);
        } else {
            try {
                f.setValue(value);
            } catch (Exception e) {
                System.err.println("❌ Cannot set value for field " + name + ": " + e.getMessage());
            }
        }
    }

    private String[] splitAmount(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        String[] parts = scaled.toPlainString().split("\\.");
        return new String[]{parts[0], parts.length > 1 ? parts[1] : "00"};
    }
}
