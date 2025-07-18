package com.zikpak.facecheck.taxesServices.efiles.xml;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.dto.Form941Data;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class Form941XmlGenerator {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository taxRecordRepo;
    private final PaymentHistoryService paymentHistoryService;
    private final AmazonS3Service amazonS3Service;

    public String generateForm941Xml(Integer userId, Integer companyId, int year, int quarter) throws Exception {

        // Get user and company data (same as PDF service)
        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        // Get quarterly data (same as PDF service)
        Form941Data data = getQuarterly941Data(companyId, year, quarter);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Root element - IRS MeF Package structure
        Element returnPackage = doc.createElement("Return");
        returnPackage.setAttribute("xmlns", "http://www.irs.gov/efile");
        returnPackage.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        returnPackage.setAttribute("xsi:schemaLocation",
                "http://www.irs.gov/efile https://www.irs.gov/pub/irs-schema/" + year + "/IRS941v" + getSchemaVersion(year) + ".xsd");
        doc.appendChild(returnPackage);

        // Return Header (required by MeF)
        addReturnHeader(doc, returnPackage, company, year, quarter, admin);

        // Return Data container
        Element returnData = doc.createElement("ReturnData");
        returnPackage.appendChild(returnData);

        // Form 941 element with correct taxPeriod format
        Element form941 = doc.createElement("IRS941");
        returnData.appendChild(form941);

        // Header section
        addHeaderSection(doc, form941, company, year, quarter);

        // Part 1: Answer these questions for this quarter
        addPart1(doc, form941, data, companyId, year, quarter);

        // Part 2: Tell us about your deposit schedule and tax liability
        addPart2(doc, form941, data, companyId, year, quarter);

        String xmlContent = documentToString(doc);

        // Upload to S3 (same as PDF service)
        uploadXmlToS3(company, companyId, year, quarter, xmlContent);

        return xmlContent;
    }

    /**
     * Add IRS MeF Return Header (required for production e-filing)
     */
    private void addReturnHeader(Document doc, Element returnPackage, Object company, int year, int quarter, User admin) {
        Element returnHeader = doc.createElement("ReturnHeader");

        // Submission ID (клиент заменит на свой)
        Element submissionId = doc.createElement("SubmissionId");
        submissionId.setTextContent("TEMP" + System.currentTimeMillis()); // Временный ID
        returnHeader.appendChild(submissionId);

        // Tax Year
        Element taxYear = doc.createElement("TaxYear");
        taxYear.setTextContent(String.valueOf(year));
        returnHeader.appendChild(taxYear);

        // Tax Period End Date
        Element taxPeriodEndDate = doc.createElement("TaxPeriodEndDate");
        taxPeriodEndDate.setTextContent(getQuarterEndDate(year, quarter));
        returnHeader.appendChild(taxPeriodEndDate);

        // Quarter Ending Date
        Element quarterEndingDt = doc.createElement("QuarterEndingDt");
        quarterEndingDt.setTextContent(getQuarterEndDate(year, quarter));
        returnHeader.appendChild(quarterEndingDt);

        // УБРАТЬ Transmitter - его добавит налоговый агент клиента

        // Return Type
        Element returnType = doc.createElement("ReturnType");
        returnType.setTextContent("941");
        returnHeader.appendChild(returnType);

        // Third Party Designee (клиент может изменить)
        Element discussWithThirdParty = doc.createElement("DiscussWithThirdPartyNoInd");
        discussWithThirdParty.setTextContent("true");
        returnHeader.appendChild(discussWithThirdParty);

        // Signature (данные клиента)
        Element signatureDocument = doc.createElement("SignatureDocumentGrp");

        Element personName = doc.createElement("PersonNm");
        personName.setTextContent(admin.fullName());
        signatureDocument.appendChild(personName);

        Element signatureDate = doc.createElement("SignatureDt");
        signatureDate.setTextContent(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        signatureDocument.appendChild(signatureDate);

        Element daytimePhone = doc.createElement("DaytimePhoneNum");
        daytimePhone.setTextContent(admin.getPhoneNumber());
        signatureDocument.appendChild(daytimePhone);

        returnHeader.appendChild(signatureDocument);
        returnPackage.appendChild(returnHeader);
    }
    /**
     * Format tax period correctly for IRS MeF
     * Q1 → YYYY03, Q2 → YYYY06, Q3 → YYYY09, Q4 → YYYY12
     */
    private String formatTaxPeriod(int year, int quarter) {
        int lastMonth = quarter * 3; // Q1=3, Q2=6, Q3=9, Q4=12
        return String.format("%04d%02d", year, lastMonth);
    }

    /**
     * Get quarter end date in YYYY-MM-DD format
     */
    private String getQuarterEndDate(int year, int quarter) {
        LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
        return quarterEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Generate submission ID for MeF
     */
    private String generateSubmissionId(Integer companyId, int year, int quarter) {
        return String.format("FACE%04d%02d%05d%08d", year, quarter, companyId, System.currentTimeMillis() % 100000000);
    }

    /**
     * Get XSD schema version for the tax year
     */
    private String getSchemaVersion(int year) {
        // IRS updates schema versions annually
        return switch (year) {
            case 2024 -> "1.0";
            case 2025 -> "1.0";
            default -> "1.0";
        };
    }

    private void addHeaderSection(Document doc, Element root, Object company, int year, int quarter) {
        // Убираем создание отдельного Header элемента - добавляем напрямую в IRS941

        // EIN без дефиса (IRS в XML использует без дефиса)
        Element ein = doc.createElement("EmployerEIN");
        String einValue = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN();
        // Убираем дефис если есть
        einValue = einValue.replace("-", "");
        ein.setTextContent(einValue);
        root.appendChild(ein);

        // Company name
        Element companyName = doc.createElement("BusinessNameLine1Txt");
        companyName.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyName());
        root.appendChild(companyName);

        // Address
        Element address = doc.createElement("AddressLine1Txt");
        address.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyAddress());
        root.appendChild(address);

        Element city = doc.createElement("CityNm");
        city.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyCity());
        root.appendChild(city);

        Element state = doc.createElement("StateAbbreviationCd");
        state.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyState());
        root.appendChild(state);

        Element zip = doc.createElement("ZIPCd");
        zip.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyZipCode());
        root.appendChild(zip);
    }

    private void addPart1(Document doc, Element root, Form941Data data, Integer companyId, int year, int quarter) {
        // Удаляем создание Part1 - элементы добавляются напрямую в IRS941

        // Line 1: Number of employees
        Element line1 = doc.createElement("EmployeeCnt");
        line1.setTextContent(String.valueOf(data.getEmployeeCount()));
        root.appendChild(line1);

        // Line 2: Wages, tips, and other compensation
        Element line2 = doc.createElement("WagesAmt");
        line2.setTextContent(formatAmount(data.getTotalGross()));
        root.appendChild(line2);

        // Line 3: Federal income tax withheld
        Element line3 = doc.createElement("FederalIncomeTaxWithheldAmt");
        line3.setTextContent(formatAmount(data.getTotalFederalWithholding()));
        root.appendChild(line3);

        // Checkbox for wages not subject to SS/Medicare (если нет зарплат)
        if (data.getTotalGross().compareTo(BigDecimal.ZERO) == 0) {
            Element noWages = doc.createElement("WagesNotSubjToSSMedcrTaxInd");
            noWages.setTextContent("true");
            root.appendChild(noWages);
            return; // Если нет зарплат, дальше не заполняем
        }

        // Line 5a: Social Security Wages and Tax (ГРУППА)
        Element ssGroup = doc.createElement("SocialSecurityWageAndTaxGrp");

        Element ssWages = doc.createElement("SocialSecurityTaxCashWagesAmt");
        ssWages.setTextContent(formatAmount(data.getSsTaxableWages()));
        ssGroup.appendChild(ssWages);

        BigDecimal ssTax = data.getSsTaxableWages().multiply(new BigDecimal("0.124"));
        Element ssTaxElement = doc.createElement("SocialSecurityTaxAmt");
        ssTaxElement.setTextContent(formatAmount(ssTax));
        ssGroup.appendChild(ssTaxElement);

        root.appendChild(ssGroup);

        // Line 5b: Social Security Tips and Tax (ГРУППА)
        Element ssTipsGroup = doc.createElement("SocialSecurityTipsAndTaxGrp");

        Element ssTips = doc.createElement("TaxableSocSecTipsAmt");
        ssTips.setTextContent(formatAmount(data.getSsTaxableTips()));
        ssTipsGroup.appendChild(ssTips);

        BigDecimal ssTipsTax = data.getSsTaxableTips().multiply(new BigDecimal("0.124"));
        Element ssTipsTaxElement = doc.createElement("TaxOnSocialSecurityTipsAmt");
        ssTipsTaxElement.setTextContent(formatAmount(ssTipsTax));
        ssTipsGroup.appendChild(ssTipsTaxElement);

        root.appendChild(ssTipsGroup);

        // Line 5c: Medicare Wages and Tax (ГРУППА)
        Element medicareGroup = doc.createElement("MedicareWageTipsAndTaxGrp");

        Element medicareWages = doc.createElement("TaxableMedicareWagesTipsAmt");
        medicareWages.setTextContent(formatAmount(data.getMedicareTaxableWages()));
        medicareGroup.appendChild(medicareWages);

        BigDecimal medicareTax = data.getMedicareTaxableWages().multiply(new BigDecimal("0.029"));
        Element medicareTaxElement = doc.createElement("TaxOnMedicareWagesTipsAmt");
        medicareTaxElement.setTextContent(formatAmount(medicareTax));
        medicareGroup.appendChild(medicareTaxElement);

        root.appendChild(medicareGroup);

        // Line 5d: Additional Medicare Tax (ГРУППА)
        Element addMedicareGroup = doc.createElement("AddnlMedicareWageTipsAndTaxGrp");

        Element addMedicareWages = doc.createElement("TxblWageTipsSubjAddnlMedcrAmt");
        addMedicareWages.setTextContent(formatAmount(data.getAdditionalMedicareTaxableWages()));
        addMedicareGroup.appendChild(addMedicareWages);

        BigDecimal addMedicareTax = data.getAdditionalMedicareTaxableWages().multiply(new BigDecimal("0.009"));
        Element addMedicareTaxElement = doc.createElement("TaxOnWageTipsSubjAddnlMedcrAmt");
        addMedicareTaxElement.setTextContent(formatAmount(addMedicareTax));
        addMedicareGroup.appendChild(addMedicareTaxElement);

        root.appendChild(addMedicareGroup);

        // Line 5e: Total SS and Medicare taxes
        BigDecimal totalSSMedicare = ssTax.add(ssTipsTax).add(medicareTax).add(addMedicareTax);
        Element line5e = doc.createElement("TotalSSMdcrTaxAmt");
        line5e.setTextContent(formatAmount(totalSSMedicare));
        root.appendChild(line5e);

        // Line 5f: Section 3121(q) Notice
        Element line5f = doc.createElement("TaxOnUnreportedTips3121qAmt");
        line5f.setTextContent("0.00");
        root.appendChild(line5f);

        // Line 6: Total taxes before adjustments
        BigDecimal line6 = data.getTotalFederalWithholding().add(totalSSMedicare);
        Element line6Element = doc.createElement("TotalTaxBeforeAdjustmentAmt");
        line6Element.setTextContent(formatAmount(line6));
        root.appendChild(line6Element);

        // Line 7: Current quarter adjustments for fractions of cents
        Element line7 = doc.createElement("CurrentQtrFractionsCentsAmt");
        line7.setTextContent("0.00");
        root.appendChild(line7);

        // Line 8: Current quarter adjustment for sick pay
        Element line8 = doc.createElement("CurrentQuarterSickPaymentAmt");
        line8.setTextContent("0.00");
        root.appendChild(line8);

        // Line 9: Current quarter adjustments for tips and group-term life insurance
        Element line9 = doc.createElement("CurrQtrTipGrpTermLifeInsAdjAmt");
        line9.setTextContent("0.00");
        root.appendChild(line9);

        // Line 10: Total taxes after adjustments
        BigDecimal line10 = line6; // Same as line 6 if no adjustments
        Element line10Element = doc.createElement("TotalTaxAfterAdjustmentAmt");
        line10Element.setTextContent(formatAmount(line10));
        root.appendChild(line10Element);

        // Line 11: Qualified small business payroll tax credit
        Element line11 = doc.createElement("PayrollTaxCreditAmt");
        line11.setTextContent("0.00");
        root.appendChild(line11);

        // Line 12: Total taxes after adjustments and nonrefundable credits
        BigDecimal line12 = line10;
        Element line12Element = doc.createElement("TotalTaxAmt");
        line12Element.setTextContent(formatAmount(line12));
        root.appendChild(line12Element);

        // Line 13: Total deposits for this quarter
        BigDecimal depositedAmount = paymentHistoryService.getTotalPaymentsForQuarter941Form(companyId, quarter, year);
        Element line13 = doc.createElement("TotalTaxDepositAmt");
        line13.setTextContent(formatAmount(depositedAmount));
        root.appendChild(line13);

        // Line 14: Balance due
        BigDecimal balanceDue = line12.subtract(depositedAmount);
        if (balanceDue.compareTo(BigDecimal.ZERO) > 0) {
            Element line14 = doc.createElement("BalanceDueAmt");
            line14.setTextContent(formatAmount(balanceDue));
            root.appendChild(line14);
        }

        // Line 15: Overpayment
        BigDecimal overpayment = depositedAmount.subtract(line12);
        if (overpayment.compareTo(BigDecimal.ZERO) > 0) {
            Element overpaymentGroup = doc.createElement("OverpaymentGrp");

            Element overpaidAmt = doc.createElement("OverpaidAmt");
            overpaidAmt.setTextContent(formatAmount(overpayment));
            overpaymentGroup.appendChild(overpaidAmt);

            // По умолчанию применяем к следующей декларации
            Element applyNext = doc.createElement("ApplyOverpaymentNextReturnInd");
            applyNext.setTextContent("true");
            overpaymentGroup.appendChild(applyNext);

            root.appendChild(overpaymentGroup);
        }
    }


    private void addPart2(Document doc, Element root, Form941Data data, Integer companyId, int year, int quarter) {

        BigDecimal totalTax = calculateTotalTax(data);

        if (totalTax.compareTo(new BigDecimal("2500")) < 0) {
            // Line 16 - checkbox 1
            Element totalTaxLessThan = doc.createElement("TotalTaxLessThanLimitAmtInd");
            totalTaxLessThan.setTextContent("true");
            root.appendChild(totalTaxLessThan);
        } else {
            // Monthly or semiweekly depositor
            if (isMonthlyDepositor(data, companyId, year, quarter)) {
                Element monthlyGrp = doc.createElement("MonthlyScheduleDepositorGrp");

                Element monthlyInd = doc.createElement("MonthlyScheduleDepositorInd");
                monthlyInd.setTextContent("true");
                monthlyGrp.appendChild(monthlyInd);

                // Add monthly liability amounts
                addMonthlyLiabilityToGroup(doc, monthlyGrp, companyId, year, quarter);

                root.appendChild(monthlyGrp);
            } else {
                Element semiweeklyInd = doc.createElement("SemiweeklyScheduleDepositorInd");
                semiweeklyInd.setTextContent("true");
                root.appendChild(semiweeklyInd);
            }
        }
    }

    /**
     * Определяет, является ли депозитор месячным или полунедельным
     */
    private boolean isMonthlyDepositor(Form941Data data, Integer companyId, int year, int quarter) {
        BigDecimal currentTax = calculateTotalTax(data);

        // Проверяем предыдущий квартал
        int prevQuarter = (quarter == 1) ? 4 : quarter - 1;
        int prevYear = (quarter == 1) ? year - 1 : year;

        try {
            Form941Data prevData = getQuarterly941Data(companyId, prevYear, prevQuarter);
            BigDecimal prevTax = calculateTotalTax(prevData);

            // Месячный, если текущий и предыдущий кварталы < $2,500 И не было $100,000+ в день
            return (currentTax.compareTo(new BigDecimal("2500")) < 0 ||
                    prevTax.compareTo(new BigDecimal("2500")) < 0) &&
                    currentTax.compareTo(new BigDecimal("100000")) < 0;

        } catch (Exception e) {
            // Если нет данных за предыдущий квартал, считаем месячным если < $2,500
            return currentTax.compareTo(new BigDecimal("2500")) < 0 &&
                    currentTax.compareTo(new BigDecimal("100000")) < 0;
        }
    }

    /**
     * Добавляет месячные обязательства в группу для месячных депозиторов
     */
    private void addMonthlyLiabilityToGroup(Document doc, Element monthlyGroup, Integer companyId, int year, int quarter) {
        // Вычисляем обязательства для каждого месяца квартала
        int startMonth = (quarter - 1) * 3 + 1;

        BigDecimal month1Liability = calculateLine12ForMonth(companyId, year, startMonth);
        BigDecimal month2Liability = calculateLine12ForMonth(companyId, year, startMonth + 1);
        BigDecimal month3Liability = calculateLine12ForMonth(companyId, year, startMonth + 2);

        // Месяц 1
        Element month1Element = doc.createElement("TaxLiabilityMonth1Amt");
        month1Element.setTextContent(formatAmount(month1Liability));
        monthlyGroup.appendChild(month1Element);

        // Месяц 2
        Element month2Element = doc.createElement("TaxLiabilityMonth2Amt");
        month2Element.setTextContent(formatAmount(month2Liability));
        monthlyGroup.appendChild(month2Element);

        // Месяц 3
        Element month3Element = doc.createElement("TaxLiabilityMonth3Amt");
        month3Element.setTextContent(formatAmount(month3Liability));
        monthlyGroup.appendChild(month3Element);

        // Общая сумма за квартал
        BigDecimal totalQuarter = month1Liability.add(month2Liability).add(month3Liability);
        Element totalElement = doc.createElement("TotalQuarterTaxLiabilityAmt");
        totalElement.setTextContent(formatAmount(totalQuarter));
        monthlyGroup.appendChild(totalElement);
    }


    private void addMonthlyLiability(Document doc, Element part2, Integer companyId, int year, int quarter, BigDecimal quarterlyTotal) {
        Element monthlyLiability = doc.createElement("MonthlyTaxLiability");
        part2.appendChild(monthlyLiability);

        // Calculate monthly amounts (same as PDF logic)
        BigDecimal m1 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 1);
        BigDecimal m2 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 2);
        BigDecimal m3 = calculateLine12ForMonth(companyId, year, (quarter - 1) * 3 + 3);

        Element month1 = doc.createElement("Month1");
        month1.setTextContent(formatAmount(m1));
        monthlyLiability.appendChild(month1);

        Element month2 = doc.createElement("Month2");
        month2.setTextContent(formatAmount(m2));
        monthlyLiability.appendChild(month2);

        Element month3 = doc.createElement("Month3");
        month3.setTextContent(formatAmount(m3));
        monthlyLiability.appendChild(month3);

        Element total = doc.createElement("TotalMonthlyLiability");
        total.setTextContent(formatAmount(quarterlyTotal));
        monthlyLiability.appendChild(total);
    }

    private void addPart3(Document doc, Element root) {

    }

    private void addPart4(Document doc, Element root) {

    }

    private void addPart5(Document doc, Element root, Object admin) {

    }

    // Helper methods (same as PDF service)
    public Form941Data getQuarterly941Data(int companyId, int year, int quarter) {
        LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate end = start.plusMonths(3).minusDays(1);

        Form941Data data = new Form941Data();
        data.setEmployeeCount(taxRecordRepo.countDistinctEmployeesByCompanyAndYear(companyId, start, end));
        data.setTotalGross(taxRecordRepo.sumGrossWages(companyId, start, end));
        data.setTotalFederalWithholding(taxRecordRepo.sumFederalWithholding(companyId, start, end));
        data.setSsTaxableWages(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, start, end));
        data.setSsTaxableTips(taxRecordRepo.sumSocialSecurityTips(companyId, start, end));
        data.setMedicareTaxableWages(taxRecordRepo.sumMedicareTaxableWages(companyId, start, end));
        data.setAdditionalMedicareTaxableWages(taxRecordRepo.sumAdditionalMedicareTaxableWages(companyId, start, end));

        return data;
    }

    private BigDecimal calculateLine10(Form941Data d) {
        BigDecimal line3 = d.getTotalFederalWithholding();
        BigDecimal tax5a = d.getSsTaxableWages().multiply(new BigDecimal("0.124"));
        BigDecimal tax5b = d.getSsTaxableTips().multiply(new BigDecimal("0.124"));
        BigDecimal tax5c = d.getMedicareTaxableWages().multiply(new BigDecimal("0.029"));
        BigDecimal tax5d = d.getAdditionalMedicareTaxableWages().multiply(new BigDecimal("0.009"));
        BigDecimal line5e = tax5a.add(tax5b).add(tax5c).add(tax5d);

        return line3.add(line5e);
    }

    private BigDecimal calculateLine12ForMonth(Integer companyId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        Form941Data monthlyData = get941DataForPeriod(companyId, start, end);
        return calculateLine10(monthlyData);
    }

    private Form941Data get941DataForPeriod(Integer companyId, LocalDate start, LocalDate end) {
        Form941Data d = new Form941Data();
        d.setEmployeeCount(taxRecordRepo.countDistinctEmployeesByCompanyAndYear(companyId, start, end));
        d.setTotalGross(taxRecordRepo.sumGrossWages(companyId, start, end));
        d.setTotalFederalWithholding(taxRecordRepo.sumFederalWithholding(companyId, start, end));
        d.setSsTaxableWages(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, start, end));
        d.setSsTaxableTips(taxRecordRepo.sumSocialSecurityTips(companyId, start, end));
        d.setMedicareTaxableWages(taxRecordRepo.sumMedicareTaxableWages(companyId, start, end));
        d.setAdditionalMedicareTaxableWages(taxRecordRepo.sumAdditionalMedicareTaxableWages(companyId, start, end));
        return d;
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal calculateTotalTax(Form941Data data) {
        BigDecimal federalTax = data.getTotalFederalWithholding();
        BigDecimal ssTax = data.getSsTaxableWages().multiply(new BigDecimal("0.124"));
        BigDecimal medicareTax = data.getMedicareTaxableWages().multiply(new BigDecimal("0.029"));
        BigDecimal additionalMedicareTax = data.getAdditionalMedicareTaxableWages().multiply(new BigDecimal("0.009"));

        return federalTax.add(ssTax).add(medicareTax).add(additionalMedicareTax);
    }

    private String generateDocumentId(Integer companyId, int year, int quarter) {
        return String.format("941_%d_%d_%d_%d", companyId, year, quarter, System.currentTimeMillis());
    }

    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        String xmlContent = writer.getBuffer().toString();

        String comment = "<!-- \n" +
                "  ВАЖНО: Этот XML файл сгенерирован для справки.\n" +
                "  Перед подачей в IRS необходимо:\n" +
                "  1. Добавить Transmitter информацию (EIN и Name вашего налогового агента)\n" +
                "  2. Заменить SubmissionId на уникальный ID от вашего софта\n" +
                "  3. Проверить все данные и подпись\n" +
                "  4. Валидировать против IRS XSD схемы\n" +
                "-->\n";

        return xmlContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + comment);
    }

    private void uploadXmlToS3(Object company, Integer companyId, int year, int quarter, String xmlContent) {
        String companyKeyPart = ((com.zikpak.facecheck.entity.Company)company).getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f941_%d_%d_%d.xml", companyId, year, quarter);
        String key = String.format("%s/%d/941form/941Xml/%d/%d/%s",
                companyKeyPart, companyId, year, quarter, fileName);

        amazonS3Service.uploadPdfToS3(xmlContent.getBytes(), key);
    }
}