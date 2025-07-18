package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.dto.Form941Data;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class W2OfficialPDFService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AmazonS3Service amazonS3Service;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final MetricsForPdfServices metric;



    public byte[] generateFilledPdf(Integer userId, Integer companyId, int year) throws IOException {
        final String FORM = "W2";
        metric.recordRequest(FORM);
        Timer.Sample timer = metric.startTimer();

        try {
            InputStream inputStream = getClass().getResourceAsStream("/assets/fw2.pdf");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(
                    new PdfReader(inputStream),
                    new PdfWriter(baos)
            );

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));


            System.out.println("==== Список полей формы ====");
            for (String fieldName : fields.keySet()) {
                System.out.println(fieldName);
            }


            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);
            var payrolls = workerPayrollRepository.findAllByWorkerIdAndYear(user.getId(), startOfYear, endOfYear);

            BigDecimal grossPayTotal = BigDecimal.ZERO;
            BigDecimal netPayTotal = BigDecimal.ZERO;
            BigDecimal federalWithholdingTotal = BigDecimal.ZERO;
            BigDecimal socialSecurityEmployeeTotal = BigDecimal.ZERO;
            BigDecimal medicareTotal = BigDecimal.ZERO;
            BigDecimal nyStateWithholdingTotal = BigDecimal.ZERO;
            BigDecimal nyLocalWithholdingTotal = BigDecimal.ZERO;
            BigDecimal nyDisabilityWithholdingTotal = BigDecimal.ZERO;
            BigDecimal nyUnemploymentWithholding = BigDecimal.ZERO;
            BigDecimal nyPaidFamilyLeaveTotal = BigDecimal.ZERO;
            BigDecimal totalAllTaxes = BigDecimal.ZERO;

            for (WorkerPayroll payroll : payrolls) {
                grossPayTotal = grossPayTotal.add(safe(payroll.getGrossPay()));
                netPayTotal = netPayTotal.add(safe(payroll.getNetPay()));
                federalWithholdingTotal = federalWithholdingTotal.add(safe(payroll.getFederalWithholding()));
                socialSecurityEmployeeTotal = socialSecurityEmployeeTotal.add(safe(payroll.getSocialSecurityEmployee()));
                medicareTotal = medicareTotal.add(safe(payroll.getMedicare()));
                nyStateWithholdingTotal = nyStateWithholdingTotal.add(safe(payroll.getNyStateWithholding()));
                nyLocalWithholdingTotal = nyLocalWithholdingTotal.add(safe(payroll.getNyLocalWithholding()));
                nyDisabilityWithholdingTotal = nyDisabilityWithholdingTotal.add(safe(payroll.getNyDisabilityWithholding()));
                nyPaidFamilyLeaveTotal = nyPaidFamilyLeaveTotal.add(safe(payroll.getNyPaidFamilyLeave()));
                nyUnemploymentWithholding = nyUnemploymentWithholding.add(payroll.getNyUnemploymentWithholding());

                totalAllTaxes = totalAllTaxes
                        .add(safe(payroll.getFederalWithholding()))
                        .add(safe(payroll.getSocialSecurityEmployee()))
                        .add(safe(payroll.getMedicare()))
                        .add(safe(payroll.getNyStateWithholding()))
                        .add(safe(payroll.getNyLocalWithholding()))
                        .add(safe(payroll.getNyDisabilityWithholding()))
                        .add(safe(payroll.getNyPaidFamilyLeave()));
            }

