package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.DocumentsI9;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DocumentsI9Repository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;



@Service
@RequiredArgsConstructor
@Slf4j
public class FillFormI9 {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final DocumentsI9Repository documentsI9Repository;
    private final AmazonS3Service amazonS3Service;


    public byte[] generateFilledPdf(Integer userId, Integer companyId) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/forms/7407199f-c591-4d4e-a00c-d9f46d4fffae_i-9.pdf");
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



        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        fill(fields, "First Name Given Name", user.getFirstName());
        if(user.getMiddleInitial() != null) {
            fill(fields, "Employee Middle Initial (if any)", user.getMiddleInitial());
        }
        else{
            fill(fields, "Employee Middle Initial (if any)", " ");

        }
        fill(fields, "Last Name (Family Name)", user.getLastName());
        fill(fields, "Employee Other Last Names Used (if any)", " ");
        fill(fields, "Address Street Number and Name", user.getHomeAddress());
        fill(fields, "Apt Number (if any)", user.getApt());
        fill(fields, "City or Town", user.getCity());
        fill(fields, "State", user.getState());
        fill(fields, "ZIP Code", user.getZipcode());
        fill(fields, "Telephone Number", user.getPhoneNumber());
        fill(fields, "US Social Security Number", formatSSNForCells(user.getSSN_WORKER()));
        fill(fields, "Date Field1",formatter.format(user.getDateOfBirth()));
        fill(fields, "Employees E-mail Address", user.getEmail());

        fill(fields, "CB_1", "Off");
        fill(fields, "CB_2", "Off");
        fill(fields, "CB_3", "Off");
        fill(fields, "CB_4", "Off");

        if(user.getIsCitizen()){
            fill(fields, "CB_1", "On");
        }

        if(user.getIsNonCitizenNationalOfTheUS()) {
            fill(fields, "CB_2", "On");
        }


        if(user.getIsPermanentResident()){
            fill(fields, "CB_3", "On");
            fill(fields, "3 A lawful permanent resident Enter USCIS or ANumber", user.getUscisNumber());
        }

        if(user.getIsANonCitizen()){
            fill(fields, "CB_4", "On");

            if (user.getWorkAuthrizationExpiryDate() != null) {
                fill(fields, "Exp Date mmddyyyy", formatter.format(user.getWorkAuthrizationExpiryDate()));
            }
            if(user.getUscisNumber() != null){
                fill(fields, "USCIS ANumber", user.getUscisNumber());
            }
            if(user.getFormI94AdmissionNumber() != null){
                fill(fields, "Form I94 Admission Number", user.getFormI94AdmissionNumber());
            }
            if(user.getPassportNumber() != null && user.getPassportCountryOfIssuance() != null){
                fill(fields, "Foreign Passport Number and Country of IssuanceRow1", user.getPassportNumber() + " " + user.getPassportCountryOfIssuance());
            }
        }
        fill(fields, "Date Field4", formatter.format(LocalDate.now()));
        if (user.getHireDate() != null) {
            fill(fields, "Date Field3", formatter.format(user.getHireDate()));
        }
        fill(fields, "Date Field2", formatter.format(LocalDate.now()));






        fill(fields, "Last Name First Name and Title of Employer or Authorized Representative", company.getCompanyOwner().getFirstName() + " " + company.getCompanyOwner().getLastName());
       fill(fields, "Employers Business or Org Name", company.getCompanyName());
        fill(fields, "Employers Business or Org Address", company.getCompanyAddress() + " " + company.getCompanyCity() + " " + company.getCompanyState() + " " +company.getCompanyZipCode());

        List<DocumentsI9> documents = documentsI9Repository.findAllDocumentsByUserId(userId);


        String[] titleFields = {
                "Document Title 1.1",
                "Document Title 2 If any",
                "  If any_val1"
        };
        String[] authFields = {
                "Issuing Authority 1",
                "Issuing Authority_2",
                "  Enter Issuing Authority"
        };
        String[] numFields = {
                "Document Number 0 (if any)",
                "Document Number If any_2",
                "  If any_val2"
        };

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (int i = 0; i < documents.size() && i < 3; i++) {
            DocumentsI9 doc = documents.get(i);
            String dateField = "Date Field" + (5 + i);

            fill(fields, titleFields[i],       doc.getDocumentTitle());
            fill(fields, authFields[i],        doc.getIssuingAuthority());
            fill(fields, numFields[i],         doc.getDocumentNumber());

            //fill(fields, dateField,            doc.getExpirationDate().format(fmt));
            if (doc.getExpirationDate() != null) {
                fill(fields, dateField, doc.getExpirationDate().format(fmt));
            }
        }



