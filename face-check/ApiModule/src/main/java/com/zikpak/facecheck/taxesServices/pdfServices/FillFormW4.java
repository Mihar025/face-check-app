package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.Dependents;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DependentRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.zikpak.facecheck.entity.W4.FilingStatus.*;


@Service
@RequiredArgsConstructor
public class FillFormW4 {
    private final UserRepository userRepository;
    private final DependentRepository dependentRepository;
    private final CompanyRepository companyRepository;
    private final AmazonS3Service amazonS3Service;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    public byte[] generateW4Pdf(Integer userId, Integer companyId) throws IOException {
       InputStream src = getClass().getResourceAsStream("/forms/fw4.pdf");
        PdfReader reader = new PdfReader(src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

/*
        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }

 */
        var foundedWorker = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<Dependents> deps = dependentRepository.findAllByUser_Id(userId);
        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with id " + companyId + " not found"));






      //  fill(fields, "topmostSubform[0]", "1");
     //   fill(fields, "topmostSubform[0].Page1[0]","2" );
    //    fill(fields, "topmostSubform[0].Page1[0].Step1a[0]", "3" );
        if(foundedWorker.getMiddleInitial() == null){
            fill(fields, "topmostSubform[0].Page1[0].Step1a[0].f1_01[0]", foundedWorker.getFirstName());
        }
        else if(foundedWorker.getMiddleInitial() != null){
            fill(fields, "topmostSubform[0].Page1[0].Step1a[0].f1_01[0]", foundedWorker.getFirstName() + " " + foundedWorker.getMiddleInitial());
        }
        else{
            throw new RuntimeException("Fields are empty contact with Facecheck Corporation!!");
        }

        fill(fields, "topmostSubform[0].Page1[0].Step1a[0].f1_02[0]",foundedWorker.getLastName() );
        fill(fields, "topmostSubform[0].Page1[0].Step1a[0].f1_03[0]",foundedWorker.getHomeAddress());
        fill(fields, "topmostSubform[0].Page1[0].Step1a[0].f1_04[0]", foundedWorker.getCity() + " " + foundedWorker.getState() + " " + foundedWorker.getZipcode());
        fill(fields, "topmostSubform[0].Page1[0].f1_05[0]", foundedWorker.getSSN_WORKER());
        //Page 1 Three checkboxes!
            fill(fields, "topmostSubform[0].Page1[0].c1_1[0]","Off" ); // Single or Married filling separately
            fill(fields, "topmostSubform[0].Page1[0].c1_1[1]", "Off"); // Married filling jointly
            fill(fields, "topmostSubform[0].Page1[0].c1_1[2]","Off" ); // Head of house hold;!

        String exportValue = "On";
        switch (foundedWorker.getFilingStatus()) {
            case SINGLE, MARRIED_FILLING_SEPARATELY ->
                    fill(fields, "topmostSubform[0].Page1[0].c1_1[0]", exportValue);
            case MARRIED_FILLING_JOINTLY ->
                    fill(fields, "topmostSubform[0].Page1[0].c1_1[1]", exportValue);
            case HEAD_OF_HOUSEHOLD ->
                    fill(fields, "topmostSubform[0].Page1[0].c1_1[2]", exportValue);
        }



        // --- ШАГ 2: Multiple Jobs or Spouse Works ---
// Устанавливаем, что пользователь входит в шаг 2
        fill(fields, "topmostSubform[0].Page1[0].c1_1[0]", "Yes");           // “Complete this step if you…”
        if (foundedWorker.getTwoJobsCheckBox()) {
            // (c) If there are only two jobs total
            fill(fields, "topmostSubform[0].Page1[0].c1_2[0]", "Yes");       // ставим галочку “Step 2(c)”
            // не трогаем worksheet-результат
        } else {
            // (b) Multiple Jobs Worksheet result → Step 4(c)
            fill(fields, "topmostSubform[0].Page1[0].c1_2[0]", "Off");       // снимаем чекбокс
            fill(fields, "topmostSubform[0].Page1[0].f1_13[0]",
                    foundedWorker.getMultipleJobsAdditionalWithholding().toString());
        }

// --- ШАГ 3: Claim Dependent and Other Credits ---
// (дети <17 по $2 000, прочие по $500)
        BigDecimal childCredit = deps.stream()
                .filter(d -> Period.between(d.getBirthDate(), LocalDate.now()).getYears() < 17)
                .map(d -> new BigDecimal("2000"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal otherCredit = deps.stream()
                .filter(d -> Period.between(d.getBirthDate(), LocalDate.now()).getYears() >= 17)
                .map(d -> new BigDecimal("500"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        fill(fields, "topmostSubform[0].Page1[0].f1_06[0]", childCredit.toString());     // Step 3: qualifying children
        fill(fields, "topmostSubform[0].Page1[0].f1_07[0]", otherCredit.toString());     // Step 3: other dependents
        fill(fields, "topmostSubform[0].Page1[0].f1_09[0]",
                childCredit.add(otherCredit).toString());                                    // Step 3: total credits




// --- ШАГ 4: Other Adjustments (optional) ---
// 4(a) Other income (not from jobs)
        fill(fields, "topmostSubform[0].Page1[0].f1_10[0]",
                Optional.ofNullable(foundedWorker.getOtherIncome()).orElse(BigDecimal.ZERO).toString());

// 4(b) Deductions (beyond standard)
        fill(fields, "topmostSubform[0].Page1[0].f1_11[0]",
                Optional.ofNullable(foundedWorker.getDeductions()).orElse(BigDecimal.ZERO).toString());

// 4(c) Extra withholding
// сюда попадёт либо multipleJobsAdditionalWithholding (вариант b),
// либо просто user.getExtraWithHoldings() (если вы хотите принудительно доп. удерживать)
        BigDecimal extra = Optional.ofNullable(foundedWorker.getMultipleJobsAdditionalWithholding())
                .filter(mw -> !foundedWorker.getTwoJobsCheckBox())
                .orElse(foundedWorker.getExtraWithHoldings());
        fill(fields, "topmostSubform[0].Page1[0].f1_12[0]", extra.toString());




        fill(fields, "topmostSubform[0].Page1[0].f1_13[0]",foundedCompany.getCompanyName()+ "  " +
                foundedCompany.getCompanyAddress() + " " + foundedWorker.getCity()
                + " " + foundedWorker.getState() + " " + foundedWorker.getZipcode());


        fill(fields, "topmostSubform[0].Page1[0].f1_14[0]",foundedWorker.getCreatedDate().format(DATE_FMT));
        fill(fields, "topmostSubform[0].Page1[0].f1_15[0]", foundedCompany.getEmployerEIN());



// === Page 3: Multiple-Jobs Worksheet ===

// Line 1: только две работы (Step 2(c))
        String line1 = foundedWorker.getTwoJobsCheckBox()
                ? Optional.ofNullable(foundedWorker.getMultipleJobsAdditionalWithholding()).orElse(BigDecimal.ZERO).toString()
                : "0";
        fill(fields, "topmostSubform[0].Page3[0].f3_01[0]", line1);

// Line 2a–2b: если 3 и более работ
        String line2a = Optional.ofNullable(foundedWorker.getMultipleJobsWorksheetLine2a())
                .orElse(BigDecimal.ZERO).toString();
        String line2b = Optional.ofNullable(foundedWorker.getMultipleJobsWorksheetLine2b())
                .orElse(BigDecimal.ZERO).toString();
        fill(fields, "topmostSubform[0].Page3[0].f3_02[0]", line2a);
        fill(fields, "topmostSubform[0].Page3[0].f3_03[0]", line2b);

// Line 2c = 2a + 2b
        BigDecimal sum2c = Optional.ofNullable(foundedWorker.getMultipleJobsWorksheetLine2a()).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(foundedWorker.getMultipleJobsWorksheetLine2b()).orElse(BigDecimal.ZERO));
        fill(fields, "topmostSubform[0].Page3[0].f3_04[0]", sum2c.toString());

// Line 3: количество выплат в году
        int payPeriods = switch (foundedWorker.getPayFrequency()) {
            case WEEKLY   -> 52;
            case BIWEEKLY -> 26;
            case MONTHLY  -> 12;
        };
        fill(fields, "topmostSubform[0].Page3[0].f3_05[0]", String.valueOf(payPeriods));

// Line 4: делим Line 1 (или 2c) на payPeriods
        BigDecimal numerator = foundedWorker.getTwoJobsCheckBox()
                ? Optional.ofNullable(foundedWorker.getMultipleJobsAdditionalWithholding()).orElse(BigDecimal.ZERO)
                : sum2c;
        BigDecimal line4 = numerator.divide(BigDecimal.valueOf(payPeriods), 2, RoundingMode.HALF_UP);
        fill(fields, "topmostSubform[0].Page3[0].f3_06[0]", line4.toString());


// === Page 3: Deductions Worksheet ===

// Line 7 (f3_07): оценка itemized deductions
        String line7 = Optional.ofNullable(foundedWorker.getEstimatedItemizedDeductions())
                .orElse(BigDecimal.ZERO).toString();
        fill(fields, "topmostSubform[0].Page3[0].f3_07[0]", line7);

// Line 8 (f3_08): стандартные уровни по статусу
        BigDecimal stdLevel = switch (foundedWorker.getFilingStatus()) {
            case MARRIED_FILLING_JOINTLY -> new BigDecimal("30000");
            case HEAD_OF_HOUSEHOLD       -> new BigDecimal("22500");
            case SINGLE, MARRIED_FILLING_SEPARATELY -> new BigDecimal("15000");
        };
        fill(fields, "topmostSubform[0].Page3[0].f3_08[0]", stdLevel.toString());

// Line 9 (f3_09): разница = max(line7 − line8, 0)
        BigDecimal line9 = Optional.ofNullable(foundedWorker.getEstimatedItemizedDeductions()).orElse(BigDecimal.ZERO)
                .subtract(stdLevel).max(BigDecimal.ZERO);
        fill(fields, "topmostSubform[0].Page3[0].f3_09[0]", line9.toString());

// Line 10 (f3_10): adjustments из Schedule 1
        String line10 = Optional.ofNullable(foundedWorker.getAdjustmentsSchedule1())
                .orElse(BigDecimal.ZERO).toString();
        fill(fields, "topmostSubform[0].Page3[0].f3_10[0]", line10);

// Line 11 (f3_11): сумма для Step 4(b) = line9 + line10
        BigDecimal line11 = line9.add(Optional.ofNullable(foundedWorker.getAdjustmentsSchedule1()).orElse(BigDecimal.ZERO));
        fill(fields, "topmostSubform[0].Page3[0].f3_11[0]", line11.toString());


        form.flattenFields();
        pdfDoc.close();


        byte[] pdfBytes = baos.toByteArray();

        String companyKeyPart = foundedCompany.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]", "_");
        String workerKeyPart  = (foundedWorker.getFirstName() + "_" + foundedWorker.getLastName())
                .trim()
                .replaceAll("[^A-Za-z0-9_]", "");

        String key = String.format("%s/W4/%s.pdf", companyKeyPart, workerKeyPart);

        amazonS3Service.uploadPdfToS3(pdfBytes, key);
        return pdfBytes;
    }


    private void fill(Map<String, PdfFormField> fields, String name, String value) {
        PdfFormField field = fields.get(name);
        if (field == null) {
            System.err.println("⚠️ Field not found: " + name);
        } else {
            try {
                field.setValue(value);
            } catch (Exception e) {
                System.err.println("❌ Cannot set value for field: " + name + " | Reason: " + e.getMessage());
            }
        }
    }

}