// Copy A - For Social Security Administration

            // Void checkbox
            //   fill(fields, "topmostSubform[0].CopyA[0].Void_ReadOrder[0].c1_1[0]", "1");

            // Box A - Employee's social security number
            fill(fields, "topmostSubform[0].CopyA[0].BoxA_ReadOrder[0].f1_01[0]", user.getSSN_WORKER());

            // Employer information (Left Column)
            fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].f1_02[0]", company.getEmployerEIN());  // Employer EIN
            fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].f1_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());  // Employer name
            // Employee information
            if (user.getMiddleInitial() != null) {
                fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].FirstName_ReadOrder[0].f1_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].FirstName_ReadOrder[0].f1_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].LastName_ReadOrder[0].f1_06[0]", user.getLastName());   // Last name
            fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].f1_07[0]", " ");  // Employee address

            String address = user.getHomeAddress();
            String apt = user.getApt();
            String city = user.getCity();
            String state = user.getState();
            String zip = user.getZipcode();

            if (apt != null && !apt.isEmpty()) {
                address += " #" + apt;
            }
            address += "\n" + city + " " + state + " " + zip;
            fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].f1_08[0]", address);


            // Wage and tax information (Right Column)
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Box1_ReadOrder[0].f1_09[0]", grossPayTotal.toString());   // Box 1 - Wages
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_10[0]", federalWithholdingTotal.toString());                    // Box 2 - Federal tax withheld
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Box3_ReadOrder[0].f1_11[0]", grossPayTotal.toString());  // Box 3 - Social security wages
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_12[0]", socialSecurityEmployeeTotal.toString());                    // Box 4 - Social security tax
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Box5_ReadOrder[0].f1_13[0]", grossPayTotal.toString());  // Box 5 - Medicare wages
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_14[0]", medicareTotal.toString());                    // Box 6 - Medicare tax
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Box7_ReadOrder[0].f1_15[0]", " ");  // Box 7 - Social security tips
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_16[0]", " ");                    // Box 8 - Allocated tips
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_18[0]", " ");                    // Box 10 - Dependent care
            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Box11_ReadOrder[0].f1_19[0]", " "); // Box 11 - Nonqualified plans

            // Box 12 - Codes

            String codeField = "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_20[0]";
            String amountField = "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_21[0]";

            // Проверяем, что страховой план поддерживается и есть ежемесячная премия
            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField).setValue("DD");
                form.getField(amountField).setValue(annualPremium.toString());
            }


            // State and local information
            fill(fields, "topmostSubform[0].CopyA[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f1_29[0]", "NY"); // Box 15 - State
            fill(fields, "topmostSubform[0].CopyA[0].Boxes15_ReadOrder[0].f1_30[0]", company.getCompanyStateIdNumber());                    // Employer state ID
            fill(fields, "topmostSubform[0].CopyA[0].Box16_ReadOrder[0].f1_33[0]", grossPayTotal.toString()); // Box 16 - State wages 1
            fill(fields, "topmostSubform[0].CopyA[0].Box17_ReadOrder[0].f1_35[0]", nyStateWithholdingTotal.toString()); // Box 17 - State tax 1
            fill(fields, "topmostSubform[0].CopyA[0].Box18_ReadOrder[0].f1_37[0]", grossPayTotal.toString()); // Box 18 - Local wages 1
            fill(fields, "topmostSubform[0].CopyA[0].Box19_ReadOrder[0].f1_39[0]", nyLocalWithholdingTotal.toString()); // Box 19 - Local tax 1

            // Locality names
            fill(fields, "topmostSubform[0].CopyA[0].f1_41[0]", "NYC"); // Box 20 - Locality 1


