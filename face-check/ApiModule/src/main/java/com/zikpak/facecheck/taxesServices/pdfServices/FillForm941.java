package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.taxesServices.dto.Form941Data;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.CodePointLengthValidator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FillForm941 {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;


    public void generateFilledPdf(Integer userId, Integer companyId, int year, int quarter) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/f941.pdf";
        String dest = "filled_f941_output.pdf";
        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }


        String ein1 = company.getEmployerEIN();
        String einParts1[] = ein1.split("-");
        if (einParts1.length != 2) {
            throw new IllegalStateException("Некорректный формат EIN: " + ein1);
        }
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_1[0]", spacedDigits(einParts1[0]));
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_2[0]", spacedDigits(einParts1[1]));

/*
 */

         // fill(fields, "topmostSubform[0]", "0.1");
    //    fill(fields, "topmostSubform[0].Page1[0]", "1");
      //  fill(fields, "topmostSubform[0].Page1[0].Header[0]", "1" );
      // fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0]", "0.4");
      //  fill(fields, "topmostSubform[0].Page1[0].Header[0].ReportForQuarter[0]", "0.5");




        Form941Data data = getQuarterly941Data(companyId, year, quarter);


        // Part 1 Rows , 1,2,3,4!
        //This field should contain all worker whose were working during the special quarter day! One op them is June 12 Thursday! This field should contain
        // all workers whose were working from example: June 8 - June 15! Or other quarter!
        //todo Fix later!
        fill(fields, "topmostSubform[0].Page1[0].f1_12[0]", String.valueOf(data.getEmployeeCount()));

