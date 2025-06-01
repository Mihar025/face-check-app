package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;

import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class Form940PdfGeneratorService {
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;


    //Form940SummaryDto s
    public  void generate940Pdf(Integer companyId, int year) throws IOException {
        String src = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/forms/f940.pdf";
        String dest = "filled_f940_output.pdf";

        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        System.out.println("==== Список полей формы ====");
        for (String fieldName : fields.keySet()) {
            System.out.println(fieldName);
        }


  /*      topmostSubform[0]
topmostSubform[0].Page1[0]
topmostSubform[0].Page1[0].Header[0]
topmostSubform[0].Page1[0].Header[0].EntityArea[0]

   */
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find company with provided Id!"));
        String ein1 = company.getEmployerEIN();
        String einParts1[] = ein1.split("-");
        if (einParts1.length != 2) {
            throw new IllegalStateException("Некорректный формат EIN: " + ein1);
        }


        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_1[0]", spacedDigits(einParts1[0]));
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_2[0]", spacedDigits(einParts1[1]));
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_3[0]", company.getCompanyName());
        //fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_4[0]", ); Trade Name
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_5[0]", company.getCompanyAddress());
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_6[0]", company.getCompanyCity());
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_7[0]", company.getCompanyState());
        fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_8[0]", company.getCompanyZipCode());
       // fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_9[0]", "9");
       // fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_10[0]", "10");
       // fill(fields, "topmostSubform[0].Page1[0].EntityArea[0].f1_11[0]", "11");

        //fill(fields, "topmostSubform[0].Page1[0].TypeReturn[0]", "12");

        //fill(fields, "topmostSubform[0].Page1[0].TypeReturn[0].c1_1[0]","1");
        //fill(fields, "topmostSubform[0].Page1[0].TypeReturn[0].c1_2[0]","1");
        //fill(fields, "topmostSubform[0].Page1[0].TypeReturn[0].c1_3[0]", "15");
        //fill(fields, "topmostSubform[0].Page1[0].TypeReturn[0].c1_4[0]", "16");

        String state = company.getCompanyState();
        char[] letters = state.toCharArray();
        fill(fields, "topmostSubform[0].Page1[0].f1_12[0]", String.valueOf(letters[0]));
        fill(fields, "topmostSubform[0].Page1[0].f1_13[0]", String.valueOf(letters[1]));

      //  fill(fields, "topmostSubform[0].Page1[0].c1_5[0]", "20");
      //  fill(fields, "topmostSubform[0].Page1[0].c1_6[0]", "21");

        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        String[] line3Split = splitAmount(line3);
        fill(fields, "topmostSubform[0].Page1[0].f1_14[0]", line3Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_15[0]", line3Split[1]);
//--------------------------------------------------------------------------------------------------------------
        BigDecimal line4 = BigDecimal.ZERO;
        String[] totalLine4 = splitAmount(line4);
        fill(fields, "topmostSubform[0].Page1[0].f1_16[0]",totalLine4[0] );
        fill(fields, "topmostSubform[0].Page1[0].f1_17[0]", totalLine4[1]);
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4a-b[0]", "25");
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4a-b[0].c1_7[0]", "26");
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4a-b[0].c1_8[0]", "27");
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4c-d[0]", "28");
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4c-d[0].c1_9[0]", "29");
      //  fill(fields, "topmostSubform[0].Page1[0].Checkboxes4c-d[0].c1_10[0]", "30");
      //  fill(fields, "topmostSubform[0].Page1[0].c1_11[0]", "31");
//---------------------------------------------------------------------------------------------------------------
        BigDecimal line5 = calculateLine5(companyId, year);
        String[] line5split = splitAmount(line5);
        fill(fields, "topmostSubform[0].Page1[0].f1_18[0]", line5split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_19[0]", line5split[1]);

        BigDecimal line6 = line4.add(line5);
        String[] line6Split = splitAmount(line6);
        fill(fields, "topmostSubform[0].Page1[0].f1_20[0]", line6Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_21[0]", line6Split[1]);

        BigDecimal line7 = line3.subtract(line6);
        String[] line7Split = splitAmount(line7);
        fill(fields, "topmostSubform[0].Page1[0].f1_22[0]", line7Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_23[0]",  line7Split[1]);


        BigDecimal rate = new BigDecimal("0.006");
        BigDecimal line8 = line7.multiply(rate);
        String[] line8Split = splitAmount(line8);
        fill(fields, "topmostSubform[0].Page1[0].f1_24[0]", line8Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_25[0]", line8Split[1]);

        BigDecimal line9 = BigDecimal.ZERO;
        String[] line9Split = splitAmount(line9);
        fill(fields, "topmostSubform[0].Page1[0].f1_26[0]", line9Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_27[0]", line9Split[1]);
        BigDecimal line10 = BigDecimal.ZERO;
        String[] line10Split = splitAmount(line10);
        fill(fields, "topmostSubform[0].Page1[0].f1_28[0]", line10Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_29[0]", line10Split[1]);
        BigDecimal line11 = BigDecimal.ZERO;
        String[] line11Split = splitAmount(line11);
        fill(fields, "topmostSubform[0].Page1[0].f1_30[0]", line11Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_31[0]", line11Split[1]);

        BigDecimal line12 = line8.add(line9).add(line10).add(line11);
        String[] line12Split = splitAmount(line12);
        fill(fields, "topmostSubform[0].Page1[0].f1_32[0]", line12Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_33[0]", line12Split[1]);


        //Made for payments to FUTA! Sum of maded payments! How much i payed to IRS deposites, each quarter!
        //Temporary Zero! Tamporary!
        BigDecimal line13 = BigDecimal.ZERO.setScale(2);
        String[] line13Split = splitAmount(line13);
        fill(fields, "topmostSubform[0].Page1[0].f1_34[0]", line13Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_35[0]", line13Split[1]);

// 2) Line 14 — Balance Due: если line12 > line13 → line14 = line12−line13, иначе 0
        BigDecimal line14;
        if (line12.compareTo(line13) > 0) {
            line14 = line12.subtract(line13);
        } else {
            line14 = BigDecimal.ZERO.setScale(2);
        }
        String[] line14Split = splitAmount(line14);
        fill(fields, "topmostSubform[0].Page1[0].f1_36[0]", line14Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_37[0]", line14Split[1]);

// 3) Line 15 — Overpayment: сравниваем (line13 + line14) с line12
        BigDecimal sumPaid = line13.add(line14);
        BigDecimal line15;
        if (sumPaid.compareTo(line12) > 0) {
            line15 = sumPaid.subtract(line12);
            fill(fields, "topmostSubform[0].Page1[0].c1_12[1]", "On");
        } else {
            line15 = BigDecimal.ZERO.setScale(2);
            fill(fields, "topmostSubform[0].Page1[0].c1_12[1]", "Off");
        }
        String[] line15Split = splitAmount(line15);
        fill(fields, "topmostSubform[0].Page1[0].f1_38[0]", line15Split[0]);
        fill(fields, "topmostSubform[0].Page1[0].f1_39[0]", line15Split[1]);


        /*
        fill(fields, "topmostSubform[0].Page1[0].f1_36[0]", "50");
        fill(fields, "topmostSubform[0].Page1[0].f1_37[0]", "51");
        fill(fields, "topmostSubform[0].Page1[0].f1_38[0]", "52");
        fill(fields, "topmostSubform[0].Page1[0].f1_39[0]", "53");
         */


        //fill(fields, "topmostSubform[0].Page1[0].c1_12[0]", "54");

        //Send refund
        //fill(fields, "topmostSubform[0].Page1[0].c1_12[1]", "55");

        fill(fields, "topmostSubform[0].Page2[0].f1_3[0]", company.getCompanyName());
        fill(fields, "topmostSubform[0].Page2[0].f1_1[0]", einParts1[0]);
        fill(fields, "topmostSubform[0].Page2[0].f1_2[0]", einParts1[1]);
        Map<String, BigDecimal> quarterlyFuta = calculateQuarterlyFuta(companyId, 2024);

        String[] q1split = splitAmount(quarterlyFuta.get("16a"));
        fill(fields, "topmostSubform[0].Page2[0].f2_1[0]", q1split[0]);
        fill(fields, "topmostSubform[0].Page2[0].f2_2[0]", q1split[1]);

        String[] q2split = splitAmount(quarterlyFuta.get("16b"));
        fill(fields, "topmostSubform[0].Page2[0].f2_3[0]", q2split[0]);
        fill(fields, "topmostSubform[0].Page2[0].f2_4[0]", q2split[1]);

        String[] q3split = splitAmount(quarterlyFuta.get("16c"));
        fill(fields, "topmostSubform[0].Page2[0].f2_5[0]", q3split[0]);
        fill(fields, "topmostSubform[0].Page2[0].f2_6[0]", q3split[1]);

        String[] q4split = splitAmount(quarterlyFuta.get("16d"));
        fill(fields, "topmostSubform[0].Page2[0].f2_7[0]", q4split[0]);
        fill(fields, "topmostSubform[0].Page2[0].f2_8[0]", q4split[1]);

        String[] totalSplit = splitAmount(quarterlyFuta.get("17"));
        fill(fields, "topmostSubform[0].Page2[0].f2_9[0]", totalSplit[0]);
        fill(fields, "topmostSubform[0].Page2[0].f2_10[0]",  totalSplit[1]);


    //    fill(fields, "topmostSubform[0].Page2[0].c2_1[0]", "70"); // Part 6 checkBox "YES"
    //    fill(fields, "topmostSubform[0].Page2[0].f2_11[0]", "Mykhailo Maidanskyi");
    //    fill(fields, "topmostSubform[0].Page2[0].f2_12[0]", "+1-347-828-5790");
    //    fill(fields, "topmostSubform[0].Page2[0].f2_13[0]", "1         2           3         4         5");

      //  fill(fields, "topmostSubform[0].Page2[0].c2_1[1]", "74"); // Part 6 checkBox "NO"

        fill(fields, "topmostSubform[0].Page2[0].f2_14[0]", company.getCompanyOwner().fullName());
        fill(fields, "topmostSubform[0].Page2[0].f2_15[0]", "Owner");
        fill(fields, "topmostSubform[0].Page2[0].f2_16[0]", company.getCompanyPhone());

        //    fill(fields, "topmostSubform[0].Page2[0].c2_2[0]", "78");
    //    fill(fields, "topmostSubform[0].Page2[0].f2_17[0]", "79");
      //  fill(fields, "topmostSubform[0].Page2[0].f2_18[0]", "80");
      //  fill(fields, "topmostSubform[0].Page2[0].f2_19[0]", "81");
     //   fill(fields, "topmostSubform[0].Page2[0].f2_20[0]", "82");
     //   fill(fields, "topmostSubform[0].Page2[0].f2_21[0]", "83");
     //   fill(fields, "topmostSubform[0].Page2[0].f2_22[0]", "84");
     //   fill(fields, "topmostSubform[0].Page2[0].f2_23[0]", "85");
        // fill(fields, "topmostSubform[0].Page2[0].f2_24[0]", "86");
        //fill(fields, "topmostSubform[0].Page2[0].f2_25[0]", "87");
        //fill(fields, "topmostSubform[0].Page3[0]", "88");
        //fill(fields, "topmostSubform[0].Page3[0].EIN_ReadOrder[0]", "89");


        fill(fields, "topmostSubform[0].Page3[0].EIN_ReadOrder[0].f1_1[0]", einParts1[0]);
        fill(fields, "topmostSubform[0].Page3[0].EIN_ReadOrder[0].f1_2[0]", einParts1[1]);

        fill(fields, "topmostSubform[0].Page3[0].f3_1[0]", line14Split[0]);
        fill(fields, "topmostSubform[0].Page3[0].f3_2[0]", line14Split[1]);

        fill(fields, "topmostSubform[0].Page3[0].f1_3[0]", company.getCompanyName());

        fill(fields, "topmostSubform[0].Page3[0].f3_4[0]", company.getCompanyAddress());
        fill(fields, "topmostSubform[0].Page3[0].f3_5[0]", company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());

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

    private String[] splitAmount(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        String[] parts = scaled.toPlainString().split("\\.");
        return new String[] {
                parts[0],                    // до точки
                parts.length > 1 ? parts[1] : "00" // после точки
        };
    }

    private BigDecimal calculateLine5(Integer companyId, int year){
        List<Object[]> rows = employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(companyId, year);
        BigDecimal result = BigDecimal.ZERO;
        for(Object[] row : rows){
            BigDecimal yearlyTotal = (BigDecimal) row[1];
            BigDecimal excess = yearlyTotal.subtract(new BigDecimal("7000"));
            result = result.add(excess);
        }
        return result;
    }


    private Map<String, BigDecimal> calculateQuarterlyFuta(Integer companyId, int year) {
        // 1. Получаем все записи о grossPay за год [1 янв, year … 31 дек, year]
        LocalDate startOfYear = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate endOfYear   = LocalDate.of(year, Month.DECEMBER, 31);

        List<EmployerTaxRecord> allRecords = employerTaxRecordRepository
                .findByCompanyIdAndWeekStartBetween(companyId, startOfYear, endOfYear);

        // 2. Группируем записи по сотруднику и по кварталу
        //    Словарь: employeeId → Map<номерКвартала, суммарный grossPay в этом квартале>
        Map<Integer, Map<Integer, BigDecimal>> grossByEmployeeAndQuarter = new HashMap<>();

        for (EmployerTaxRecord rec : allRecords) {
            Integer empId = rec.getEmployee().getId();
            LocalDate date = rec.getWeekStart(); // берем любую дату, попадающую в период выплаты
            BigDecimal pay = rec.getGrossPay();

            // Определим номер квартала по дате weekStart
            int quarter = getQuarterFromDate(date);

            grossByEmployeeAndQuarter
                    .computeIfAbsent(empId, k -> new HashMap<>())
                    .merge(quarter, pay, BigDecimal::add);
        }

        // 3. Для каждого сотрудника «разлетаем» его годовые выплаты по квартальным базам FUTA
        //    – сначала заводим структуру для хранения, сколько каждый уже «использовал» из лимита 7000 к концу каждого предыдущего квартала.
        //    – годовой лимит FUTA = 7000
        BigDecimal annualLimit = new BigDecimal("7000.00");

        // Результирующие базы FUTA (0.6%) по кварталам: ключ=номер квартала 1..4, значение = база
        Map<Integer, BigDecimal> quarterBases = new HashMap<>();
        quarterBases.put(1, BigDecimal.ZERO);
        quarterBases.put(2, BigDecimal.ZERO);
        quarterBases.put(3, BigDecimal.ZERO);
        quarterBases.put(4, BigDecimal.ZERO);

        // Пройдем по каждому сотруднику
        for (Map.Entry<Integer, Map<Integer, BigDecimal>> entry : grossByEmployeeAndQuarter.entrySet()) {
            Map<Integer, BigDecimal> payPerQuarter = entry.getValue();

            // 3.1 Отслеживаем, сколько из лимита (7000) уже «израсходовано» к началу каждого квартала
            BigDecimal usedSoFar = BigDecimal.ZERO;

            // Проходим кварталы по порядку: 1 → 2 → 3 → 4
            for (int q = 1; q <= 4; q++) {
                // Сумма grossPay у этого сотрудника в квартале q (может не быть записи = null → тогда 0)
                BigDecimal grossInQuarter = payPerQuarter.getOrDefault(q, BigDecimal.ZERO);

                // Остаток лимита перед текущим кварталом:
                BigDecimal remainingLimit = annualLimit.subtract(usedSoFar);
                if (remainingLimit.compareTo(BigDecimal.ZERO) <= 0) {
                    // Если остаток ≤ 0, значит сотрудник уже исчерпал весь лимит в предыдущих кварталах
                    // → база в текущем квартале = 0
                    continue;
                }

                // В базу на этот квартал идёт min(зарплата за квартал, остаток лимита)
                BigDecimal baseThisQuarter = grossInQuarter.min(remainingLimit);
                if (baseThisQuarter.compareTo(BigDecimal.ZERO) > 0) {
                    // Наращиваем квартальную сумму по компании
                    quarterBases.merge(q, baseThisQuarter, BigDecimal::add);
                    // Обновляем, сколько из лимита уже «использовано» к концу этого квартала
                    usedSoFar = usedSoFar.add(baseThisQuarter);
                }
            }
        }

        // 4. Теперь умножаем каждую квартальную базу на ставку 0.006 (т. е. 0.6%)
        BigDecimal rate = new BigDecimal("0.006");
        BigDecimal futuraQ1 = quarterBases.get(1).multiply(rate);
        BigDecimal futuraQ2 = quarterBases.get(2).multiply(rate);
        BigDecimal futuraQ3 = quarterBases.get(3).multiply(rate);
        BigDecimal futuraQ4 = quarterBases.get(4).multiply(rate);

        // Суммарный FUTA за год по четырём кварталам:
        BigDecimal totalQuarterly = futuraQ1
                .add(futuraQ2)
                .add(futuraQ3)
                .add(futuraQ4);

        // Если итоговая сумма ≤ 500, то согласно инструкции Part 5 пропускаем,
        // → поля 16a–16d и 17 оставляем пустыми (или «0.00», в зависимости от требований PDF).
        if (totalQuarterly.compareTo(new BigDecimal("500.00")) <= 0) {
            futuraQ1 = futuraQ2 = futuraQ3 = futuraQ4 = BigDecimal.ZERO.setScale(2);
            totalQuarterly = BigDecimal.ZERO.setScale(2);
        } else {
            // Округлим до двух знаков (в США обычно всё считается с точностью до цента)
            futuraQ1 = futuraQ1.setScale(2, BigDecimal.ROUND_HALF_UP);
            futuraQ2 = futuraQ2.setScale(2, BigDecimal.ROUND_HALF_UP);
            futuraQ3 = futuraQ3.setScale(2, BigDecimal.ROUND_HALF_UP);
            futuraQ4 = futuraQ4.setScale(2, BigDecimal.ROUND_HALF_UP);
            totalQuarterly = totalQuarterly.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // 5. Собираем результаты в Map и возвращаем
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("16a", futuraQ1);
        result.put("16b", futuraQ2);
        result.put("16c", futuraQ3);
        result.put("16d", futuraQ4);
        result.put("17",  totalQuarterly);

        return result;
    }

    /**
     * Вспомогательный метод: по дате определяем квартал (1..4).
     */
    private int getQuarterFromDate(LocalDate date) {
        int month = date.getMonthValue();
        if (month >= 1 && month <= 3) {
            return 1;
        } else if (month >= 4 && month <= 6) {
            return 2;
        } else if (month >= 7 && month <= 9) {
            return 3;
        } else {
            return 4;
        }
    }




}