// Copy 1 - For State, City, or Local Tax Department
            // Box A - Employee's social security number
            fill(fields, "topmostSubform[0].Copy1[0].BoxA_ReadOrder[0].f2_01[0]", user.getSSN_WORKER());

            // Employer information (Left Column)
            fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].f2_02[0]", company.getEmployerEIN());  // Employer EIN
            fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].f2_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode()); // Employer name
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].f2_04[0]", "49");  // Employer address

            // Employee information
            if (user.getMiddleInitial() != null) {

                fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].LastName_ReadOrder[0].f2_06[0]", user.getLastName());   // Last name
            fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].f2_07[0]", " ");  // Employee address
            fill(fields, "topmostSubform[0].Copy1[0].Col_Left[0].f2_08[0]", address); // Employee city, state, ZIP

            // Wage and tax information (Right Column)
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box1_ReadOrder[0].f2_09[0]", grossPayTotal.toString());   // Box 1
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_10[0]", federalWithholdingTotal.toString());                     // Box 2
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box3_ReadOrder[0].f2_11[0]", grossPayTotal.toString());   // Box 3
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_12[0]", socialSecurityEmployeeTotal.toString());                     // Box 4
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box5_ReadOrder[0].f2_13[0]", grossPayTotal.toString());   // Box 5
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_14[0]", medicareTotal.toString());                     // Box 6
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box7_ReadOrder[0].f2_15[0]", " ");   // Box 7
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_16[0]", " ");                     // Box 8
            //fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box9_ReadOrder[0].f2_17[0]", "62");   // Box 9
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_18[0]", "63");                     // Box 10
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box11__ReadOrder[0].f2_19[0]", "64"); // Box 11

            // Box 12
            String codeField2 = "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]";
            String amountField2 = "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]";
            // Проверяем, что страховой план поддерживается и есть ежемесячная премия
            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField2).setValue("DD");
                form.getField(amountField2).setValue(annualPremium.toString());
            }

 /*   fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_22[0]", "67");
    fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_23[0]", "68");
    fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_24[0]", "69");
    fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_25[0]", "70");
    fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_26[0]", "71");
    fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Box12_ReadOrder[0].f2_27[0]", "72");

  */

            // Checkboxes
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Statutory_ReadOrder[0].c2_2[0]", "73");
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].Retirement_ReadOrder[0].c2_3[0]", "74");
            // fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].c2_4[0]", "75");
            BigDecimal sdiPflTotal = nyDisabilityWithholdingTotal.add(nyPaidFamilyLeaveTotal);
            BigDecimal uiDbTotal = nyUnemploymentWithholding.setScale(2, RoundingMode.HALF_UP);

            String box14Value = String.format(
                    "SDI/PFL %s%nUI/DB %s",
                    sdiPflTotal.setScale(2, RoundingMode.HALF_UP),
                    uiDbTotal
            );
            fill(fields, "topmostSubform[0].Copy1[0].Col_Right[0].f2_28[0]", box14Value); // Box 14

            // State and local
            fill(fields, "topmostSubform[0].Copy1[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f2_29[0]", "NY");
            fill(fields, "topmostSubform[0].Copy1[0].Boxes15_ReadOrder[0].f2_30[0]", company.getCompanyStateIdNumber());
            // fill(fields, "topmostSubform[0].Copy1[0].Boxes15_ReadOrder[0].f2_31[0]", "79");
            //fill(fields, "topmostSubform[0].Copy1[0].Boxes15_ReadOrder[0].f2_32[0]", "80");

            fill(fields, "topmostSubform[0].Copy1[0].Box16_ReadOrder[0].f2_33[0]", grossPayTotal.toString());
            //   fill(fields, "topmostSubform[0].Copy1[0].Box16_ReadOrder[0].f2_34[0]", "82");
            fill(fields, "topmostSubform[0].Copy1[0].Box17_ReadOrder[0].f2_35[0]", nyStateWithholdingTotal.toString());
            //  fill(fields, "topmostSubform[0].Copy1[0].Box17_ReadOrder[0].f2_36[0]", "84");
            fill(fields, "topmostSubform[0].Copy1[0].Box18_ReadOrder[0].f2_37[0]", grossPayTotal.toString());
            //  fill(fields, "topmostSubform[0].Copy1[0].Box18_ReadOrder[0].f2_38[0]", "86");
            fill(fields, "topmostSubform[0].Copy1[0].Box19_ReadOrder[0].f2_39[0]", nyLocalWithholdingTotal.toString());
            // fill(fields, "topmostSubform[0].Copy1[0].Box19_ReadOrder[0].f2_40[0]", "88");

            fill(fields, "topmostSubform[0].Copy1[0].f2_41[0]", "NYC");
            //  fill(fields, "topmostSubform[0].Copy1[0].f2_42[0]", "90");


