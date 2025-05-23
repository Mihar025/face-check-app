package com.zikpak.facecheck.taxesServices.pdfServices;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FillForm941ScheduleB {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;


    public void generateFilledPdf(Integer userId, Integer companyId, int year, int quarter) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/f941sb22.pdf";
        String dest = "filled_f941sb_output.pdf";
        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();
/*
        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);

                    topmostSubform[0]


        }

 */
       // fill(fields, "topmostSubform[0]", "0.001");
       // fill(fields, "topmostSubform[0].Page1[0]", "0.1");
       // fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0]", "0.2");
       // fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0]", "0.3");

        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_01[0]", "1");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_02[0]", "2");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_03[0]", "3");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_04[0]", "4");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_05[0]", "5");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_06[0]", "6");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_07[0]", "7");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_08[0]", "8");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_09[0]", "9");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_10[0]", "10");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_11[0]", "11");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_12[0]", "12");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_13[0]", "13");
        fill(fields, "topmostSubform[0].Page1[0].Header-Entity[0].Entity[0].f1_14[0]", "14");
        // Guess its quarter select
        fill(fields, "topmostSubform[0].Page1[0].c1_1[0]", "16");
        fill(fields, "topmostSubform[0].Page1[0].c1_1[1]", "17");
        fill(fields, "topmostSubform[0].Page1[0].c1_1[2]", "18");
        fill(fields, "topmostSubform[0].Page1[0].c1_1[3]", "19");

        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0]", "20");


        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_15[0]", "21");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_16[0]", "22");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_17[0]", "23");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_18[0]", "24");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_19[0]", "25");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_20[0]", "26");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_21[0]", "27");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_22[0]", "28");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_23[0]", "29");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_24[0]", "30");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_25[0]", "31");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_26[0]", "32");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_27[0]", "33");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_28[0]", "34");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_29[0]", "35");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month1[0].f1_30[0]", "36");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0]", "20");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_31[0]", "37");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_32[0]", "38");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_33[0]", "39");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_34[0]", "40");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_35[0]", "41");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_36[0]", "42");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_37[0]", "43");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_38[0]", "44");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_39[0]", "45");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_40[0]", "46");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_41[0]", "47");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_42[0]", "48");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_43[0]", "49");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_44[0]", "50");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_45[0]", "51");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month1[0].f1_46[0]", "52");

        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0]", "53");


        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_47[0]", "54");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_48[0]", "55");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_49[0]", "56");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_50[0]", "57");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_51[0]", "58");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_52[0]", "59");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_53[0]", "60");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_54[0]", "61");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_55[0]", "62");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_56[0]", "63");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_57[0]", "64");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_58[0]", "65");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_59[0]", "66");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_60[0]", "67");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_61[0]", "68");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month1[0].f1_62[0]", "69");

        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0]", "70");


        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_63[0]", "71");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_64[0]", "72");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_65[0]", "73");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_66[0]", "74");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_67[0]", "75");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_68[0]", "76");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_69[0]", "77");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_70[0]", "78");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_71[0]", "79");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_72[0]", "80");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_73[0]", "81");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_74[0]", "82");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_75[0]", "83");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month1[0].f1_76[0]", "84");
        fill(fields, "topmostSubform[0].Page1[0].f1_77[0]", "85");
        fill(fields, "topmostSubform[0].Page1[0].f1_78[0]", "86");

        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0]", "87");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_79[0]", "88");
      //  fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_80[0]", "89");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_81[0]", "90");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_82[0]", "91");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_83[0]", "92");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_84[0]", "93");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_85[0]", "94");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_86[0]", "95");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_87[0]", "96");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_88[0]", "97");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_89[0]", "98");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_90[0]", "99");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_91[0]", "100");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_92[0]", "101");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_93[0]", "102");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month2[0].f1_94[0]", "103");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0]", "104");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_95[0]", "105");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_96[0]", "106");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_97[0]", "107");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_98[0]", "108");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_99[0]", "109");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_100[0]", "110");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_101[0]", "111");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_102[0]", "112");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_103[0]", "113");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_104[0]", "114");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_105[0]", "115");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_106[0]", "116");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_107[0]", "117");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_108[0]", "118");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_109[0]", "119");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month2[0].f1_110[0]", "120");


        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0]", "121");

        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_111[0]", "122");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_112[0]", "123");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_113[0]", "124");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_114[0]", "125");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_115[0]", "126");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_116[0]", "127");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_117[0]", "128");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_118[0]", "129");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_119[0]", "130");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_120[0]", "131");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_121[0]", "132");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_122[0]", "133");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_123[0]", "134");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_124[0]", "135");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_125[0]", "136");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month2[0].f1_126[0]", "137");

        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0]", "138");

        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_127[0]", "139");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_128[0]", "140");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_129[0]", "141");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_130[0]", "142");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_131[0]", "143");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_132[0]", "144");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_133[0]", "145");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_134[0]", "146");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_135[0]", "147");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_136[0]", "148");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_137[0]", "149");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_138[0]", "150");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_139[0]", "151");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month2[0].f1_140[0]", "152");

        fill(fields, "topmostSubform[0].Page1[0].f1_141[0]", "153");
        fill(fields, "topmostSubform[0].Page1[0].f1_142[0]", "154");

        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0]", "155");

        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_143[0]", "157");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_144[0]", "158");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_145[0]", "159");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_146[0]", "160");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_147[0]", "161");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_148[0]", "162");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_149[0]", "163");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_150[0]", "164");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_151[0]", "165");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_152[0]", "166");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_153[0]", "167");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_154[0]", "168");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_155[0]", "169");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_156[0]", "170");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_157[0]", "171");
        fill(fields, "topmostSubform[0].Page1[0].Day1-8_Month3[0].f1_158[0]", "172");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0]", "173");

        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_159[0]", "174");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_160[0]", "175");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_161[0]", "176");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_162[0]", "177");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_163[0]", "178");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_164[0]", "179");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_165[0]", "180");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_166[0]", "181");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_167[0]", "182");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_168[0]", "183");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_169[0]", "184");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_170[0]", "185");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_171[0]", "186");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_172[0]", "187");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_173[0]", "188");
        fill(fields, "topmostSubform[0].Page1[0].Day9-16_Month3[0].f1_174[0]", "189");

        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0]", "190");

        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_175[0]", "190");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_176[0]", "191");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_177[0]", "192");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_178[0]", "193");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_179[0]", "194");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_180[0]", "195");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_181[0]", "196");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_182[0]", "197");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_183[0]", "198");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_184[0]", "199");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_185[0]", "200");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_186[0]", "201");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_187[0]", "202");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_188[0]", "203");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_189[0]", "204");
        fill(fields, "topmostSubform[0].Page1[0].Day17-24_Month3[0].f1_190[0]", "205");

        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0]", "206");

        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_191[0]", "207");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_192[0]", "208");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_193[0]", "209");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_194[0]", "210");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_195[0]", "211");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_196[0]", "212");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_197[0]", "213");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_198[0]", "214");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_199[0]", "215");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_200[0]", "216");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_201[0]", "217");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_202[0]", "218");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_203[0]", "219");
        fill(fields, "topmostSubform[0].Page1[0].Day25-31_Month3[0].f1_204[0]", "220");

        fill(fields, "topmostSubform[0].Page1[0].f1_205[0]", "221");
        fill(fields, "topmostSubform[0].Page1[0].f1_206[0]", "222");
        fill(fields, "topmostSubform[0].Page1[0].f1_207[0]", "223");
        fill(fields, "topmostSubform[0].Page1[0].f1_208[0]", "224");

        form.flattenFields();
        pdfDoc.close(); // завершает запись в файл

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
