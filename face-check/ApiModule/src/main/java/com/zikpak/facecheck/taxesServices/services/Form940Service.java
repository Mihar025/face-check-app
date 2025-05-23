package com.zikpak.facecheck.taxesServices.services;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Form940Service {

    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;

    public byte[] generateForm940(Integer companyId, int year) throws IOException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        // 1) Кол-во сотрудников
        int numEmployees = taxRecordRepo.countDistinctEmployeesForYear(companyId, year);

        // 2) Сумма FUTA-зарплат и налог
        BigDecimal totalFutaWages = taxRecordRepo.sumFutaWagesForYear(companyId, year);
        BigDecimal totalFutaTax = totalFutaWages
                .multiply(BigDecimal.valueOf(0.006))
                .setScale(2, RoundingMode.HALF_UP);

        try (InputStream templateStream = getClass().getResourceAsStream("/forms/f940.pdf")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Создаем iText PDF документ
            PdfReader reader = new PdfReader(templateStream);
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(reader, writer);

            // Получаем форму
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

            // Выводим все поля для отладки
            Map<String, PdfFormField> fields = form.getFormFields();
            System.out.println("---- iText PDF fields: ----");
            for (String fieldName : fields.keySet()) {
                System.out.println(fieldName);
            }
            System.out.println("--------------------------");

            // Заполняем поля
            // Имя компании
            if (fields.containsKey("topmostSubform[0].Page1[0].f1_16[0]")) {
                fields.get("topmostSubform[0].Page1[0].f1_16[0]")
                        .setValue(company.getCompanyName());
                System.out.println("Company name set: " + company.getCompanyName());
            }

            // EIN
            if (fields.containsKey("topmostSubform[0].Page1[0].EntityArea[0].f1_3[0]")) {
                fields.get("topmostSubform[0].Page1[0].EntityArea[0].f1_3[0]")
                        .setValue(company.getEmployerEIN());
                System.out.println("EIN set: " + company.getEmployerEIN());
            }

            // No Payments checkbox
            String noPaymentsName = "topmostSubform[0].Page1[0].TypeReturn[0].c1_3[0]";
            if (fields.containsKey(noPaymentsName)) {
                fields.get(noPaymentsName)
                        .setValue(numEmployees == 0 ? "1" : "Off");
                System.out.println("No payments checkbox set: " + (numEmployees == 0 ? "1" : "Off"));
            }

            // Установим необходимость регенерации внешнего вида полей
            form.setNeedAppearances(true);

            // Создаем внешний вид полей перед заморозкой
            //  form.regenerateField(fields.get("topmostSubform[0].Page1[0].f1_16[0]"));
            //  form.regenerateField(fields.get("topmostSubform[0].Page1[0].EntityArea[0].f1_3[0]"));

            // Запекаем форму (этот шаг делает поля неизменяемыми, но видимыми)
            form.flattenFields();

            // Закрываем документ и возвращаем байты
            pdf.close();

            System.out.println("PDF generated, size: " + baos.size() + " bytes");
            return baos.toByteArray();
        }
    }
}