// Copy B - To Be Filed With Employee's FEDERAL Tax Return
            // All fields follow same pattern, starting from 91
            fill(fields, "topmostSubform[0].CopyB[0].BoxA_ReadOrder[0].f2_01[0]", user.getSSN_WORKER());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].f2_02[0]", company.getEmployerEIN());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].f2_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());

            // fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].f2_04[0]", "94");
            if (user.getMiddleInitial() != null) {
                fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].LastName_ReadOrder[0].f2_06[0]", user.getLastName());
            // fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].f2_07[0]", "97");
            fill(fields, "topmostSubform[0].CopyB[0].Col_Left[0].f2_08[0]", address);

            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box1_ReadOrder[0].f2_09[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_10[0]", federalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box3_ReadOrder[0].f2_11[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_12[0]", socialSecurityEmployeeTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box5_ReadOrder[0].f2_13[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_14[0]", medicareTotal.toString());

            // Box 12
            String codeField3 = "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]";
            String amountField3 = "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]";

            // Проверяем, что страховой план поддерживается и есть ежемесячная премия
            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField3).setValue("DD");
                form.getField(amountField3).setValue(annualPremium.toString());
            }
            // State and local
            fill(fields, "topmostSubform[0].CopyB[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f2_29[0]", "NY");
            fill(fields, "topmostSubform[0].CopyB[0].Boxes15_ReadOrder[0].f2_30[0]", company.getCompanyStateIdNumber());
            fill(fields, "topmostSubform[0].CopyB[0].Box16_ReadOrder[0].f2_33[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Box17_ReadOrder[0].f2_35[0]", nyStateWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Box18_ReadOrder[0].f2_37[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].Box19_ReadOrder[0].f2_39[0]", nyLocalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyB[0].f2_41[0]", "NYC");

            //  fill(fields, "topmostSubform[0].CopyB[0].Boxes15_ReadOrder[0].f2_31[0]", "124");
            // fill(fields, "topmostSubform[0].CopyB[0].Boxes15_ReadOrder[0].f2_32[0]", "125");
            //  fill(fields, "topmostSubform[0].CopyB[0].f2_42[0]", "135");
            //  fill(fields, "topmostSubform[0].CopyB[0].Box18_ReadOrder[0].f2_38[0]", "131");
            // fill(fields, "topmostSubform[0].CopyB[0].Box19_ReadOrder[0].f2_40[0]", "133");
            //   fill(fields, "topmostSubform[0].CopyB[0].Box17_ReadOrder[0].f2_36[0]", "129");
            //  fill(fields, "topmostSubform[0].CopyB[0].Box16_ReadOrder[0].f2_34[0]", "127");
            //   fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box9_ReadOrder[0].f2_17[0]", "107");
            //   fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box7_ReadOrder[0].f2_15[0]", "105");
//    fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_16[0]", "106");
            // fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_18[0]", "108");
            //  fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box11__ReadOrder[0].f2_19[0]", "109");

            //  fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_22[0]", "112");
            //  fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_23[0]", "113");
            //   fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_24[0]", "114");
            //   fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_25[0]", "115");
            //   fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_26[0]", "116");
//fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Box12_ReadOrder[0].f2_27[0]", "117");

            // Checkboxes
            // fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Statutory_ReadOrder[0].c2_2[0]", "118");
            // fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].Retirement_ReadOrder[0].c2_3[0]", "119");
            // fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].c2_4[0]", "120");

            fill(fields, "topmostSubform[0].CopyB[0].Col_Right[0].f2_28[0]", box14Value);