// Line 2 — Wages, Tips, and Other Compensation
        String[] grossParts = splitAmount(data.getTotalGross());
        fill(fields, "topmostSubform[0].Page1[0].f1_13[0]", grossParts[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_14[0]", grossParts[1]);

// Line 3 — Federal Income Tax Withheld
        String[] fedParts = splitAmount(data.getTotalFederalWithholding());
        fill(fields, "topmostSubform[0].Page1[0].f1_15[0]", fedParts[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_16[0]", fedParts[1]);


      //  fill(fields, "topmostSubform[0].Page2[0].c2_5[0]", "12.0");


        if(quarter == 1) {
            fill(fields, "topmostSubform[0].Page1[0].Header[0].ReportForQuarter[0].c1_1[0]", "On");
        }
        else if(quarter == 2) {
            fill(fields, "topmostSubform[0].Page1[0].Header[0].ReportForQuarter[0].c1_1[1]", "On");
        }
        else if(quarter == 3) {
            fill(fields, "topmostSubform[0].Page1[0].Header[0].ReportForQuarter[0].c1_1[2]", "On");
        } else if (quarter == 4) {
            fill(fields, "topmostSubform[0].Page1[0].Header[0].ReportForQuarter[0].c1_1[3]", "On");
        }







       /*
            EIN , only two squares filling! We will by our selves manualy filing data!
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_1[0]", "1");
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_2[0]", "1");

        */
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_3[0]", company.getCompanyName());
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_4[0]", company.getCompanyName());
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_5[0]", company.getCompanyAddress());
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_6[0]", company.getCompanyCity());
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_7[0]", company.getCompanyState());
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_8[0]", company.getCompanyZipCode());
        /*
            This fields is : 1)Foreign country name ,
                             2)Foreign province/county ,
                             3)Foreign postal code
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_9[0]", "Ukraine");
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_10[0]", "Chernivtsi");
        fill(fields, "topmostSubform[0].Page1[0].Header[0].EntityArea[0].f1_11[0]", "11203");


         */

        //  fill(fields, "topmostSubform[0].Page1[0].c1_2[0]", "3"); // Check here and go to line 6 Part 1 Checkbox

        //May 16 9:15 PM

        // 5a: Taxable Social Security Wages (×0.124)
        String[] base5a = splitAmount(data.getSsTaxableWages());
        BigDecimal taxAmt5a = data.getSsTaxableWages().multiply(new BigDecimal("0.124"));
        String[] tax5a  = splitAmount(taxAmt5a);

        fill(fields, "topmostSubform[0].Page1[0].f1_17[0]", base5a[0]); // 5a-Col1 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_18[0]", base5a[1]); // 5a-Col1 fraction
        fill(fields, "topmostSubform[0].Page1[0].f1_19[0]", tax5a[0]);  // 5a-Col2 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_20[0]", tax5a[1]);  // 5a-Col2 fraction

// 5b: Taxable Social Security Tips (×0.124)
        String[] base5b = splitAmount(data.getSsTaxableTips());
        BigDecimal taxAmt5b = data.getSsTaxableTips().multiply(new BigDecimal("0.124"));
        String[] tax5b  = splitAmount(taxAmt5b);

        fill(fields, "topmostSubform[0].Page1[0].f1_21[0]", base5b[0]); // 5b-Col1 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_22[0]", base5b[1]); // 5b-Col1 fraction
        fill(fields, "topmostSubform[0].Page1[0].f1_23[0]", tax5b[0]);  // 5b-Col2 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_24[0]", tax5b[1]);  // 5b-Col2 fraction

// 5c: Taxable Medicare Wages & Tips (×0.029)
        String[] base5c = splitAmount(data.getMedicareTaxableWages());
        BigDecimal taxAmt5c = data.getMedicareTaxableWages().multiply(new BigDecimal("0.029"));
        String[] tax5c  = splitAmount(taxAmt5c);

        fill(fields, "topmostSubform[0].Page1[0].f1_25[0]", base5c[0]); // 5c-Col1 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_26[0]", base5c[1]); // 5c-Col1 fraction
        fill(fields, "topmostSubform[0].Page1[0].f1_27[0]", tax5c[0]);  // 5c-Col2 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_28[0]", tax5c[1]);  // 5c-Col2 fraction

// 5d: Wages & Tips Subject to Additional Medicare Tax (×0.009)
        String[] base5d = splitAmount(data.getAdditionalMedicareTaxableWages());
        BigDecimal taxAmt5d = data.getAdditionalMedicareTaxableWages().multiply(new BigDecimal("0.009"));
        String[] tax5d  = splitAmount(taxAmt5d);

        fill(fields, "topmostSubform[0].Page1[0].f1_29[0]", base5d[0]); // 5d-Col1 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_30[0]", base5d[1]); // 5d-Col1 fraction
        fill(fields, "topmostSubform[0].Page1[0].f1_31[0]", tax5d[0]);  // 5d-Col2 integer
        fill(fields, "topmostSubform[0].Page1[0].f1_32[0]", tax5d[1]);  // 5d-Col2 fraction

        BigDecimal totalFica = taxAmt5a
                .add(taxAmt5b)
                .add(taxAmt5c)
                .add(taxAmt5d);
        String[] parts5e = splitAmount(totalFica);
        //5e
        fill(fields, "topmostSubform[0].Page1[0].f1_33[0]", parts5e[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_34[0]", parts5e[1]);


        //     fill(fields, "topmostSubform[0].Page1[0].f1_25[0]", "12"); I dont fucking know (
        //5а
        fill(fields, "topmostSubform[0].Page1[0].f1_35[0]", "0");
        fill(fields, "topmostSubform[0].Page1[0].f1_36[0]", "0");

        //6
        BigDecimal line3 = data.getTotalFederalWithholding();
        BigDecimal line5e = taxAmt5a.add(taxAmt5c).add(taxAmt5d);
        BigDecimal line5f = BigDecimal.ZERO;
        BigDecimal line6 = line3.add(line5e).add(line5f);
        String[] parts6 = splitAmount(line6);
        fill(fields, "topmostSubform[0].Page1[0].f1_37[0]", parts6[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_38[0]", parts6[1]);

        //7
        BigDecimal line7 = BigDecimal.ZERO;
        String[] part7 = splitAmount(line7);
        fill(fields, "topmostSubform[0].Page1[0].f1_39[0]", part7[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_40[0]", part7[1]);
        //8
        BigDecimal line8 = BigDecimal.ZERO;
        String[] part8 = splitAmount(line8);
        fill(fields, "topmostSubform[0].Page1[0].f1_41[0]", part8[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_42[0]", part8[1]);
        //9
        BigDecimal line9 = BigDecimal.ZERO;
        String[] parts9 = splitAmount(line9);
        fill(fields, "topmostSubform[0].Page1[0].f1_43[0]", parts9[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_44[0]", parts9[1]);

        //10
        BigDecimal line10 = line6.add(line7).add(line8).add(line9);
        String[] part10 = splitAmount(line10);
        fill(fields, "topmostSubform[0].Page1[0].f1_45[0]", part10[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_46[0]", part10[1]);
        //11
        /*
        Как правило, традиционные строительные подрядчики (где основная деятельность — возведение и ремонт зданий по уже отработанным технологиям) не используют этот R&D-кредит в разделе 11 формы 941, потому что:
    Нет «qualified research»
IRS требует, чтобы вы вёлись систематические эксперименты или разработку новых продуктов,
процессов или материалов. Обычная стройка по чертежам под эту категорию не попадает.
Кредит доступен только молодым компаниям (< 5 лет) с выручкой до $5 млн за последние 3 года.
Большинство строительных фирм старше этого порога.
Нужно вести тщательные учёт и документацию R&D-процессов
(протоколы испытаний, записи инженеров), а это серьёзная дополнительная нагрузка.
         */
        BigDecimal line11 = BigDecimal.ZERO;
        String[] part11 = splitAmount(line11);
        fill(fields, "topmostSubform[0].Page1[0].f1_47[0]", part11[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_48[0]", part11[1]);

        //12 Repeat line 10
        BigDecimal line12 = line10.subtract(line11);
        String[] part12 = splitAmount(line12);
        fill(fields, "topmostSubform[0].Page1[0].f1_49[0]", part12[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_50[0]", part12[1]);



        BigDecimal depositedAmount= paymentHistoryService.getTotalPaymentsForQuarter(companyId, quarter, year);
        String[] partsForDepositedAmount = splitAmount(depositedAmount);
        fill(fields, "topmostSubform[0].Page1[0].f1_51[0]", partsForDepositedAmount[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_52[0]", partsForDepositedAmount[1]);


        if (line12.compareTo(depositedAmount) > 0){
            BigDecimal line14 = line12.subtract(depositedAmount);
            String[] part14 = splitAmount(line14);
            fill(fields, "topmostSubform[0].Page1[0].f1_53[0]", part14[0]);
            fill(fields, "topmostSubform[0].Page1[0].f1_54[0]", part14[1]);
            // Enter the amount of your payment.Make your check or money order payable to “United States Treasury
            fill(fields, "topmostSubform[0].Page3[0].f3_1[0]", part14[0]);
            fill(fields, "topmostSubform[0].Page3[0].f3_2[0]", part14[1]);
        }
        else{
            BigDecimal line14 = BigDecimal.ZERO;
            String[] part14 = splitAmount(line14);
            fill(fields, "topmostSubform[0].Page1[0].f1_53[0]",part14[0] );
            fill(fields, "topmostSubform[0].Page1[0].f1_54[0]", part14[1]);
            fill(fields, "topmostSubform[0].Page3[0].f3_1[0]", part14[0]);
            fill(fields, "topmostSubform[0].Page3[0].f3_2[0]", part14[1]);


        }

        if(depositedAmount.compareTo(line12) > 0){
            BigDecimal line15 = depositedAmount.subtract(line12);
            String[] part15 = splitAmount(line15);
            fill(fields, "topmostSubform[0].Page1[0].f1_55[0]", part15[0]);
            fill(fields, "topmostSubform[0].Page1[0].f1_56[0]", part15[1]);
        }
        else {
            BigDecimal line15 = BigDecimal.ZERO;
            String[] part15 = splitAmount(line15);
            fill(fields, "topmostSubform[0].Page1[0].f1_55[0]", part15[0]);
            fill(fields, "topmostSubform[0].Page1[0].f1_56[0]", part15[1]);
        }



        //Check one: choice
        fill(fields, "topmostSubform[0].Page1[0].c1_3[0]", "44"); // Apply to next return
        fill(fields, "topmostSubform[0].Page1[0].c1_3[1]", "45"); // Send a refund!

        // Name + EIN!
        // fill(fields, "topmostSubform[0].Page2[0]", "MYKHAILO");

        // fill(fields, "topmostSubform[0].Page2[0].Name_ReadOrder[0]", "46"); i dont know
        fill(fields, "topmostSubform[0].Page2[0].Name_ReadOrder[0].f1_3[0]", company.getCompanyName());
        //      fill(fields, "topmostSubform[0].Page2[0].EIN_Number[0]", "48"); i dont know

        String ein = company.getEmployerEIN();
        String einParts[] = ein.split("-");
        if (einParts.length != 2) {
            throw new IllegalStateException("Некорректный формат EIN: " + ein);
        }
        fill(fields, "topmostSubform[0].Page2[0].EIN_Number[0].f1_1[0]", einParts[0]);
        fill(fields, "topmostSubform[0].Page2[0].EIN_Number[0].f1_2[0]", einParts[1]);


        //  fill(fields, "topmostSubform[0].Page2[0].c2_1[0]", "51"); Check One Page 2 Part 2 Line 12 ..... text
        // fill(fields, "topmostSubform[0].Page2[0].c2_1[1]", "52"); Check One Page 2 Part 2 You were a monthly ...
        //  fill(fields, "topmostSubform[0].Page2[0].c2_1[2]", "61"); Checkbox You were a semiweekly schedule depositor for any part of this qua



        int prevQuarter = (quarter == 1 ? 4 : quarter - 1);
        int prevYear    = (quarter == 1 ? year - 1  : year);
        Form941Data prev = getQuarterly941Data(companyId, prevYear, prevQuarter);
        BigDecimal prevLine10 = calculateLine10(prev);
        BigDecimal prevLine11 = BigDecimal.ZERO;
        BigDecimal prevLine12 = prevLine10.subtract(prevLine11);

// 3) Определяем, monthly depositor ли вы
        boolean isMonthly = (
                line12.compareTo(new BigDecimal("2500"))  < 0
                        || prevLine12.compareTo(new BigDecimal("2500")) < 0
        ) && line12.compareTo(new BigDecimal("100000")) < 0;

// 4) Сбрасываем оба чекбокса
        fill(fields, "topmostSubform[0].Page2[0].c2_1[1]",  "Off");
        fill(fields, "topmostSubform[0].Page2[0].c2_1[2]","Off");

        if (isMonthly) {
            // отмечаем monthly
            fill(fields, "topmostSubform[0].Page2[0].c2_1[1]", "Yes");

            // для каждого месяца считаем Line 12
            BigDecimal m1 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 1);
            BigDecimal m2 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 2);
            BigDecimal m3 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 3);
            BigDecimal sumMonths = m1.add(m2).add(m3);
            BigDecimal roundedSum    = sumMonths.setScale(2, RoundingMode.HALF_UP);
            BigDecimal roundedLine12 = line12.setScale(2, RoundingMode.HALF_UP);

            if (!roundedSum.equals(roundedLine12)) {
                throw new IllegalStateException("Month sums don't equal Line 12");
            }


            // разбиваем и заполняем Month1, Month2, Month3 и Total
            String[] p1 = splitAmount(m1);
            String[] p2 = splitAmount(m2);
            String[] p3 = splitAmount(m3);
            String[] pt = splitAmount(line12);




            fill(fields, "topmostSubform[0].Page2[0].f2_1[0]",     p1[0]);
            fill(fields, "topmostSubform[0].Page2[0].f2_2[0]",    p1[1]);
            fill(fields, "topmostSubform[0].Page2[0].f2_3[0]",     p2[0]);
            fill(fields, "topmostSubform[0].Page2[0].f2_4[0]",    p2[1]);
            fill(fields, "topmostSubform[0].Page2[0].f2_5[0]",     p3[0]);
            fill(fields, "topmostSubform[0].Page2[0].f2_6[0]",    p3[1]);
            fill(fields, "topmostSubform[0].Page2[0].f2_7[0]",  pt[0]);
            fill(fields, "topmostSubform[0].Page2[0].f2_8[0]", pt[1]);

        } else {
            // отмечаем semiweekly и прикрепляем Schedule B
        //    fill(fields, "semiweeklyDepositorCheck[0]", "Yes");
            // …генерация и прикрепление Schedule B…
        }




        //  fill(fields, "topmostSubform[0].Page2[0].c2_2[0]", "62"); 17.  Check box If your business has closed or you stopped paying wages
     //   fill(fields, "topmostSubform[0].Page2[0].f2_9[0]", "63"); // date

        //fill(fields, "topmostSubform[0].Page2[0].c2_3[0]", "64"); // check box Check here. 18 point


        fill(fields, "topmostSubform[0].Page2[0].c2_4[0]", "65"); // check box YES part 4
        fill(fields, "topmostSubform[0].Page2[0].f2_10[0]", "Mykhailo Maidanskyi");
        fill(fields, "topmostSubform[0].Page2[0].f2_11[0]", "347-828-5790");
        fill(fields, "topmostSubform[0].Page2[0].f2_12[0]", "1       2       3       4       5");
      //  fill(fields, "topmostSubform[0].Page2[0].c2_4[1]", "69"); // Check box NO! After 5- digit PIN
        fill(fields, "topmostSubform[0].Page2[0].f2_13[0]", admin.fullName());

        fill(fields, "topmostSubform[0].Page2[0].f2_14[0]", "Owner");
        fill(fields, "topmostSubform[0].Page2[0].f2_15[0]", admin.getPhoneNumber());


        //Leaving this empty because im helping for company fo free! Not taking anyadvantage + finance!
       // fill(fields, "topmostSubform[0].Page2[0].c2_5[0]", "16.0"); // check if you are self employed  Paird Preparer Use Only
        //fill(fields, "topmostSubform[0].Page2[0].f2_16[0]", "Maidanskyi Mykhailo");
        //fill(fields, "topmostSubform[0].Page2[0].f2_17[0]", "74");
        //fill(fields, "topmostSubform[0].Page2[0].f2_18[0]", "Facecheck Corp");
        //fill(fields, "topmostSubform[0].Page2[0].f2_19[0]", "EIN");
        //fill(fields, "topmostSubform[0].Page2[0].f2_20[0]", "407 Ocean View Ave");
        //fill(fields, "topmostSubform[0].Page2[0].f2_21[0]", "347-828-5790");
        //fill(fields, "topmostSubform[0].Page2[0].f2_22[0]", "Brooklyn");
        //fill(fields, "topmostSubform[0].Page2[0].f2_23[0]", "NY");
       // fill(fields, "topmostSubform[0].Page2[0].f2_24[0]", "11235");





            fill(fields, "topmostSubform[0].Page3[0]", "77");
        fill(fields, "topmostSubform[0].Page3[0].EIN_Number[0]", "78");

        fill(fields, "topmostSubform[0].Page3[0].EIN_Number[0].f1_1[0]", einParts[0]);
        fill(fields, "topmostSubform[0].Page3[0].EIN_Number[0].f1_2[0]", einParts[1]);

   //     fill(fields, "topmostSubform[0].Page3[0].EIN_Number[0].f1_2[0]" + "topmostSubform[0].Page3[0].EIN_Number[0].f1_1[0]", company.getEmployerEIN());





      //  fill(fields, "topmostSubform[0].Page3[0].Line3_ReadOrder[0]", "83");
        if(quarter == 1) {
            fill(fields, "topmostSubform[0].Page3[0].Line3_ReadOrder[0].c3_1[0]", "On"); //quarter 1
        }
        else if(quarter == 2) {
            fill(fields, "topmostSubform[0].Page3[0].Line3_ReadOrder[0].c3_1[1]", "On"); //quarter 2
        }
        else if(quarter == 3) {
            fill(fields, "topmostSubform[0].Page3[0].Line3_ReadOrder[0].c3_1[2]", "On"); //quarter 3
        }
        else if(quarter == 4) {
            fill(fields, "topmostSubform[0].Page3[0].Line3_ReadOrder[0].c3_1[3]", "On"); // quarter 4
        }

        fill(fields, "topmostSubform[0].Page3[0].f1_3[0]", company.getCompanyName());
        fill(fields, "topmostSubform[0].Page3[0].f3_3[0]", admin.getHomeAddress());
        fill(fields, "topmostSubform[0].Page3[0].f3_4[0]", admin.getCity() + " " + admin.getState() + " " + admin.getZipcode());


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


    public Form941Data getQuarterly941Data(int companyId, int year, int quarter) {
        LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate end   = start.plusMonths(3).minusDays(1);

        Form941Data data = new Form941Data();
        data.setEmployeeCount(
                taxRecordRepo.countDistinctEmployeesByCompanyAndYear(companyId, start, end));
        data.setTotalGross(
                taxRecordRepo.sumGrossWages(companyId, start, end));
        data.setTotalFederalWithholding(
                taxRecordRepo.sumFederalWithholding(companyId, start, end));

        data.setSsTaxableWages(
                taxRecordRepo.sumSocialSecurityTaxableWages(companyId, start, end));
        data.setSsTaxableTips(
                taxRecordRepo.sumSocialSecurityTips(companyId, start, end));
        data.setMedicareTaxableWages(
                taxRecordRepo.sumMedicareTaxableWages(companyId, start, end));
        data.setAdditionalMedicareTaxableWages(
                taxRecordRepo.sumAdditionalMedicareTaxableWages(companyId, start, end));

        return data;
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


    /**
     * Считает Line 10 («Total taxes after adjustments») для любого набора данных Form941Data.
     */
    private BigDecimal calculateLine10(Form941Data d) {
        // 1) Federal withholding (Line 3)
        BigDecimal line3 = d.getTotalFederalWithholding();

        // 2) FICA-налог: 5a–5d
        BigDecimal tax5a = d.getSsTaxableWages().multiply(new BigDecimal("0.124"));
        BigDecimal tax5b = d.getSsTaxableTips().multiply(new BigDecimal("0.124"));
        BigDecimal tax5c = d.getMedicareTaxableWages().multiply(new BigDecimal("0.029"));
        BigDecimal tax5d = d.getAdditionalMedicareTaxableWages().multiply(new BigDecimal("0.009"));
        BigDecimal line5e = tax5a.add(tax5b).add(tax5c).add(tax5d);

        // 3) Корректировки 5f, 7, 8, 9 (в простейшем случае — нули)
        BigDecimal line5f = BigDecimal.ZERO; // Section 3121(q) Notice
        BigDecimal line7  = BigDecimal.ZERO; // fractions-of-cents
        BigDecimal line8  = BigDecimal.ZERO; // sick-pay adjustment
        BigDecimal line9  = BigDecimal.ZERO; // tips & GTLI adjustment

        // 4) Line 6 = line3 + line5e + line5f
        BigDecimal line6 = line3.add(line5e).add(line5f);

        // 5) Line 10 = line6 + line7 + line8 + line9
        return line6.add(line7).add(line8).add(line9);
    }

    /**
     * Для заданного месяца возвращает Line 12: т.е. tax after adjustments.
     * (Line 11 у вас всегда 0, поэтому просто возвращаем calculateLine10.)
     */
    private BigDecimal calculateLine12ForMonth(Integer companyId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.plusMonths(1).minusDays(1);

        // вместо taxRecordRepo.aggregate941Data(...) — вызываем ручной сбор
        Form941Data monthlyData = get941DataForPeriod(companyId, start, end);

        // Line 10 по вашей логике
        BigDecimal line10 = calculateLine10(monthlyData);

        // Line 11 (non-refundable credits) у вас всегда 0
        return line10;
    }



    /**
     * Собирает все необходимые суммы из репозитория за произвольный период.
     */
    private Form941Data get941DataForPeriod(Integer companyId, LocalDate start, LocalDate end) {
        Form941Data d = new Form941Data();
        d.setEmployeeCount(
                taxRecordRepo.countDistinctEmployeesByCompanyAndYear(companyId, start, end));
        d.setTotalGross(
                taxRecordRepo.sumGrossWages(companyId, start, end));
        d.setTotalFederalWithholding(
                taxRecordRepo.sumFederalWithholding(companyId, start, end));
        d.setSsTaxableWages(
                taxRecordRepo.sumSocialSecurityTaxableWages(companyId, start, end));
        d.setSsTaxableTips(
                taxRecordRepo.sumSocialSecurityTips(companyId, start, end));
        d.setMedicareTaxableWages(
                taxRecordRepo.sumMedicareTaxableWages(companyId, start, end));
        d.setAdditionalMedicareTaxableWages(
                taxRecordRepo.sumAdditionalMedicareTaxableWages(companyId, start, end));
        return d;
    }


}