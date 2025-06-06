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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FillForm1094C {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;

    public void generateFilledPdf() throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f1094c.pdf";
        String dest = "filled_f1094C_output.pdf";

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

        fill(fields, "topmostSubform[0]", "1");
        fill(fields, "topmostSubform[0].Page1[0]", "2");
        fill(fields, "topmostSubform[0].Page1[0].c1_1[0]", "3");
        fill(fields, "topmostSubform[0].Page1[0].Name[0]", "4");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_1[0]", "5");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_2[0]", "6");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_3[0]", "7");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_4[0]", "8");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_5[0]", "9");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_6[0]", "10");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_7[0]", "11");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_8[0]", "12");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_9[0]", "13");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_10[0]", "14");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_11[0]", "15");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_12[0]", "16");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_13[0]", "17");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_14[0]", "18");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_15[0]", "19");
        fill(fields, "topmostSubform[0].Page1[0].Name[0].f1_16[0]", "20");
        fill(fields, "topmostSubform[0].Page1[0].c1_2[0]", "21");
        fill(fields, "topmostSubform[0].Page1[0].f1_17[0]", "22");
        fill(fields, "topmostSubform[0].Page1[0].c1_3[0]", "23");
        fill(fields, "topmostSubform[0].Page1[0].f1_18[0]", "24");
        fill(fields, "topmostSubform[0].Page1[0].c1_4[0]", "25");
        fill(fields, "topmostSubform[0].Page1[0].c1_4[1]", "26");
        fill(fields, "topmostSubform[0].Page1[0].c1_5[0]", "27");
        fill(fields, "topmostSubform[0].Page1[0].c1_6[0]", "28");
        fill(fields, "topmostSubform[0].Page1[0].c1_7[0]", "29");
        fill(fields, "topmostSubform[0].Page1[0].c1_8[0]", "30");
        fill(fields, "topmostSubform[0].Page1[0].f1_19[0]", "31");
        fill(fields, "topmostSubform[0].Page2[0]", "32");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0]", "33");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0]", "34");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].c2_1[0]", "35");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].c2_1[1]", "36");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].f2_1[0]", "37");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].f2_2[0]", "38");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].c2_2[0]", "39");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row1[0].f2_3[0]", "40");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0]", "41");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].c2_3[0]", "42");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].c2_3[1]", "43");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].f2_4[0]", "44");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].f2_5[0]", "45");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].c2_4[0]", "46");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row2[0].f2_6[0]", "47");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0]", "48");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].c2_5[0]", "49");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].c2_5[1]", "50");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].f2_7[0]", "51");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].f2_8[0]", "52");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].c2_6[0]", "53");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row3[0].f2_9[0]", "54");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0]", "55");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].c2_7[0]", "56");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].c2_7[1]", "57");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].f2_10[0]", "58");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].f2_11[0]", "59");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].c2_8[0]", "60");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row4[0].f2_12[0]", "61");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0]", "62");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].c2_9[0]", "63");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].c2_9[1]", "64");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].f2_13[0]", "65");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].f2_14[0]", "66");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].c2_10[0]", "67");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row5[0].f2_15[0]", "68");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0]", "69");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].c2_11[0]", "70");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].c2_11[1]", "71");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].f2_16[0]", "72");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].f2_17[0]", "73");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].c2_12[0]", "74");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row6[0].f2_18[0]", "75");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0]", "76");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].c2_13[0]", "77");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].c2_13[1]", "78");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].f2_19[0]", "79");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].f2_20[0]", "80");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].c2_14[0]", "81");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row7[0].f2_21[0]", "82");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0]", "83");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].c2_15[0]", "84");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].c2_15[1]", "85");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].f2_22[0]", "86");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].f2_23[0]", "87");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].c2_16[0]", "88");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row8[0].f2_24[0]", "89");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0]", "90");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].c2_17[0]", "91");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].c2_17[1]", "92");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].f2_25[0]", "93");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].f2_26[0]", "94");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].c2_18[0]", "95");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row9[0].f2_27[0]", "96");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0]", "97");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].c2_19[0]", "98");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].c2_19[1]", "99");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].f2_28[0]", "100");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].f2_29[0]", "101");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].c2_20[0]", "102");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row10[0].f2_30[0]", "103");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0]", "104");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].c2_21[0]", "105");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].c2_21[1]", "106");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].f2_31[0]", "107");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].f2_32[0]", "108");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].c2_22[0]", "109");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row11[0].f2_33[0]", "110");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0]", "111");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].c2_23[0]", "112");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].c2_23[1]", "113");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].f2_34[0]", "114");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].f2_35[0]", "115");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].c2_24[0]", "116");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row12[0].f2_36[0]", "117");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0]", "118");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].c2_25[0]", "119");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].c2_25[1]", "120");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].f2_37[0]", "121");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].f2_38[0]", "122");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].c2_26[0]", "123");
        fill(fields, "topmostSubform[0].Page2[0].Table1[0].Row13[0].f2_39[0]", "124");
        fill(fields, "topmostSubform[0].Page3[0]", "125");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0]", "126");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow36[0]", "127");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow36[0].Col1[0]", "128");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow36[0].Col1[0].f3_1[0]", "129");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow36[0].f3_2[0]", "130");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow37[0]", "131");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow37[0].Col1[0]", "132");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow37[0].Col1[0].f3_3[0]", "133");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow37[0].f3_4[0]", "134");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow38[0]", "135");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow38[0].Col1[0]", "136");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow38[0].Col1[0].f3_5[0]", "137");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow38[0].f3_6[0]", "138");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow39[0]", "139");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow39[0].Col1[0]", "140");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow39[0].Col1[0].f3_7[0]", "141");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow39[0].f3_8[0]", "142");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow40[0]", "143");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow40[0].Col1[0]", "144");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow40[0].Col1[0].f3_9[0]", "145");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow40[0].f3_10[0]", "146");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow41[0]", "147");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow41[0].Col1[0]", "148");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow41[0].Col1[0].f3_11[0]", "149");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow41[0].f3_12[0]", "150");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow42[0]", "151");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow42[0].Col1[0]", "152");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow42[0].Col1[0].f3_13[0]", "153");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow42[0].f3_14[0]", "154");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow43[0]", "155");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow43[0].Col1[0]", "156");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow43[0].Col1[0].f3_15[0]", "157");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow43[0].f3_16[0]", "158");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow44[0]", "159");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow44[0].Col1[0]", "160");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow44[0].Col1[0].f3_17[0]", "161");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow44[0].f3_18[0]", "162");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow45[0]", "163");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow45[0].Col1[0]", "164");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow45[0].Col1[0].f3_19[0]", "165");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow45[0].f3_20[0]", "166");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow46[0]", "167");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow46[0].Col1[0]", "168");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow46[0].Col1[0].f3_21[0]", "169");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow46[0].f3_22[0]", "170");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow47[0]", "171");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow47[0].Col1[0]", "172");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow47[0].Col1[0].f3_23[0]", "173");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow47[0].f3_24[0]", "174");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow48[0]", "175");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow48[0].Col1[0]", "176");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow48[0].Col1[0].f3_25[0]", "177");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow48[0].f3_26[0]", "178");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow49[0]", "179");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow49[0].Col1[0]", "180");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow49[0].Col1[0].f3_27[0]", "181");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow49[0].f3_28[0]", "182");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow50[0]", "183");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow50[0].Col1[0]", "184");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow50[0].Col1[0].f3_29[0]", "185");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN1[0].BodyRow50[0].f3_30[0]", "186");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0]", "187");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow51[0]", "188");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow51[0].Col2[0]", "189");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow51[0].Col2[0].f3_31[0]", "190");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow51[0].f3_32[0]", "191");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow52[0]", "192");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow52[0].Col2[0]", "193");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow52[0].Col2[0].f3_33[0]", "194");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow52[0].f3_34[0]", "195");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow53[0]", "196");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow53[0].Col2[0]", "197");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow53[0].Col2[0].f3_35[0]", "198");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow53[0].f3_36[0]", "199");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow54[0]", "200");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow54[0].Col2[0]", "201");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow54[0].Col2[0].f3_37[0]", "202");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow54[0].f3_38[0]", "203");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow55[0]", "204");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow55[0].Col2[0]", "205");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow55[0].Col2[0].f3_39[0]", "206");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow55[0].f3_40[0]", "207");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow56[0]", "208");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow56[0].Col2[0]", "209");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow56[0].Col2[0].f3_41[0]", "210");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow56[0].f3_42[0]", "211");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow57[0]", "212");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow57[0].Col2[0]", "213");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow57[0].Col2[0].f3_43[0]", "214");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow57[0].f3_44[0]", "215");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow58[0]", "216");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow58[0].Col2[0]", "217");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow58[0].Col2[0].f3_45[0]", "218");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow58[0].f3_46[0]", "219");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow59[0]", "220");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow59[0].Col2[0]", "221");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow59[0].Col2[0].f3_47[0]", "222");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow59[0].f3_48[0]", "223");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow60[0]", "224");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow60[0].Col2[0]", "225");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow60[0].Col2[0].f3_49[0]", "226");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow60[0].f3_50[0]", "227");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow61[0]", "228");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow61[0].Col2[0]", "229");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow61[0].Col2[0].f3_51[0]", "230");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow61[0].f3_52[0]", "231");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow62[0]", "232");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow62[0].Col2[0]", "233");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow62[0].Col2[0].f3_53[0]", "234");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow62[0].f3_54[0]", "235");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow63[0]", "236");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow63[0].Col2[0]", "237");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow63[0].Col2[0].f3_55[0]", "238");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow63[0].f3_56[0]", "239");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow64[0]", "240");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow64[0].Col2[0]", "241");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow64[0].Col2[0].f3_57[0]", "242");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow64[0].f3_58[0]", "243");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow65[0]", "244");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow65[0].Col2[0]", "245");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow65[0].Col2[0].f3_59[0]", "246");
        fill(fields, "topmostSubform[0].Page3[0].PartIVNameEIN2[0].BodyRow65[0].f3_60[0]", "247");
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