// Copy C - For EMPLOYEE'S RECORDS

            // All fields follow same pattern, starting from 136
            fill(fields, "topmostSubform[0].CopyC[0].BoxA_ReadOrder[0].f2_01[0]", user.getSSN_WORKER());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].f2_02[0]", company.getEmployerEIN());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].f2_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());

            //  fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].f2_04[0]", "139");
            if (user.getMiddleInitial() != null) {
                fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].LastName_ReadOrder[0].f2_06[0]", user.getLastName());
            // fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].f2_07[0]", "142");
            fill(fields, "topmostSubform[0].CopyC[0].Col_Left[0].f2_08[0]", address);

            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box1_ReadOrder[0].f2_09[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_10[0]", federalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box3_ReadOrder[0].f2_11[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_12[0]", socialSecurityEmployeeTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box5_ReadOrder[0].f2_13[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_14[0]", medicareTotal.toString());

            // Box 12
            String codeField4 = "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]";
            String amountField4 = "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]";
            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField4).setValue("DD");
                form.getField(amountField4).setValue(annualPremium.toString());
            }

            // State and local
            fill(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f2_29[0]", "NY");
            fill(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].f2_30[0]", company.getCompanyStateIdNumber());

            fill(fields, "topmostSubform[0].CopyC[0].Box16_ReadOrder[0].f2_33[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Box17_ReadOrder[0].f2_35[0]", nyStateWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Box18_ReadOrder[0].f2_37[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyC[0].Box19_ReadOrder[0].f2_39[0]", nyLocalWithholdingTotal.toString());

            fill(fields, "topmostSubform[0].CopyC[0].f2_41[0]", "NYC");


            //  fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_22[0]", "157");
//  fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_23[0]", "158");
//    fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_24[0]", "159");
            //   fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_25[0]", "160");
            //  fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_26[0]", "161");
            //  fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box12_ReadOrder[0].f2_27[0]", "162");

            // Checkboxes
            // fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box7_ReadOrder[0].f2_15[0]", "150");
//    fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_16[0]", "151");
            // fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box9_ReadOrder[0].f2_17[0]", "152");
            //   fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_18[0]", "153");
            //   fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Box11__ReadOrder[0].f2_19[0]", "154");

            //  fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Statutory_ReadOrder[0].c2_2[0]", "163");
            //   fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].Retirement_ReadOrder[0].c2_3[0]", "164");
            // fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].c2_4[0]", "165");

            fill(fields, "topmostSubform[0].CopyC[0].Col_Right[0].f2_28[0]", box14Value);
            //fill(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].f2_31[0]", "169");
            // fi //   fill(fields, "topmostSubform[0].CopyC[0].Box19_ReadOrder[0].f2_40[0]", "178");
            //        //  fill(fields, "topmostSubform[0].CopyC[0].f2_42[0]", "180");
            //        //  fill(fields, "topmostSubform[0].CopyC[0].Box18_ReadOrder[0].f2_38[0]", "176");
            //        //  fill(fields, "topmostSubform[0].CopyC[0].Box17_ReadOrder[0].f2_36[0]", "174");
            //        //  fill(fields, "topmostSubform[0].CopyC[0].f2_42[0]", "180");
            //        // fill(fields, "topmostSubform[0].CopyC[0].Box16_ReadOrder[0].f2_34[0]", "172");
            //        //fill(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].f2_31[0]", "169");
            //        // fill(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].f2_32[0]", "170")ll(fields, "topmostSubform[0].CopyC[0].Boxes15_ReadOrder[0].f2_32[0]", "170");

// Copy 2 - To Be Filed With Employee's State, City, or Local Income Tax Return
            // All fields follow same pattern, starting from 181
            fill(fields, "topmostSubform[0].Copy2[0].BoxA_ReadOrder[0].f2_01[0]", user.getSSN_WORKER());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].f2_02[0]", company.getEmployerEIN());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].f2_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());

            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].f2_04[0]", "184");
            if (user.getMiddleInitial() != null) {
                fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].LastName_ReadOrder[0].f2_06[0]", user.getLastName());
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].f2_07[0]", "187");
            fill(fields, "topmostSubform[0].Copy2[0].Col_Left[0].f2_08[0]", address);

            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box1_ReadOrder[0].f2_09[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_10[0]", federalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box3_ReadOrder[0].f2_11[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_12[0]", socialSecurityEmployeeTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box5_ReadOrder[0].f2_13[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_14[0]", medicareTotal.toString());


            // Box 12
            String codeField5 = "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]";
            String amountField5 = "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]";

            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField5).setValue("DD");
                form.getField(amountField5).setValue(annualPremium.toString());
            }

            // State and local
            fill(fields, "topmostSubform[0].Copy2[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f2_29[0]", "NY");
            fill(fields, "topmostSubform[0].Copy2[0].Boxes15_ReadOrder[0].f2_30[0]", company.getCompanyStateIdNumber());
            fill(fields, "topmostSubform[0].Copy2[0].Box16_ReadOrder[0].f2_33[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Box17_ReadOrder[0].f2_35[0]", nyStateWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Box18_ReadOrder[0].f2_37[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].Box19_ReadOrder[0].f2_39[0]", nyLocalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].Copy2[0].f2_41[0]", "NYC");

            //fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_18[0]", "198");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box11__ReadOrder[0].f2_19[0]", "199");
            // fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box7_ReadOrder[0].f2_15[0]", "195");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_16[0]", "196");
            //fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box9_ReadOrder[0].f2_17[0]", "197");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]", "200");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]", "201");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_22[0]", "202");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_23[0]", "203");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_24[0]", "204");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_25[0]", "205");
            //   fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_26[0]", "206");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Box12_ReadOrder[0].f2_27[0]", "207");
            // Checkboxes
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Statutory_ReadOrder[0].c2_2[0]", "208");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].Retirement_ReadOrder[0].c2_3[0]", "209");
            //  fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].c2_4[0]", "210");
            fill(fields, "topmostSubform[0].Copy2[0].Col_Right[0].f2_28[0]", box14Value);
            //fill(fields, "topmostSubform[0].Copy2[0].f2_42[0]", "225");
            //  fill(fields, "topmostSubform[0].Copy2[0].Box19_ReadOrder[0].f2_40[0]", "223");
            //  fill(fields, "topmostSubform[0].Copy2[0].Box18_ReadOrder[0].f2_38[0]", "221");
            // fill(fields, "topmostSubform[0].Copy2[0].Box17_ReadOrder[0].f2_36[0]", "219");
            // fill(fields, "topmostSubform[0].Copy2[0].Box16_ReadOrder[0].f2_34[0]", "217");
            //  fill(fields, "topmostSubform[0].Copy2[0].Boxes15_ReadOrder[0].f2_31[0]", "214");
            // fill(fields, "topmostSubform[0].Copy2[0].Boxes15_ReadOrder[0].f2_32[0]", "215");

