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
public class FillForm1095B {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;

    public void generateFilledPdf() throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f1095b.pdf";
        String dest = "filled_f1095B_output.pdf";

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }




        fill(fields, "topmostSubform[0].Page1[0]", "2");
        fill(fields, "topmostSubform[0].Page1[0].Pg1Header[0]", "3");
        fill(fields, "topmostSubform[0].Page1[0].Pg1Header[0].cb_1[0]", "4");
        fill(fields, "topmostSubform[0].Page1[0].Pg1Header[0].cb_1[1]", "5");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0]", "6");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].Line1[0]", "7");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].Line1[0].f1_01[0]", "8");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].Line1[0].f1_02[0]", "9");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].Line1[0].f1_03[0]", "10");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_04[0]", "11");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_05[0]", "12");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_06[0]", "13");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_07[0]", "14");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_08[0]", "15");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_09[0]", "16");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_10[0]", "17");
        fill(fields, "topmostSubform[0].Page1[0].Part1Contents[0].f1_11[0]", "18");
        fill(fields, "topmostSubform[0].Page1[0].f1_12[0]", "19");
        fill(fields, "topmostSubform[0].Page1[0].f1_13[0]", "20");
        fill(fields, "topmostSubform[0].Page1[0].f1_14[0]", "21");
        fill(fields, "topmostSubform[0].Page1[0].f1_15[0]", "22");
        fill(fields, "topmostSubform[0].Page1[0].f1_16[0]", "23");
        fill(fields, "topmostSubform[0].Page1[0].f1_17[0]", "24");
        fill(fields, "topmostSubform[0].Page1[0].f1_18[0]", "25");
        fill(fields, "topmostSubform[0].Page1[0].f1_19[0]", "26");
        fill(fields, "topmostSubform[0].Page1[0].f1_20[0]", "27");
        fill(fields, "topmostSubform[0].Page1[0].f1_21[0]", "28");
        fill(fields, "topmostSubform[0].Page1[0].f1_22[0]", "29");
        fill(fields, "topmostSubform[0].Page1[0].f1_23[0]", "30");
        fill(fields, "topmostSubform[0].Page1[0].f1_24[0]", "31");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0]", "32");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0]", "33");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].f1_25[0]", "34");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].f1_26[0]", "35");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].f1_27[0]", "36");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].f1_28[0]", "37");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].f1_29[0]", "38");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_01[0]", "39");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_02[0]", "40");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_03[0]", "41");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_04[0]", "42");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_05[0]", "43");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_06[0]", "44");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_07[0]", "45");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_08[0]", "46");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_09[0]", "47");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_10[0]", "48");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_11[0]", "49");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_12[0]", "50");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row23[0].c1_13[0]", "51");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0]", "52");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].f1_30[0]", "53");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].f1_31[0]", "54");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].f1_32[0]", "55");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].f1_33[0]", "56");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].f1_34[0]", "57");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_14[0]", "58");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_15[0]", "59");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_16[0]", "60");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_17[0]", "61");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_18[0]", "62");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_19[0]", "63");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_20[0]", "64");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_21[0]", "65");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_22[0]", "66");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_23[0]", "67");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_24[0]", "68");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_25[0]", "69");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row24[0].c1_26[0]", "70");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0]", "71");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].f1_35[0]", "72");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].f1_36[0]", "73");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].f1_37[0]", "74");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].f1_38[0]", "75");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].f1_39[0]", "76");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_27[0]", "77");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_28[0]", "78");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_29[0]", "79");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_30[0]", "80");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_31[0]", "81");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_32[0]", "82");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_33[0]", "83");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_34[0]", "84");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_35[0]", "85");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_36[0]", "86");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_37[0]", "87");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_38[0]", "88");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row25[0].c1_39[0]", "89");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0]", "90");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].f1_40[0]", "91");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].f1_41[0]", "92");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].f1_42[0]", "93");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].f1_43[0]", "94");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].f1_44[0]", "95");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_40[0]", "96");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_41[0]", "97");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_42[0]", "98");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_43[0]", "99");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_44[0]", "100");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_45[0]", "101");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_46[0]", "102");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_47[0]", "103");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_48[0]", "104");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_49[0]", "105");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_50[0]", "106");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_51[0]", "107");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row26[0].c1_52[0]", "108");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0]", "109");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].f1_45[0]", "110");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].f1_46[0]", "111");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].f1_47[0]", "112");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].f1_48[0]", "113");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].f1_49[0]", "114");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_53[0]", "115");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_54[0]", "116");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_55[0]", "117");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_56[0]", "118");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_57[0]", "119");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_58[0]", "120");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_59[0]", "121");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_60[0]", "122");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_61[0]", "123");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_62[0]", "124");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_63[0]", "125");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_64[0]", "126");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row27[0].c1_65[0]", "127");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0]", "128");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].f1_50[0]", "129");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].f1_51[0]", "130");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].f1_52[0]", "131");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].f1_53[0]", "132");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].f1_54[0]", "133");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_66[0]", "134");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_67[0]", "135");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_68[0]", "136");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_69[0]", "137");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_70[0]", "138");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_71[0]", "139");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_72[0]", "140");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_73[0]", "141");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_74[0]", "142");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_75[0]", "143");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_76[0]", "144");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_77[0]", "145");
        fill(fields, "topmostSubform[0].Page1[0].Table1_Part4[0].Row28[0].c1_78[0]", "146");
        fill(fields, "topmostSubform[0].Page3[0]", "147");
        fill(fields, "topmostSubform[0].Page3[0].Name_ReadOrder[0]", "148");
        fill(fields, "topmostSubform[0].Page3[0].Name_ReadOrder[0].f3_01[0]", "149");
        fill(fields, "topmostSubform[0].Page3[0].Name_ReadOrder[0].f3_02[0]", "150");
        fill(fields, "topmostSubform[0].Page3[0].Name_ReadOrder[0].f3_03[0]", "151");
        fill(fields, "topmostSubform[0].Page3[0].f3_04[0]", "152");
        fill(fields, "topmostSubform[0].Page3[0].f3_05[0]", "153");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0]", "154");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0]", "155");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].f3_06[0]", "156");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].f3_07[0]", "157");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].f3_08[0]", "158");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].f3_09[0]", "159");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].f3_10[0]", "160");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_01[0]", "161");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_02[0]", "162");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_03[0]", "163");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_04[0]", "164");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_05[0]", "165");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_06[0]", "166");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_07[0]", "167");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_08[0]", "168");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_09[0]", "169");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_10[0]", "170");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_11[0]", "171");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_12[0]", "172");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row29[0].c3_13[0]", "173");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0]", "174");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].f3_11[0]", "175");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].f3_12[0]", "176");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].f3_13[0]", "177");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].f3_14[0]", "178");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].f3_15[0]", "179");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_14[0]", "180");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_15[0]", "181");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_16[0]", "182");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_17[0]", "183");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_18[0]", "184");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_19[0]", "185");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_20[0]", "186");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_21[0]", "187");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_22[0]", "188");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_23[0]", "189");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_24[0]", "190");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_25[0]", "191");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row30[0].c3_26[0]", "192");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0]", "193");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].f3_16[0]", "194");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].f3_17[0]", "195");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].f3_18[0]", "196");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].f3_19[0]", "197");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].f3_20[0]", "198");

        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_27[0]", "199");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_28[0]", "200");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_29[0]", "201");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_30[0]", "202");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_31[0]", "203");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_32[0]", "204");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_33[0]", "205");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_34[0]", "206");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_35[0]", "207");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_36[0]", "208");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_37[0]", "209");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_38[0]", "210");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row31[0].c3_39[0]", "211");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0]", "212");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].f3_21[0]", "213");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].f3_22[0]", "214");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].f3_23[0]", "215");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].f3_24[0]", "216");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].f3_25[0]", "217");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_40[0]", "218");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_41[0]", "219");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_42[0]", "220");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_43[0]", "221");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_44[0]", "222");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_45[0]", "223");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_46[0]", "224");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_47[0]", "225");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_48[0]", "226");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_49[0]", "227");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_50[0]", "228");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_51[0]", "229");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row32[0].c3_52[0]", "230");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0]", "231");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].f3_26[0]", "232");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].f3_27[0]", "233");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].f3_28[0]", "234");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].f3_29[0]", "235");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].f3_30[0]", "236");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_53[0]", "237");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_54[0]", "238");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_55[0]", "239");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_56[0]", "240");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_57[0]", "241");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_58[0]", "242");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_59[0]", "243");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_60[0]", "244");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_61[0]", "245");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_62[0]", "246");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_63[0]", "247");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_64[0]", "248");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row33[0].c3_65[0]", "249");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0]", "250");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].f3_31[0]", "251");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].f3_32[0]", "252");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].f3_33[0]", "253");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].f3_34[0]", "254");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].f3_35[0]", "255");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_66[0]", "256");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_67[0]", "257");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_68[0]", "258");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_69[0]", "259");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_70[0]", "260");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_71[0]", "261");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_72[0]", "262");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_73[0]", "263");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_74[0]", "264");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_75[0]", "265");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_76[0]", "266");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_77[0]", "267");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row34[0].c3_78[0]", "268");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0]", "269");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].f3_36[0]", "270");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].f3_37[0]", "271");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].f3_38[0]", "272");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].f3_39[0]", "273");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].f3_40[0]", "274");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_79[0]", "275");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_80[0]", "276");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_81[0]", "277");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_82[0]", "278");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_83[0]", "279");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_84[0]", "280");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_85[0]", "281");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_86[0]", "282");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_87[0]", "283");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_88[0]", "284");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_89[0]", "285");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_90[0]", "286");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row35[0].c3_91[0]", "287");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0]", "288");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].f3_41[0]", "289");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].f3_42[0]", "290");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].f3_43[0]", "291");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].f3_44[0]", "292");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].f3_45[0]", "293");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_92[0]", "294");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_93[0]", "295");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_94[0]", "296");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_95[0]", "297");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_96[0]", "298");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_97[0]", "299");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_98[0]", "300");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_99[0]", "301");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_100[0]", "302");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_101[0]", "303");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_102[0]", "304");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_103[0]", "305");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row36[0].c3_104[0]", "306");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0]", "307");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].f3_46[0]", "308");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].f3_47[0]", "309");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].f3_48[0]", "310");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].f3_49[0]", "311");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].f3_50[0]", "312");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_105[0]", "313");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_106[0]", "314");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_107[0]", "315");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_108[0]", "316");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_109[0]", "317");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_110[0]", "318");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_111[0]", "319");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_112[0]", "320");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_113[0]", "321");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_114[0]", "322");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_115[0]", "323");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_116[0]", "324");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row37[0].c3_117[0]", "325");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0]", "326");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].f3_51[0]", "327");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].f3_52[0]", "328");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].f3_53[0]", "329");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].f3_54[0]", "330");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].f3_55[0]", "331");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_118[0]", "332");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_119[0]", "333");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_120[0]", "334");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_121[0]", "335");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_122[0]", "336");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_123[0]", "337");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_124[0]", "338");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_125[0]", "339");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_126[0]", "340");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_127[0]", "341");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_128[0]", "342");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_129[0]", "343");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row38[0].c3_130[0]", "344");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0]", "345");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].f3_56[0]", "346");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].f3_57[0]", "347");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].f3_58[0]", "348");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].f3_59[0]", "349");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].f3_60[0]", "350");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_131[0]", "351");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_132[0]", "352");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_133[0]", "353");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_134[0]", "354");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_135[0]", "355");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_136[0]", "356");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_137[0]", "357");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_138[0]", "358");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_139[0]", "359");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_140[0]", "360");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_141[0]", "361");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_142[0]", "362");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row39[0].c3_143[0]", "363");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0]", "364");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].f3_61[0]", "365");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].f3_62[0]", "366");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].f3_63[0]", "367");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].f3_64[0]", "368");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].f3_65[0]", "369");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_144[0]", "370");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_145[0]", "371");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_146[0]", "372");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_147[0]", "373");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_148[0]", "374");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_149[0]", "375");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_150[0]", "376");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_151[0]", "377");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_152[0]", "378");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_153[0]", "379");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_154[0]", "380");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_155[0]", "381");
        fill(fields, "topmostSubform[0].Page3[0].Table2_Part4[0].Row40[0].c3_156[0]", "382");









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
