package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FillForm1095C {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;

    public void generateFilledPdf(Integer companyId, Integer workerId, int reportingYear) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f1095c.pdf";
        String dest = "filled_f1095C_output.pdf";

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
        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        var foundedUser = userRepository.findById(workerId)
                        .orElseThrow(() -> new RuntimeException("User not found"));


      //  fill(fields, "topmostSubform[0]", "1");
      //  fill(fields, "topmostSubform[0].Page1[0]", "2");
     //   fill(fields, "topmostSubform[0].Page1[0].PgHeader[0]", "3");

        //void
        fill(fields, "topmostSubform[0].Page1[0].PgHeader[0].c1_1[0]", "4");
        //corrected
        fill(fields, "topmostSubform[0].Page1[0].PgHeader[0].c1_1[1]", "5");


      //  fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0]", "6");

        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_1[0]", foundedUser.getFirstName());
            if(foundedUser.getMiddleInitial() != null) {
                fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_2[0]", foundedUser.getMiddleInitial());
            }
            else {
                fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_2[0]", " ");

            }
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_3[0]", foundedUser.getLastName());
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_4[0]", foundedUser.getSSN_WORKER());
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_5[0]", foundedUser.getHomeAddress());
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_6[0]", foundedUser.getCity());
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_7[0]", foundedUser.getState());
        fill(fields, "topmostSubform[0].Page1[0].EmployeeName[0].f1_8[0]", foundedUser.getZipcode());

      //  fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0]", "15");


        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_9[0]", foundedCompany.getCompanyName());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_10[0]", foundedCompany.getEmployerEIN());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_11[0]", foundedCompany.getCompanyAddress());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_12[0]", foundedCompany.getCompanyPhone());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_13[0]", foundedCompany.getCompanyCity());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_14[0]", foundedCompany.getCompanyState());
        fill(fields, "topmostSubform[0].Page1[0].EmployerIssuer[0].f1_15[0]", foundedCompany.getCompanyZipCode());
     //   fill(fields, "topmostSubform[0].Page1[0].PartII[0]", "23");

        int age = LocalDate.now().getYear() - foundedUser.getDateOfBirth().getYear();
        String ageSplit = String.valueOf(age);
        fill(fields, "topmostSubform[0].Page1[0].PartII[0].f1_17[0]", ageSplit);

        LocalDate coverageDate = foundedUser.getCoverageStartDate();
        if (coverageDate == null) {
            // Если даты покрытия нет, оставляем поле пустым и больше ничего не делаем
            fill(fields, "topmostSubform[0].Page1[0].PartII[0].f1_16[0]", "");
        } else {
            int startYear = coverageDate.getYear();

            if (startYear < reportingYear) {
                // Покрытие начиналось раньше отчётного года → “01”
                fill(fields, "topmostSubform[0].Page1[0].PartII[0].f1_16[0]", "01");
            }
            else if (startYear == reportingYear) {
                // Покрытие начинается в этом же году → ставим текущий месяц “MM”
                int m = coverageDate.getMonthValue();
                String mm = (m < 10 ? "0" : "") + m;
                fill(fields, "topmostSubform[0].Page1[0].PartII[0].f1_16[0]", mm);
            }
            else {
                // Покрытие начнётся после reportingYear → в этом году ещё не было покрытия, оставляем пустым
                fill(fields, "topmostSubform[0].Page1[0].PartII[0].f1_16[0]", "");
            }
        }


       // fill(fields, "topmostSubform[0].Page1[0].Table1[0]", "26");
      //  fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0]", "27");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_17[0]", "28");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_18[0]", "29");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_19[0]", "30");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_20[0]", "31");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_21[0]", "32");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_22[0]", "33");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_23[0]", "34");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_24[0]", "35");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_25[0]", "36");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_26[0]", "37");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_27[0]", "38");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_28[0]", "39");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row1[0].f1_29[0]", "40");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0]", "41");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_30[0]", "42");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_31[0]", "43");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_32[0]", "44");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_33[0]", "45");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_34[0]", "46");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_35[0]", "47");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_36[0]", "48");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_37[0]", "49");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_38[0]", "50");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_39[0]", "51");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_40[0]", "52");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_41[0]", "53");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row2[0].f1_42[0]", "54");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0]", "55");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_43[0]", "56");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_44[0]", "57");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_45[0]", "58");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_46[0]", "59");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_47[0]", "60");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_48[0]", "61");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_49[0]", "62");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_50[0]", "63");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_51[0]", "64");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_52[0]", "65");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_53[0]", "66");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_54[0]", "67");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row3[0].f1_55[0]", "68");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0]", "69");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_56[0]", "70");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_57[0]", "71");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_58[0]", "72");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_59[0]", "73");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_60[0]", "74");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_61[0]", "75");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_62[0]", "76");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_63[0]", "77");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_64[0]", "78");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_65[0]", "79");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_66[0]", "80");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_67[0]", "81");
        fill(fields, "topmostSubform[0].Page1[0].Table1[0].Row4[0].f1_68[0]", "82");
        fill(fields, "topmostSubform[0].Page3[0]", "83");
        fill(fields, "topmostSubform[0].Page3[0].PartIII[0]", "84");
        fill(fields, "topmostSubform[0].Page3[0].PartIII[0].c1_2[0]", "85");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0]", "86");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0]", "87");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].f3_56[0]", "88");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].f3_57[0]", "89");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].f3_58[0]", "90");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].f3_59[0]", "91");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].f3_60[0]", "92");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_3[0]", "93");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_4[0]", "94");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_5[0]", "95");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_6[0]", "96");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_7[0]", "97");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_8[0]", "98");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_9[0]", "99");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_10[0]", "100");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_11[0]", "101");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_12[0]", "102");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_13[0]", "103");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_14[0]", "104");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row1[0].c3_15[0]", "105");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0]", "106");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].f3_61[0]", "107");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].f3_62[0]", "108");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].f3_63[0]", "109");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].f3_64[0]", "110");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].f3_65[0]", "111");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_16[0]", "112");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_17[0]", "113");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_18[0]", "114");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_19[0]", "115");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_20[0]", "116");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_21[0]", "117");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_22[0]", "118");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_23[0]", "119");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_24[0]", "120");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_25[0]", "121");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_26[0]", "122");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_27[0]", "123");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row2[0].c3_28[0]", "124");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0]", "125");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].f3_66[0]", "126");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].f3_67[0]", "127");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].f3_68[0]", "128");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].f3_69[0]", "129");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].f3_70[0]", "130");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_29[0]", "131");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_30[0]", "132");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_31[0]", "133");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_32[0]", "134");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_33[0]", "135");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_34[0]", "136");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_35[0]", "137");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_36[0]", "138");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_37[0]", "139");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_38[0]", "140");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_39[0]", "141");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_40[0]", "142");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row3[0].c3_41[0]", "143");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0]", "144");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].f3_71[0]", "145");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].f3_72[0]", "146");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].f3_73[0]", "147");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].f3_74[0]", "148");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].f3_75[0]", "149");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_42[0]", "150");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_43[0]", "151");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_44[0]", "152");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_45[0]", "153");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_46[0]", "154");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_47[0]", "155");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_48[0]", "156");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_49[0]", "157");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_50[0]", "158");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_51[0]", "159");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_52[0]", "160");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_53[0]", "161");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row4[0].c3_54[0]", "162");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0]", "163");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].f3_76[0]", "164");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].f3_77[0]", "165");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].f3_78[0]", "166");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].f3_79[0]", "167");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].f3_80[0]", "168");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_55[0]", "169");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_56[0]", "170");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_57[0]", "171");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_58[0]", "172");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_59[0]", "173");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_60[0]", "174");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_61[0]", "175");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_62[0]", "176");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_63[0]", "177");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_64[0]", "178");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_65[0]", "179");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_66[0]", "180");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row5[0].c3_67[0]", "181");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0]", "182");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_81[0]", "183");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_82[0]", "184");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_83[0]", "185");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_84[0]", "186");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_85[0]", "187");
        // need to find this field not correct
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].f3_86[0]", "188");
        //
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_69[0]", "189");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_70[0]", "190");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_71[0]", "191");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_72[0]", "192");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_73[0]", "193");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_74[0]", "194");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_75[0]", "195");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_76[0]", "196");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_77[0]", "197");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_78[0]", "198");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_79[0]", "199");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3_80[0]", "200");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0]", "201");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].f3_86[0]", "202");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].f3_87[0]", "203");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].f3_88[0]", "204");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].f3_89[0]", "205");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].f3_90[0]", "206");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_81[0]", "207");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_82[0]", "208");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_83[0]", "209");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_84[0]", "210");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_85[0]", "211");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_86[0]", "212");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_87[0]", "213");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_88[0]", "214");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_89[0]", "215");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_90[0]", "216");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_91[0]", "217");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_92[0]", "218");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row7[0].c3_93[0]", "219");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0]", "220");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].f3_91[0]", "221");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].f3_92[0]", "222");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].f3_93[0]", "223");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].f3_95[0]", "224");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].f3_96[0]", "225");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_94[0]", "226");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_95[0]", "227");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_96[0]", "228");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_97[0]", "229");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_98[0]", "230");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_99[0]", "231");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_100[0]", "232");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_101[0]", "233");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_102[0]", "234");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_103[0]", "235");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_104[0]", "236");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_105[0]", "237");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row8[0].c3_106[0]", "238");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0]", "239");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].f3_97[0]", "240");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].f3_98[0]", "241");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].f3_99[0]", "242");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].f3_100[0]", "243");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].f3_101[0]", "244");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_107[0]", "245");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_108[0]", "246");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_109[0]", "247");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_110[0]", "248");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_111[0]", "249");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_112[0]", "250");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_113[0]", "251");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_114[0]", "252");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_115[0]", "253");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_116[0]", "254");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_117[0]", "255");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_118[0]", "256");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row9[0].c3_119[0]", "257");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0]", "258");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].f3_102[0]", "259");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].f3_103[0]", "260");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].f3_104[0]", "261");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].f3_105[0]", "262");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].f3_106[0]", "263");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_120[0]", "264");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_121[0]", "265");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_122[0]", "266");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_123[0]", "267");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_124[0]", "268");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_125[0]", "269");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_126[0]", "270");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_127[0]", "271");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_128[0]", "272");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_129[0]", "273");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_130[0]", "274");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_131[0]", "275");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row10[0].c3_132[0]", "276");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0]", "277");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].f3_107[0]", "278");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].f3_108[0]", "279");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].f3_109[0]", "280");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].f3_110[0]", "281");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].f3_111[0]", "282");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_112[0]", "283");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_113[0]", "284");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_114[0]", "285");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_115[0]", "286");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_116[0]", "287");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_117[0]", "288");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_118[0]", "289");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_119[0]", "290");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_120[0]", "291");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_121[0]", "292");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_122[0]", "293");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_123[0]", "294");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row11[0].c3_124[0]", "295");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0]", "296");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].f3_113[0]", "297");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].f3_114[0]", "298");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].f3_115[0]", "299");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].f3_116[0]", "300");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].f3_117[0]", "301");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_125[0]", "302");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_126[0]", "303");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_127[0]", "304");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_128[0]", "305");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_129[0]", "306");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_130[0]", "307");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_131[0]", "308");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_132[0]", "309");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_133[0]", "310");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_134[0]", "311");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_135[0]", "312");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_136[0]", "313");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row12[0].c3_137[0]", "314");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0]", "315");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].f3_118[0]", "316");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].f3_119[0]", "317");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].f3_120[0]", "318");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].f3_121[0]", "319");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].f3_122[0]", "320");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_138[0]", "321");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_139[0]", "322");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_140[0]", "323");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_141[0]", "324");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_142[0]", "325");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_143[0]", "326");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_144[0]", "327");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_145[0]", "328");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_146[0]", "329");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_148[0]", "330");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_149[0]", "331");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_150[0]", "332");
        fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row13[0].c3_151[0]", "333");


       // fill(fields, "topmostSubform[0].Page3[0].Table_Part3[0].Row6[0].c3



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

    private String splitAmount(int amount) {
        return new String();
    }



    private String spacedDigits(String value) {
        return value.chars()
                .mapToObj(c -> (char) c + "        ") // 7 пробелов
                .collect(Collectors.joining());

    }
}
