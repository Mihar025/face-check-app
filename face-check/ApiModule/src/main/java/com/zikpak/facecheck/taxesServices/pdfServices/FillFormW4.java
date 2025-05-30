package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.Dependents;
import com.zikpak.facecheck.entity.W4.TaxRates;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DependentRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.itextpdf.kernel.pdf.PdfName.User;

@Service
@RequiredArgsConstructor
public class FillFormW4 {
    private final UserRepository userRepository;
    private final DependentRepository dependentRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    public void generateW4Pdf(Integer userId, Integer companyId) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/forms/fw4.pdf";
        String dest = "filled_fW4_output.pdf";

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
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





        //Check box Step 2! (C)
        fill(fields, "topmostSubform[0].Page1[0].c1_2[0]","12" );
        //Garbage not usable
      //  fill(fields, "topmostSubform[0].Page1[0].Step3_ReadOrder[0]","13" );




        BigDecimal child = deps.stream()
                .filter(d -> Period.between(d.getBirthDate(), LocalDate.now()).getYears() < 17)
                .map(d -> new BigDecimal("2000"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dependent = deps.stream()
                .filter(d -> Period.between(d.getBirthDate(), LocalDate.now()).getYears() >= 17)
                .map(d -> new BigDecimal("500"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        fill(fields, "topmostSubform[0].Page1[0].Step3_ReadOrder[0].f1_06[0]", child.toString());

        fill(fields, "topmostSubform[0].Page1[0].Step3_ReadOrder[0].f1_07[0]",dependent.toString());
        BigDecimal finalSum = child.add(dependent);

        fill(fields, "topmostSubform[0].Page1[0].f1_09[0]", finalSum.toString());


            //Worker will fill separately!
       // fill(fields, "topmostSubform[0].Page1[0].f1_10[0]","17" );
       // fill(fields, "topmostSubform[0].Page1[0].f1_11[0]","18" );


        fill(fields, "topmostSubform[0].Page1[0].f1_12[0]", foundedWorker.getExtraWithHoldings().toString());

        fill(fields, "topmostSubform[0].Page1[0].f1_13[0]",foundedCompany.getCompanyName()+ "  " +
                foundedCompany.getCompanyAddress() + " " + foundedWorker.getCity()
                + " " + foundedWorker.getState() + " " + foundedWorker.getZipcode());


        fill(fields, "topmostSubform[0].Page1[0].f1_14[0]",foundedWorker.getCreatedDate().format(DATE_FMT));
        fill(fields, "topmostSubform[0].Page1[0].f1_15[0]", foundedCompany.getEmployerEIN());
       // fill(fields, "topmostSubform[0].Page3[0]", "23");

        /*
        fill(fields, "topmostSubform[0].Page3[0].f3_01[0]", "24");
        fill(fields, "topmostSubform[0].Page3[0].f3_02[0]","25" );
        fill(fields, "topmostSubform[0].Page3[0].f3_03[0]","26");
        fill(fields, "topmostSubform[0].Page3[0].f3_04[0]","27");
        fill(fields, "topmostSubform[0].Page3[0].f3_05[0]", "28");
        fill(fields, "topmostSubform[0].Page3[0].f3_06[0]", "29");
        fill(fields, "topmostSubform[0].Page3[0].f3_07[0]","30" );
        fill(fields, "topmostSubform[0].Page3[0].f3_08[0]","31");
        fill(fields, "topmostSubform[0].Page3[0].f3_09[0]", "32");
        fill(fields, "topmostSubform[0].Page3[0].f3_10[0]", "33");
        fill(fields, "topmostSubform[0].Page3[0].f3_11[0]", "34");
         */





        form.flattenFields();
        pdfDoc.close();

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