        if (Boolean.TRUE.equals(user.getIsRehired())) {
            fill(fields, "Last Name Family Name from Section 1-2", user.getLastName());
            fill(fields, "First Name Given Name from Section 1-2", user.getFirstName());
            if(user.getMiddleInitial() != null) {
                fill(fields, "Middle initial if any from Section 1-2", user.getMiddleInitial());
            }
            else{
                fill(fields, "Middle initial if any from Section 1-2", " ");
            }

            fill(fields, "Last Name 0", user.getLastName());
            fill(fields, "First Name 0", user.getFirstName());
            if(user.getMiddleInitial() != null) {
                fill(fields, "Middle Initial 0", user.getMiddleInitial());
            }
            else{
                fill(fields, "Middle Initial 0", " ");
            }
           fill(fields, "Name of Emp or Auth Rep 0", company.getCompanyOwner().getFirstName());
           fill(fields, "Addtl Info 0", "");


           fill(fields, "Last Name 1",user.getLastName());
           fill(fields, "First Name 1", user.getFirstName());
            if(user.getMiddleInitial() != null) {
                fill(fields, "Middle Initial 1", user.getMiddleInitial());
            }
            else{
                fill(fields, "Middle Initial 1", " ");
            }
           fill(fields, "Name of Emp or Auth Rep 1",company.getCompanyOwner().getFirstName());
            if(user.getDateWhenRehired() != null) {
                fill(fields, "Date_9", formatter.format(user.getDateWhenRehired()));
                fill(fields, "Date Field14", formatter.format(user.getDateWhenRehired()));
                fill(fields, "Date Field18", formatter.format(user.getDateWhenRehired()));
            }
           fill(fields, "Last Name 2", user.getLastName());
           fill(fields, "First Name 2", user.getFirstName());
            if(user.getMiddleInitial() != null) {
                fill(fields, "Middle Initial 2", user.getMiddleInitial());
            }
            else{
                fill(fields, "Middle Initial 2", " ");

            }
           fill(fields, "Name of Emp or Auth Rep 2", company.getCompanyOwner().getFirstName());

            fill(fields, "Date Field19", formatter.format(LocalDate.now()));
            fill(fields, "Date Field16", formatter.format(LocalDate.now()));
            fill(fields, "Date Field13", formatter.format(LocalDate.now()));

           List<DocumentsI9> documents1 = documentsI9Repository.findAllDocumentsByUserId(userId);

           // Правильные массивы полей
           String[] titleFields1 = {
                   "Document Title 0",      // Документ 1
                   "Document Title 1.2",    // Документ 2
                   "Document Title 2"       // Документ 3
           };

           String[] numFields1 = {
                   "Document Number 0",     // Документ 1
                   "Document Number 1",     // Документ 2
                   "Document Number 2"      // Документ 3
           };

           String[] dateFields1 = {
                   "Date Field12",           // Документ 1 (уточните, если нужно другое)
                   "Date Field15",          // Документ 2
                   "Date Field17"           // Документ 3
           };



           DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

           for (int i = 0; i < documents1.size() && i < 3; i++) {
               DocumentsI9 doc = documents1.get(i);

               String title = doc.getDocumentTitle() != null ? doc.getDocumentTitle() : "";
               String num = doc.getDocumentNumber() != null ? doc.getDocumentNumber() : "";
               String date = doc.getExpirationDate() != null
                       ? doc.getExpirationDate().format(fmt1)
                       : "";

               fill(fields, titleFields1[i], title);
               fill(fields, numFields1[i], num);

               // Заполняем дату только для документов 2 и 3
              // if (i > 0 || dateFields1[i].equals("Date Field5")) {
                   fill(fields, dateFields1[i], date);
             //  }
           }
       }
       else {
           System.out.println("Hello");
       }



     //  form.flattenFields();
        pdfDoc.close();

        byte[] pdfBytes = baos.toByteArray();

        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String workerKeyPart = String.format("%s_%s",
                user.getFirstName().trim().replaceAll("\\s+", "_"),
                user.getLastName().trim().replaceAll("\\s+", "_"));

// 2. Имя файла (можете скорректировать по своему вкусу)
        String fileName = String.format("I9_%d_%s_%s.pdf",
                companyId,
                user.getFirstName().toLowerCase(),
                user.getLastName().toLowerCase());

// 3. Собираем полный ключ
        String key = String.format("%s/%d/I9Form/%s/%s",
                companyKeyPart,
                companyId,
                workerKeyPart,
                fileName);

// 4. Загружаем в S3
        amazonS3Service.uploadPdfToS3(pdfBytes, key);
        log.info("✅ Form I-9 uploaded to S3: {}", key);

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

    private String formatSSNForCells(String ssn) {
        if (ssn == null || ssn.isEmpty()) return "";

        String cleanSSN = ssn.replaceAll("[^0-9]", "");
        if (cleanSSN.length() != 9) return ssn;

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cleanSSN.length(); i++) {
            formatted.append(cleanSSN.charAt(i));
            if (i < cleanSSN.length() - 1) {
                 formatted.append("  \u200A"); // 2 обычных + волосяной пробел
            }
        }
        return formatted.toString();
    }
}
