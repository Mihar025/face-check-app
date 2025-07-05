package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.DocumentsI9;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DocumentsI9Repository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class FillFormI9 {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final DocumentsI9Repository documentsI9Repository;


    public byte[] generateFilledPdf(Integer userId, Integer companyId) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/7407199f-c591-4d4e-a00c-d9f46d4fffae_i-9.pdf";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(src),
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
        fill(fields, "Employee Middle Initial (if any)", user.getMiddleInitial());
        fill(fields, "Last Name (Family Name)", user.getLastName());
        fill(fields, "Employee Other Last Names Used (if any)", " ");
        fill(fields, "Address Street Number and Name", user.getHomeAddress());
        fill(fields, "Apt Number (if any)", user.getApt());
        fill(fields, "City or Town", user.getCity());
        fill(fields, "State", user.getState());
        fill(fields, "ZIP Code", user.getZipcode());
        fill(fields, "Telephone Number", user.getPhoneNumber());
        fill(fields, "US Social Security Number", user.getSSN_WORKER() );
        fill(fields, "Date Field1",formatter.format(user.getDateOfBirth()));
        fill(fields, "Employees E-mail Address", user.getEmail());


        fill(fields, "CB_1", "Off");
        fill(fields, "CB_2", "Off");
        fill(fields, "CB_3", "Off");
        fill(fields, "CB_4", "Off");

        //A citizen of United States
        if(user.getIsCitizen()){
            fill(fields, "CB_1", "On");
        }

        //A noncitizen national of the US
        if(user.getIsNonCitizenNationalOfTheUS()) {
            fill(fields, "CB_2", "On");
        }


        //Answer for number 3, like checkbox CB_2
        if(user.getIsPermanentResident()){
            fill(fields, "CB_3", "On");
            fill(fields, "3 A lawful permanent resident Enter USCIS or ANumber", user.getUscisNumber());
        }

        if(user.getIsANonCitizen()){
            fill(fields, "CB_4", "On");
            fill(fields, "Exp Date mmddyyyy", user.getWorkAuthrizationExpiryDate().toString());
            if(!user.getUscisNumber().equals(null)){
                fill(fields, "USCIS ANumber", user.getUscisNumber());
            }
            if(!user.getFormI94AdmissionNumber().equals(null)){
                fill(fields, "Form I94 Admission Number", user.getFormI94AdmissionNumber());
            }
            if(user.getPassportNumber() != null && user.getPassportCountryOfIssuance() != null){
                fill(fields, "Foreign Passport Number and Country of IssuanceRow1", user.getPassportNumber() + " " + user.getPassportCountryOfIssuance());
            }
        }




        fill(fields, "Date Field4", formatter.format(LocalDate.now()));
        fill(fields, "Date Field2", formatter.format(LocalDate.now()));

        fill(fields, "Date Field3", formatter.format(user.getHireDate()));
        fill(fields, "Last Name First Name and Title of Employer or Authorized Representative", company.getCompanyOwner().getFirstName() + " " + company.getCompanyOwner().getLastName());
        fill(fields, "Employers Business or Org Name", company.getCompanyName());
        fill(fields, "Employers Business or Org Address", company.getCompanyAddress() + " " + company.getCompanyCity() + " " + company.getCompanyState() + " " +company.getCompanyZipCode());

        List<DocumentsI9> documents = documentsI9Repository.findAllDocumentsByUserId(userId);


// Массивы с точными названиями полей из PDF (ровно в том виде, как они лежат в XFA/AcroForm)
        String[] titleFields = {
                "Document Title 1",
                "Document Title 2 If any",
                "  If any_val1"          // обратите внимание на два пробела в начале
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

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMddyyyy");

        for (int i = 0; i < documents.size() && i < 3; i++) {
            DocumentsI9 doc = documents.get(i);
            // название поля с датой — просто подставляем номер: Field5, Field6, Field7
            String dateField = "Date Field" + (5 + i);

            fill(fields, titleFields[i],       doc.getDocumentTitle());
            fill(fields, authFields[i],        doc.getIssuingAuthority());
            fill(fields, numFields[i],         doc.getDocumentNumber());
            fill(fields, dateField,            doc.getExpirationDate().format(fmt));
        }


        fill(fields, "List B Document 1 Title", "13");
        fill(fields, "List C Document Title 1", "14");
        fill(fields, "List B Issuing Authority 1", "15");
        fill(fields, "List C Issuing Authority 1", "16");
        fill(fields, "List B Document Number 1", "17");
        fill(fields, "List C Document Number 1", "18");
        fill(fields, "List B Expiration Date 1", "19");
        fill(fields, "List C Expiration Date 1", "20");



        fill(fields, "CB_Alt", "41");


        //Additional information
       // fill(fields, "Additional Information", "35");






       if(user.getIsRehired() == true){
            fill(fields, "Last Name Family Name from Section 1-2", user.getLastName());
            fill(fields, "First Name Given Name from Section 1-2", user.getFirstName());
            fill(fields, "Middle initial if any from Section 1-2",user.getMiddleInitial());

         //   fill(fields, "Date_9", formatter.format(user.getDateWhenRehired()));
            fill(fields, "Last Name 0", user.getLastName());
            fill(fields, "First Name 0", user.getFirstName());
            fill(fields, "Middle Initial 0", user.getMiddleInitial());
            fill(fields, "Name of Emp or Auth Rep 0", "1");
           fill(fields, "Name of Emp or Auth Rep 0", company.getCompanyOwner().getFirstName());
           fill(fields, "Addtl Info 0", "");
           //   fill(fields, "CB_Alt_0", "6");


           fill(fields, "Date Field14", "7");
           fill(fields, "Last Name 1",user.getLastName());
           fill(fields, "First Name 1", user.getFirstName());
           fill(fields, "Middle Initial 1", user.getMiddleInitial());

           fill(fields, "Name of Emp or Auth Rep 1",company.getCompanyOwner().getFirstName());
           fill(fields, "Date Field14", formatter.format(user.getDateWhenRehired()));
           //   fill(fields, "Addtl Info 1", "16");
           //   fill(fields, "CB_Alt_1", "17");



           fill(fields, "Date Field18",  formatter.format(user.getDateWhenRehired()));
           fill(fields, "Last Name 2", user.getLastName());
           fill(fields, "First Name 2", user.getFirstName());
           fill(fields, "Middle Initial 2", user.getMiddleInitial());

           fill(fields, "Name of Emp or Auth Rep 2", company.getCompanyOwner().getFirstName());
           //Todays Date
           fill(fields, "Date Field19", "26");
           // fill(fields, "Addtl Info 2", "27");
           //  fill(fields, "CB_Alt_2", "28");
           List<DocumentsI9> documents1 = documentsI9Repository.findAllDocumentsByUserId(userId);

           String[] titleFields1 = { "Document Title 1", "Document Title 2 If any", "  If any_val1" };
           String[] authFields1  = { "Issuing Authority 1", "Issuing Authority_2", "  Enter Issuing Authority" };
           String[] numFields1  = { "Document Number 0 (if any)", "Document Number If any_2", "  If any_val2" };
           String[] dateFields1  = { "Date Field5", "Date Field6", "Date Field7" };

           DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("MMddyyyy");

           for (int i = 0; i < documents1.size() && i < 3; i++) {
               DocumentsI9 doc = documents.get(i);
               String title = doc.getDocumentTitle()    != null ? doc.getDocumentTitle() : "";
               String auth  = doc.getIssuingAuthority() != null ? doc.getIssuingAuthority() : "";
               String num   = doc.getDocumentNumber()   != null ? doc.getDocumentNumber() : "";
               String date  = doc.getExpirationDate()   != null
                       ? doc.getExpirationDate().format(fmt)
                       : "";

               fill(fields, titleFields1[i], title);
               fill(fields, authFields1[i],  auth);
               fill(fields, numFields1[i],   num);
               fill(fields, dateFields1[i],  date);
           }

       }
       else {
           System.out.println("Hello");
       }







        form.flattenFields();
        pdfDoc.removePage(2);
       // pdfDoc.removePage(1);
        pdfDoc.close();
        return baos.toByteArray();
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

    private String splitAmount(int amount) {
        return new String();
    }



    private String spacedDigits(String value) {
        return value.chars()
                .mapToObj(c -> (char) c + "        ") // 7 пробелов
                .collect(Collectors.joining());

    }
}
