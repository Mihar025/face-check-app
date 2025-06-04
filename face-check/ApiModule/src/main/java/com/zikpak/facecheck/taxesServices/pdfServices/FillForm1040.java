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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FillForm1040 {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;

    public void generateFilledPdf() throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f1040.pdf";
        String dest = "filled_f1040_output.pdf";

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }
        fill(fields, "topmostSubform[0]", "1");
        fill(fields, "topmostSubform[0].Page1[0]", "2");
        fill(fields, "topmostSubform[0].Page1[0].f1_01[0]", "3");
        fill(fields, "topmostSubform[0].Page1[0].f1_02[0]", "4");
        fill(fields, "topmostSubform[0].Page1[0].f1_03[0]", "5");
        fill(fields, "topmostSubform[0].Page1[0].f1_04[0]", "6");
        fill(fields, "topmostSubform[0].Page1[0].f1_05[0]", "7");
        fill(fields, "topmostSubform[0].Page1[0].f1_06[0]", "8");
        fill(fields, "topmostSubform[0].Page1[0].f1_07[0]", "9");
        fill(fields, "topmostSubform[0].Page1[0].f1_08[0]", "10");
        fill(fields, "topmostSubform[0].Page1[0].f1_09[0]", "11");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0]", "12");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_10[0]", "13");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_11[0]", "14");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_12[0]", "15");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_13[0]", "16");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_14[0]", "17");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_15[0]", "18");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_16[0]", "19");
        fill(fields, "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_17[0]", "20");
        fill(fields, "topmostSubform[0].Page1[0].c1_1[0]", "21");
        fill(fields, "topmostSubform[0].Page1[0].c1_2[0]", "22");
        fill(fields, "topmostSubform[0].Page1[0].FilingStatus_ReadOrder[0]", "23");
        fill(fields, "topmostSubform[0].Page1[0].FilingStatus_ReadOrder[0].c1_3[0]", "24");
        fill(fields, "topmostSubform[0].Page1[0].FilingStatus_ReadOrder[0].c1_3[1]", "25");
        fill(fields, "topmostSubform[0].Page1[0].FilingStatus_ReadOrder[0].c1_3[2]", "26");
        fill(fields, "topmostSubform[0].Page1[0].c1_3[0]", "27");
        fill(fields, "topmostSubform[0].Page1[0].c1_3[1]", "28");
        fill(fields, "topmostSubform[0].Page1[0].f1_18[0]", "29");
        fill(fields, "topmostSubform[0].Page1[0].c1_4[0]", "30");
        fill(fields, "topmostSubform[0].Page1[0].f1_19[0]", "31");
        fill(fields, "topmostSubform[0].Page1[0].c1_5[0]", "32");
        fill(fields, "topmostSubform[0].Page1[0].c1_5[1]", "33");
        fill(fields, "topmostSubform[0].Page1[0].c1_6[0]", "34");
        fill(fields, "topmostSubform[0].Page1[0].c1_7[0]", "35");
        fill(fields, "topmostSubform[0].Page1[0].c1_8[0]", "36");
        fill(fields, "topmostSubform[0].Page1[0].c1_9[0]", "37");
        fill(fields, "topmostSubform[0].Page1[0].c1_10[0]", "38");
        fill(fields, "topmostSubform[0].Page1[0].c1_11[0]", "39");
        fill(fields, "topmostSubform[0].Page1[0].c1_12[0]", "40");
        fill(fields, "topmostSubform[0].Page1[0].Dependents_ReadOrder[0]", "41");
        fill(fields, "topmostSubform[0].Page1[0].Dependents_ReadOrder[0].c1_13[0]", "42");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0]", "43");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0]", "44");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0].f1_20[0]", "45");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0].f1_21[0]", "46");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0].f1_22[0]", "47");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0].c1_14[0]", "48");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row1[0].c1_15[0]", "49");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0]", "50");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0].f1_23[0]", "51");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0].f1_24[0]", "52");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0].f1_25[0]", "53");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0].c1_16[0]", "54");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row2[0].c1_17[0]", "55");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0]", "56");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0].f1_26[0]", "57");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0].f1_27[0]", "58");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0].f1_28[0]", "59");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0].c1_18[0]", "60");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row3[0].c1_19[0]", "61");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0]", "62");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0].f1_29[0]", "63");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0].f1_30[0]", "64");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0].f1_31[0]", "65");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0].c1_20[0]", "66");
        fill(fields, "topmostSubform[0].Page1[0].Table_Dependents[0].Row4[0].c1_21[0]", "67");
        fill(fields, "topmostSubform[0].Page1[0].f1_32[0]", "68");
        fill(fields, "topmostSubform[0].Page1[0].f1_33[0]", "69");
        fill(fields, "topmostSubform[0].Page1[0].f1_34[0]", "70");
        fill(fields, "topmostSubform[0].Page1[0].f1_35[0]", "71");
        fill(fields, "topmostSubform[0].Page1[0].f1_36[0]", "72");
        fill(fields, "topmostSubform[0].Page1[0].f1_37[0]", "73");
        fill(fields, "topmostSubform[0].Page1[0].f1_38[0]", "74");
        fill(fields, "topmostSubform[0].Page1[0].f1_39[0]", "75");
        fill(fields, "topmostSubform[0].Page1[0].f1_40[0]", "76");
        fill(fields, "topmostSubform[0].Page1[0].f1_41[0]", "77");
        fill(fields, "topmostSubform[0].Page1[0].f1_42[0]", "78");
        fill(fields, "topmostSubform[0].Page1[0].f1_43[0]", "79");
        fill(fields, "topmostSubform[0].Page1[0].f1_44[0]", "80");
        fill(fields, "topmostSubform[0].Page1[0].f1_45[0]", "81");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0]", "82");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_46[0]", "83");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_47[0]", "84");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_48[0]", "85");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_49[0]", "86");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_50[0]", "87");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_51[0]", "88");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].c1_22[0]", "89");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].c1_23[0]", "90");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_52[0]", "91");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_53[0]", "92");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_54[0]", "93");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_55[0]", "94");
        fill(fields, "topmostSubform[0].Page1[0].Line4a-11_ReadOrder[0].f1_56[0]", "95");
        fill(fields, "topmostSubform[0].Page1[0].f1_57[0]", "96");
        fill(fields, "topmostSubform[0].Page1[0].f1_58[0]", "97");
        fill(fields, "topmostSubform[0].Page1[0].f1_59[0]", "98");
        fill(fields, "topmostSubform[0].Page1[0].f1_60[0]", "99");
        fill(fields, "topmostSubform[0].Page2[0]", "100");
        fill(fields, "topmostSubform[0].Page2[0].c2_1[0]", "101");
        fill(fields, "topmostSubform[0].Page2[0].c2_2[0]", "102");
        fill(fields, "topmostSubform[0].Page2[0].c2_3[0]", "103");
        fill(fields, "topmostSubform[0].Page2[0].f2_01[0]", "104");
        fill(fields, "topmostSubform[0].Page2[0].f2_02[0]", "105");
        fill(fields, "topmostSubform[0].Page2[0].f2_03[0]", "106");
        fill(fields, "topmostSubform[0].Page2[0].f2_04[0]", "107");
        fill(fields, "topmostSubform[0].Page2[0].f2_05[0]", "108");
        fill(fields, "topmostSubform[0].Page2[0].f2_06[0]", "109");
        fill(fields, "topmostSubform[0].Page2[0].f2_07[0]", "110");
        fill(fields, "topmostSubform[0].Page2[0].f2_08[0]", "111");
        fill(fields, "topmostSubform[0].Page2[0].f2_09[0]", "112");
        fill(fields, "topmostSubform[0].Page2[0].f2_10[0]", "113");
        fill(fields, "topmostSubform[0].Page2[0].f2_11[0]", "114");
        fill(fields, "topmostSubform[0].Page2[0].f2_12[0]", "115");
        fill(fields, "topmostSubform[0].Page2[0].f2_13[0]", "116");
        fill(fields, "topmostSubform[0].Page2[0].f2_14[0]", "117");
        fill(fields, "topmostSubform[0].Page2[0].f2_15[0]", "118");
        fill(fields, "topmostSubform[0].Page2[0].f2_16[0]", "119");
        fill(fields, "topmostSubform[0].Page2[0].f2_17[0]", "120");
        fill(fields, "topmostSubform[0].Page2[0].f2_18[0]", "121");
        fill(fields, "topmostSubform[0].Page2[0].f2_19[0]", "122");
        fill(fields, "topmostSubform[0].Page2[0].f2_20[0]", "123");
        fill(fields, "topmostSubform[0].Page2[0].f2_21[0]", "124");
        fill(fields, "topmostSubform[0].Page2[0].f2_22[0]", "125");
        fill(fields, "topmostSubform[0].Page2[0].f2_23[0]", "126");
        fill(fields, "topmostSubform[0].Page2[0].c2_4[0]", "127");
        fill(fields, "topmostSubform[0].Page2[0].f2_24[0]", "128");
        fill(fields, "topmostSubform[0].Page2[0].RoutingNo[0]", "129");
        fill(fields, "topmostSubform[0].Page2[0].RoutingNo[0].f2_25[0]", "130");
        fill(fields, "topmostSubform[0].Page2[0].c2_5[0]", "131");
        fill(fields, "topmostSubform[0].Page2[0].c2_5[1]", "132");
        fill(fields, "topmostSubform[0].Page2[0].AccountNo[0]", "133");
        fill(fields, "topmostSubform[0].Page2[0].AccountNo[0].f2_26[0]", "134");
        fill(fields, "topmostSubform[0].Page2[0].f2_27[0]", "135");
        fill(fields, "topmostSubform[0].Page2[0].f2_28[0]", "136");
        fill(fields, "topmostSubform[0].Page2[0].f2_29[0]", "137");
        fill(fields, "topmostSubform[0].Page2[0].c2_6[0]", "138");
        fill(fields, "topmostSubform[0].Page2[0].c2_6[1]", "139");
        fill(fields, "topmostSubform[0].Page2[0].f2_30[0]", "140");
        fill(fields, "topmostSubform[0].Page2[0].f2_31[0]", "141");
        fill(fields, "topmostSubform[0].Page2[0].f2_32[0]", "142");
        fill(fields, "topmostSubform[0].Page2[0].f2_33[0]", "143");
        fill(fields, "topmostSubform[0].Page2[0].f2_34[0]", "144");
        fill(fields, "topmostSubform[0].Page2[0].f2_35[0]", "145");
        fill(fields, "topmostSubform[0].Page2[0].f2_36[0]", "146");
        fill(fields, "topmostSubform[0].Page2[0].f2_37[0]", "147");
        fill(fields, "topmostSubform[0].Page2[0].f2_38[0]", "148");
        fill(fields, "topmostSubform[0].Page2[0].f2_39[0]", "149");
        fill(fields, "topmostSubform[0].Page2[0].f2_40[0]", "150");
        fill(fields, "topmostSubform[0].Page2[0].c2_7[0]", "151");
        fill(fields, "topmostSubform[0].Page2[0].f2_41[0]", "152");
        fill(fields, "topmostSubform[0].Page2[0].f2_42[0]", "153");
        fill(fields, "topmostSubform[0].Page2[0].f2_43[0]", "154");
        fill(fields, "topmostSubform[0].Page2[0].f2_44[0]", "155");



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

private String spacedDigits(String value) {
    return value.chars()
            .mapToObj(c -> (char) c + "        ") // 7 пробелов
            .collect(Collectors.joining());

}

}
