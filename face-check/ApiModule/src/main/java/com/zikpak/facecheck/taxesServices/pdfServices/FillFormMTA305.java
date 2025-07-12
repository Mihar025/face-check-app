package com.zikpak.facecheck.taxesServices.pdfServices;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j

                        /*
                        IMPORTANT!!!!
                        IF COMPANY PAYED SALARIES OVER 312500$ PER QUARTER WE FILLING THIS FORM!
                        IF TOTAL SUM OF SALARIES LESS THAN 312500 PER QUARTER I DONT GENERATING THIS FORM!
                         */
public class FillFormMTA305 {
    private final CompanyRepository companyRepository;
    private final WorkerPayrollRepository payrollRepo;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private final AmazonS3Service amazonS3Service;


    public byte[] generateFilledPdf(Integer companyId, int quarter, Integer year) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/assets/mta305_fill_in.pdf");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(inputStream),
                new PdfWriter(baos)
        );

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();


/*
        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }


 */
        LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate end   = start.plusMonths(3).minusDays(1);
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        BigDecimal line1a = workerPayrollRepository.sumGrossWages(company.getId(),start, end);
        if (line1a.compareTo(new BigDecimal("312500")) > 0) {

            // MTA-305 Form Fill Methods - все ключи с нумерацией

// 1-7. Company Information
            fill(fields, "legal name", company.getCompanyName());
            fill(fields, "address", company.getCompanyAddress());
            fill(fields, "city, village, post", company.getCompanyCity());
            fill(fields, "State", company.getCompanyState());
            fill(fields, "ZIP", company.getCompanyZipCode());
            //   fill(fields, "address change", "6");
            //  fill(fields, "amended", "7");

// 8-12. Quarter Selection

            //Check where this field working;
            //     fill(fields, "quarter", "Off");


            fill(fields, "quarter.1", "Off");
            fill(fields, "quarter.2", "Off");
            fill(fields, "quarter.3", "Off");
            fill(fields, "quarter.4", "Off");

            if (quarter == 1) {
                fill(fields, "quarter.1", "On");

            } else if (quarter == 2) {
                fill(fields, "quarter.2", "On");

            } else if (quarter == 3) {
                fill(fields, "quarter.3", "On");

            } else if (quarter == 4) {
                fill(fields, "quarter.4", "On");
            }


// 13-16. Tax Year and Identification
            fill(fields, "tax year", year.toString());
            fill(fields, "EIN", company.getEmployerEIN());

            Integer quantityOfWorkers = getEmployeeCountForQuarter(company.getId(), year, quarter);
            fill(fields, "number of employees Z1", quantityOfWorkers.toString());
            fill(fields, "number of employees", quantityOfWorkers.toString());
            //Maybe Later add to Worksite entity field city where all workers working, and add every day new records about punches in Worksite, and get this ciTY AND DEFINE INSTRUCTIONS FOR ZONE 2 CHECK AGAIN WHAT WRITTEN
            // fill(fields, "number of employees Z2", "51");


            fill(fields, "special condition code", company.getSpecialTwoCharConditionCodeForMTA305());
            //fill(fields, "date ceased wages", "16"); We filling this field only when we stop paying salaries!

// 17-22. Line Amounts (Dollars and Cents)


// 23-25. Line 6 Fields


// 26-33. Third Party Designee
        /*
       fill(fields, "3rd Party Discuss", "26");
        fill(fields, "3rd Party Discuss.1", "27");
        fill(fields, "3rd Party Discuss.2", "28");
        fill(fields, "Designee's Name", "29");
        fill(fields, "Des area code", "30");
        fill(fields, "Designee telephone number", "31");
        fill(fields, "Designee email", "32");
        fill(fields, "PIN", "33");

         */

// 34-44. Paid Preparer Information
        /*
        fill(fields, "datepaid", "34");
        fill(fields, "prep NYTPRIN", "35");
        fill(fields, "prep PTIN", "36");
        fill(fields, "paid ein", "37");
        fill(fields, "NYTPRIN excl code", "38");
        fill(fields, "firms name", "39");
        fill(fields, "paid addr 1", "40");
        fill(fields, "paid email", "41");
        fill(fields, "payroll service name", "42");
        fill(fields, "payroll EIN", "43");

         */

// 45-51. Taxpayer Information
            fill(fields, "print", company.getCompanyOwner().getFirstName() + " " + company.getCompanyOwner().getLastName());
            fill(fields, "title", "Owner");
            fill(fields, "dateyour", LocalDate.now().toString());

            String raw = company.getCompanyPhone();
            String digits = raw.replaceAll("\\D+", "");
            if (digits.length() == 11) {
                digits = digits.substring(1);
            }
            String areaCode = digits.substring(0, 3);
            String numberPart = digits.substring(3, 6)
                    + "-"
                    + digits.substring(6);

            fill(fields, "t-pay area code", areaCode);
            fill(fields, "phone number", numberPart);

            fill(fields, "email3", company.getCompanyEmail());


// 53-70. Line 1 and 2 Fields (Dollars and Cents)


            String[] grossParts = splitAmount(line1a);
            fill(fields, "dollars line 1", grossParts[0]);
            fill(fields, "cents line 1", grossParts[1]);

            //  fill(fields, "dollars line 1b", "55");
            //  fill(fields, "cents line 1b", "56");

            fill(fields, "dollars line 1c", grossParts[0]);
            fill(fields, "cents line 1c", grossParts[1]);

            BigDecimal rate = getZone1MctmtRate(end, line1a);
            BigDecimal line2a = line1a.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            String[] line2aParts = splitAmount(line2a);

            fill(fields, "dollars line 2", line2aParts[0]);
            fill(fields, "cents line 2", line2aParts[1]);

            BigDecimal line2b = BigDecimal.ZERO;
            String[] line2bParts = splitAmount(line2b);
            fill(fields, "dollars line 2b", line2bParts[0]);
            fill(fields, "cents line 2b", line2bParts[1]);


            BigDecimal line2c = line2a.add(line2b);
            String[] line2cParts = splitAmount(line2c);
            fill(fields, "dollars line 2c", line2cParts[0]);
            fill(fields, "cents line 2c", line2cParts[1]);

            BigDecimal line3 = paymentHistoryIrsRepository.getTotalMctmtPrepaymentsAndCredits(company.getId(), year, quarter);
            String[] line3Parts = splitAmount(line3);
            fill(fields, "dollars line 3", line3Parts[0]);
            fill(fields, "cents line 3", line3Parts[1]);

            if (line2c.compareTo(line3) > 0) {
                BigDecimal line4 = line2c.subtract(line3).setScale(2, RoundingMode.HALF_UP);
                String[] line4Parts = splitAmount(line4);
                fill(fields, "dollars line 4", line4Parts[0]);
                fill(fields, "cents line 4", line4Parts[1]);
                fill(fields, "dollars line 5", " ");
                fill(fields, "cents line 5", " ");
            } else if (line2c.compareTo(line3) < 0) {
                BigDecimal line5 = line3.subtract(line2c).setScale(2, RoundingMode.HALF_UP);
                String[] line5Parts = splitAmount(line5);
                fill(fields, "dollars line 5", line5Parts[0]);
                fill(fields, "cents line 5", line5Parts[1]);
                //  fill(fields, "line 6", "23");
                fill(fields, "line 6.1", "On");
                fill(fields, "line 6.2", "Off");
            }


            pdfDoc.close();



            byte[] pdfBytes = baos.toByteArray();
            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("MTA305_%d_%d_%d.pdf",
                    companyId,
                    year,
                    quarter);

            String key = String.format("%s/%d/MTA305/%d/%d/%s",
                    companyKeyPart,
                    companyId,
                    year,
                    quarter,
                    fileName);


            amazonS3Service.uploadPdfToS3(pdfBytes, key);
            log.info("✅ Form MTA-305 uploaded to S3: {}", key);
            return pdfBytes;
        }
        else{
            throw new RuntimeException("Company with id: " + company.getId() + " should not making this form!");
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

    private Integer getEmployeeCountForQuarter(Integer companyId, int year, int quarter) {
        // Вычисляем даты квартала
        LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

        // Получаем все payroll записи за квартал
        List<WorkerPayroll> quarterPayrolls = payrollRepo
                .findAllByCompanyIdAndPeriodBetween(companyId, quarterStart, quarterEnd);

        // Собираем уникальных работников, у которых есть payroll за квартал
        Set<Integer> uniqueEmployees = quarterPayrolls.stream()
                .map(payroll -> payroll.getWorker().getId())
                .collect(Collectors.toSet());

        return uniqueEmployees.size();
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


    private BigDecimal getZone1MctmtRate(LocalDate quarterEnd, BigDecimal sumGross) {
        BigDecimal threshold1 = new BigDecimal("375000.00");
        BigDecimal threshold2 = new BigDecimal("437500.00");
        // дата переключения таблицы ставок
        LocalDate julyFirst = LocalDate.of(quarterEnd.getYear(), 7, 1);

        if (quarterEnd.isBefore(julyFirst)) {
            // === Table 1a (до 1 июля включительно) ===
            if (sumGross.compareTo(threshold1) <= 0) {
                // sumGross <= 375 000
                return new BigDecimal("0.0011");   // 0.11 %
            } else if (sumGross.compareTo(threshold2) <= 0) {
                // 375 000 < sumGross <= 437 500
                return new BigDecimal("0.0023");   // 0.23 %
            } else {
                // sumGross > 437 500
                return new BigDecimal("0.0060");   // 0.60 %
            }
        } else {
            // === Table 1b (после 1 июля) ===
            if (sumGross.compareTo(threshold1) <= 0) {
                // sumGross <= 375 000
                return new BigDecimal("0.00055");  // 0.055 %
            } else if (sumGross.compareTo(threshold2) <= 0) {
                // 375 000 < sumGross <= 437 500
                return new BigDecimal("0.00115");  // 0.115 %
            } else {
                // sumGross > 437 500
                return new BigDecimal("0.0060");   // 0.60 %  (можно расширить до 2 500 000 и выше  слоем 0.00895)
            }
        }
    }

}
