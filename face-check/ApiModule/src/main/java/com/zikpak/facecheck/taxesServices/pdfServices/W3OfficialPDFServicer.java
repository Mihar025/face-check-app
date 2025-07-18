package com.zikpak.facecheck.taxesServices.pdfServices;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.YearToDateForWorkerResponse;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.services.CompanyPayrollService;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class W3OfficialPDFServicer {


    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AmazonS3Service amazonS3Service;
    private final WorkerPayRollService workerPayRollService;
    private final MetricsForPdfServices metric;

    public byte[] generateFilledPdf( Integer companyId, int year) throws IOException {
        final String FORM = "W3";
        metric.recordRequest(FORM);
        Timer.Sample timer = metric.startTimer();

        try {
            InputStream inputStream = getClass().getResourceAsStream("/assets/w-3.pdf");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(
                    new PdfReader(inputStream),
                    new PdfWriter(baos)
            );

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));


/*
        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }

 */
            List<User> workers = userRepository.findWorkersWithPayrollInYear(companyId, year);
            BigDecimal totalWages = BigDecimal.ZERO;
            BigDecimal totalFederalIncomeTax = BigDecimal.ZERO;
            BigDecimal totalSocialSecurityWages = BigDecimal.ZERO;
            BigDecimal totalSocialSecurityTax = BigDecimal.ZERO;
            BigDecimal totalMedicareWages = BigDecimal.ZERO;
            BigDecimal totalMedicareTax = BigDecimal.ZERO;
            BigDecimal totalLocalWages = BigDecimal.ZERO;
            BigDecimal totalStateIncomeTax = BigDecimal.ZERO;
            BigDecimal totalInsuranceBox12 = BigDecimal.ZERO;


            WorkerYearlySummaryDto workerSummary = workerPayRollService.calculateWorkerYearlyTotalsForAllWorkers(companyId, year);
            totalWages = totalWages.add(workerSummary.getGrossPayTotal());
            totalFederalIncomeTax = totalFederalIncomeTax.add(workerSummary.getFederalWithholdingTotal());
            totalSocialSecurityWages = totalSocialSecurityWages.add(workerSummary.getGrossPayTotal());
            totalSocialSecurityTax = totalSocialSecurityTax.add(workerSummary.getSocialSecurityEmployeeTotal());
            totalMedicareWages = totalMedicareWages.add(workerSummary.getGrossPayTotal());
            totalMedicareTax = totalMedicareTax.add(workerSummary.getMedicareTotal());
            totalLocalWages = totalLocalWages.add(workerSummary.getNyLocalWithholdingTotal());
            totalStateIncomeTax = totalStateIncomeTax.add(workerSummary.getNyStateWithholdingTotal());
            Integer totalEmployees = workers.size();

            for (User user : workers) {
                if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                        && user.getMonthlyHealthPremium() != null
                        && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal annualPremium = user.getMonthlyHealthPremium()
                            .multiply(BigDecimal.valueOf(12))
                            .setScale(2, RoundingMode.HALF_UP);
                    totalInsuranceBox12 = totalInsuranceBox12.add(annualPremium);
                }
            }


            fill(fields, "Check Box1", "1");
            // fill(fields, "Check Box2", "2");
            // fill(fields, "Check Box3", "3");
            //    fill(fields, "Check Box4", "4");
            //fill(fields, "Check Box5", "5");
            // fill(fields, "Check Box6", "6");
            //   fill(fields, "Check Box7", "7");
            fill(fields, "Check Box8", "8");
            // fill(fields, "Check Box9", "9");
            //  fill(fields, "Check Box10", "10");
            //  fill(fields, "Check Box11", "11");
            //  fill(fields, "Check Box12", "12");
            //  fill(fields, "Check Box13", "13");

            fill(fields, "Text2", totalEmployees.toString());
            fill(fields, "Text3", " ");
            fill(fields, "Text4", totalWages.toString()); // Wages, tips, other compensation
            fill(fields, "Text5", totalFederalIncomeTax.toString()); // Fedederal tax withheld
            fill(fields, "Text6", totalSocialSecurityTax.toString()); // Social security tax withheld
            fill(fields, "Text7", totalSocialSecurityWages.toString()); //Social security wages
            fill(fields, "Text8", company.getEmployerEIN());
            fill(fields, "Text9", company.getCompanyName());
            fill(fields, "Text10", totalMedicareWages.toString()); //Medicare wages and tips
            fill(fields, "Text11", totalMedicareTax.toString()); // Medicare tax withheld
            //fill(fields, "Text12", "12");
            //fill(fields, "Text13", "13");
            //fill(fields, "Text14", "14");
            // fill(fields, "Text15", "15");
            // fill(fields, "Text16", "16");
            //    fill(fields, "Text17", "17"); //Dependent care benefits?


            fill(fields, "Text18", totalInsuranceBox12.toString()); // Only insurance offering companies! Not 401k etc! Add method from W2 ! Sum of all employees!


            fill(fields, "Text19", company.getCompanyStateIdNumber());
            fill(fields, "Text20", totalStateIncomeTax.toString()); // State income tax
            fill(fields, "Text21", company.getCompanyOwner().getPhoneNumber());
            //fill(fields, "Text22", "22");
            fill(fields, "Text23", company.getCompanyEmail());
            // fill(fields, "Text24", "24");
            fill(fields, "Text25", company.getCompanyPhone());
            fill(fields, "Text26", totalWages.toString());
            fill(fields, "Text27", totalLocalWages.toString());
            //fill(fields, "Text28", "28");
            fill(fields, "Text29", "Owner");
            fill(fields, "Text30", company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());
            fill(fields, "Text31", totalWages.toString()); //State wages tips etc
            // fill(fields, "Digital Signature1", "31");
            String pattern = "MM/dd/yyyy"; // Example: Day/Month/Year
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            fill(fields, "Date Field1", formatter.format(LocalDate.now()));


            pdfDoc.close();

            byte[] pdfBytes = baos.toByteArray();

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName = String.format("w3_%d_%s.pdf",
                    companyId,
                    datePart);

            String key = String.format("%s/%d/W3Official/%s/%s",
                    companyKeyPart,
                    companyId,
                    datePart,
                    fileName);

            long ms = System.currentTimeMillis();
            amazonS3Service.uploadPdfToS3(pdfBytes, key);
            long end = System.currentTimeMillis() - ms;

            log.info("✅ Form W3 uploaded to S3: {}", key);



            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end);
            metric.recordOperationTime(timer,"W3_success");

            return pdfBytes;
        } catch (Exception e) {
            metric.recordOperationTime(timer,"W3_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("W3_failed", e.getMessage(), e);
            throw e;
        }
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


private BigDecimal safe(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
}
}