// Copy D - For Employer
            // Void checkbox
            fill(fields, "topmostSubform[0].CopyD[0].Void_ReadOrder[0].c2_1[0]", "226");

            // All other fields starting from 227
            fill(fields, "topmostSubform[0].CopyD[0].BoxA_ReadOrder[0].f2_01[0]", user.getSSN_WORKER());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].f2_02[0]", company.getEmployerEIN());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].f2_03[0]", company.getCompanyName() + "\n" +
                    company.getCompanyAddress() + "\n" + company.getCompanyCity() + " " + company.getCompanyState() + " " + company.getCompanyZipCode());  // Employer name
            //  fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].f2_04[0]", "230");
            if (user.getMiddleInitial() != null) {

                fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName() + user.getMiddleInitial());  // First name
            } else {
                fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].FirstName_ReadOrder[0].f2_05[0]", user.getFirstName());  // First name
            }
            fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].LastName_ReadOrder[0].f2_06[0]", user.getLastName());


            //  fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].f2_07[0]", "233");
            fill(fields, "topmostSubform[0].CopyD[0].Col_Left[0].f2_08[0]", address);

            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box1_ReadOrder[0].f2_09[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_10[0]", federalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box3_ReadOrder[0].f2_11[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_12[0]", socialSecurityEmployeeTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box5_ReadOrder[0].f2_13[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_14[0]", medicareTotal.toString());

            // Box 12
            String codeField6 = "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_20[0]";
            String amountField6 = "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_21[0]";
            if (Boolean.TRUE.equals(user.getEnrolledInHealthPlan())
                    && user.getMonthlyHealthPremium() != null
                    && user.getMonthlyHealthPremium().compareTo(BigDecimal.ZERO) > 0) {

                // Вычисляем годовую стоимость: 12 месяцев × ежемес. премия
                BigDecimal annualPremium = user.getMonthlyHealthPremium()
                        .multiply(BigDecimal.valueOf(12))
                        .setScale(2, RoundingMode.HALF_UP);

                // Заполняем Code DD и сумму
                form.getField(codeField6).setValue("DD");
                form.getField(amountField6).setValue(annualPremium.toString());
            }


            // State and local
            fill(fields, "topmostSubform[0].CopyD[0].Boxes15_ReadOrder[0].Box15_ReadOrder[0].f2_29[0]", "NY");
            fill(fields, "topmostSubform[0].CopyD[0].Boxes15_ReadOrder[0].f2_30[0]", company.getCompanyStateIdNumber());

            fill(fields, "topmostSubform[0].CopyD[0].Box16_ReadOrder[0].f2_33[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Box17_ReadOrder[0].f2_35[0]", nyStateWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Box18_ReadOrder[0].f2_37[0]", grossPayTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].Box19_ReadOrder[0].f2_39[0]", nyLocalWithholdingTotal.toString());
            fill(fields, "topmostSubform[0].CopyD[0].f2_41[0]", "NYC");
            fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_28[0]", box14Value);

    /*
        // Checkboxes
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Statutory_ReadOrder[0].c2_2[0]", "254");
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Retirement_ReadOrder[0].c2_3[0]", "255");

  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].c2_4[0]", "256");

        fill(fields, "topmostSubform[0].CopyD[0].Box19_ReadOrder[0].f2_40[0]", "269");
    fill(fields, "topmostSubform[0].CopyD[0].Box18_ReadOrder[0].f2_38[0]", "267");
    fill(fields, "topmostSubform[0].CopyD[0].Box17_ReadOrder[0].f2_36[0]", "265");
    fill(fields, "topmostSubform[0].CopyD[0].Box16_ReadOrder[0].f2_34[0]", "263");
    fill(fields, "topmostSubform[0].CopyD[0].Boxes15_ReadOrder[0].f2_32[0]", "261");
    fill(fields, "topmostSubform[0].CopyD[0].Boxes15_ReadOrder[0].f2_31[0]", "260");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_22[0]", "248");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_23[0]", "249");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_24[0]", "250");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_25[0]", "251");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_26[0]", "252");
    //fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box12_ReadOrder[0].f2_27[0]", "253");
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box7_ReadOrder[0].f2_15[0]", "241");
   // fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_16[0]", "242");
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box9_ReadOrder[0].f2_17[0]", "243");
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].f2_18[0]", "244");
  //  fill(fields, "topmostSubform[0].CopyD[0].Col_Right[0].Box11__ReadOrder[0].f2_19[0]", "245");
     */


            // fill(fields, "topmostSubform[0].CopyD[0].f2_42[0]", "271");

            //fill(fields, "topmostSubform[0].CopyA[0].Col_Left[0].f1_04[0]", "4");  // Controll number, for searching and tracking person in payroll system. im not supporting


            // fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_22[0]", "22"); // 12b code
            //  fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_23[0]", "23"); // 12b amount
            //  fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_24[0]", "24"); // 12c code
            //  fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_25[0]", "25"); // 12c amount
            //  fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_26[0]", "26"); // 12d code
            // fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Line12_ReadOrder[0].f1_27[0]", "27"); // 12d amount
            //  fill(fields, "topmostSubform[0].CopyA[0].Boxes15_ReadOrder[0].f1_32[0]", "35");                    // Employer state ID 2

            // State wages and taxes
            //  fill(fields, "topmostSubform[0].CopyA[0].Box16_ReadOrder[0].f1_34[0]", "37"); // State wages 2
//    fill(fields, "topmostSubform[0].CopyA[0].Box17_ReadOrder[0].f1_36[0]", "39"); // State tax 2
            //  fill(fields, "topmostSubform[0].CopyA[0].Boxes15_ReadOrder[0].f1_31[0]", "34");                    // State 2
            // Local wages and taxes

            //   fill(fields, "topmostSubform[0].CopyA[0].Box18_ReadOrder[0].f1_38[0]", "41"); // Local wages 2
            //   fill(fields, "topmostSubform[0].CopyA[0].Box19_ReadOrder[0].f1_40[0]", "43"); // Local tax 2
            //fill(fields, "topmostSubform[0].CopyA[0].f1_42[0]", "45"); // Locality 2

            // Checkboxes
            //   fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Statutory_ReadOrder[0].c1_2[0]", "28"); // Statutory employee
            //   fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].Retirement_ReadOrder[0].c1_3[0]", "29"); // Retirement plan
            //  fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].c1_4[0]", "30");                         // Third-party sick pay

            fill(fields, "topmostSubform[0].CopyA[0].Col_Right[0].f1_28[0]", box14Value); // Box 14 - Other


            pdfDoc.removePage(1);
            pdfDoc.removePage(4);
            pdfDoc.removePage(5);
            pdfDoc.removePage(6);
            pdfDoc.removePage(7);

            //    pdfDoc.removePage(9);
            //   pdfDoc.removePage(11);

            // form.flattenFields();
            pdfDoc.close();

            byte[] pdfBytes = baos.toByteArray();

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String workerKeyPart = String.format("%s_%s",
                    user.getFirstName().trim().replaceAll("\\s+", "_"),
                    user.getLastName().trim().replaceAll("\\s+", "_"));

// 2. Имя файла (можете скорректировать по своему вкусу)
            String fileName = String.format("w2_%d_%s_%s.pdf",
                    companyId,
                    user.getFirstName().toLowerCase(),
                    user.getLastName().toLowerCase());

// 3. Собираем полный ключ
            String key = String.format("%s/%d/OfficialW2/%s/%s",
                    companyKeyPart,
                    companyId,
                    workerKeyPart,
                    fileName);

// 4. Загружаем в S3
            long ms = System.currentTimeMillis();
            amazonS3Service.uploadPdfToS3(pdfBytes, key);
            long end = System.currentTimeMillis() - ms;

            log.info("✅ Form W2 uploaded to S3: {}", key);
            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end);
            metric.recordOperationTime(timer,"W2_success");

            return pdfBytes;
        } catch (Exception e) {
            metric.recordOperationTime(timer,"W2_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("W2_failed", e.getMessage(), e);
            throw e;
        }
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


    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }


}
