package com.zikpak.facecheck.taxesServices.efiles.xml;

import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm940SA;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class Form940XmlGenerator {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private final AmazonS3Service amazonS3Service;
    private final FillForm940SA fillForm940SA;

    /**
     * Generate official IRS Form 940 XML e-file
     */
    public String generateForm940Xml(Integer userId, Integer companyId, int year) throws Exception {
        log.info("🏛️ Generating official IRS Form 940 XML e-file for company {} year {}", companyId, year);

        // Get user and company data
        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Root element - IRS MeF Package structure for Form 940
        Element returnElement = doc.createElementNS("http://www.irs.gov/efile", "Return");
        doc.appendChild(returnElement);
        returnElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        returnElement.setAttribute("xsi:schemaLocation",
                "http://www.irs.gov/efile https://www.irs.gov/pub/irs-schema/" + year + "/IRS940v" + getSchemaVersion(year) + ".xsd");

        // Return Header (required by MeF)
        addReturnHeader(doc, returnElement, company, year);

        // Return Data container
        Element returnData = doc.createElement("ReturnData");
        returnElement.appendChild(returnData);

        // Form 940 element
        Element form940 = doc.createElement("IRS940");
        form940.setAttribute("documentId", generateDocumentId(companyId, year));
        returnData.appendChild(form940);

        // Header section with company info
     //   addHeaderSection(doc, form940, company, year);

        // Part 1: Answer these questions about your payroll
  //      addPart1(doc, form940, companyId, year);

        // Part 2: Determine your FUTA tax before adjustments
    //    addPart2WithIRSNames(doc, form940, companyId, year);

        // Part 3: Determine your adjustments
       // addPart3(doc, form940);

        // Part 4: Determine your FUTA tax and balance due or overpayment
      //  addPart4(doc, form940, companyId, year);

     //   addCreditReductionSection(doc, form940, company, companyId, year);

        // Part 5: Report your FUTA tax liability by quarter
     //   addPart5(doc, form940, companyId, year);

        // Part 6: Third party designee
      //  addPart6(doc, form940);

        // Part 7: Sign here
     //   addPart7(doc, form940, admin);
        addAllFieldsFlat(doc, form940, companyId, year, company, admin);


        String xmlContent = documentToString(doc);

        // Upload to S3
        uploadXmlToS3(company, companyId, year, xmlContent);

        log.info("✅ Form 940 XML e-file generated successfully for company {} year {}", companyId, year);
        return xmlContent;
    }

    /**
     * Add IRS MeF Return Header (required for production e-filing)
     */
    private void addReturnHeader(Document doc, Element returnElement, Object company, int year) {
        Element returnHeader = doc.createElement("ReturnHeader");
        returnHeader.setAttribute("binaryAttachmentCnt", "0");
        returnElement.appendChild(returnHeader);

        // Правильные элементы для IRS MeF
        addElement(doc, returnHeader, "TaxYr", String.valueOf(year));
        addElement(doc, returnHeader, "TaxPeriodBeginDt", year + "-01-01");
        addElement(doc, returnHeader, "TaxPeriodEndDt", year + "-12-31");

        // Software ID (должен быть зарегистрирован в IRS)
        addElement(doc, returnHeader, "SoftwareId", "00000000"); // Placeholder - нужен реальный ID
        addElement(doc, returnHeader, "SoftwareVersionNum", "2024.1.0");

        // Originator Group (требуется для e-file)
        Element originatorGrp = doc.createElement("OriginatorGrp");
        returnHeader.appendChild(originatorGrp);
        addElement(doc, originatorGrp, "EFIN", "000000"); // Electronic Filing ID Number - нужен реальный
        addElement(doc, originatorGrp, "OriginatorTypeCd", "ERO"); // Electronic Return Originator

        // Return Type Code
        addElement(doc, returnHeader, "ReturnTypeCd", "940");

        // Filer information
        Element filer = doc.createElement("Filer");
        returnHeader.appendChild(filer);

        // EIN без дефисов для IRS
        String ein = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN().replaceAll("-", "");
        addElement(doc, filer, "EIN", ein);

        // Business Name structure
        Element businessName = doc.createElement("BusinessName");
        filer.appendChild(businessName);
        addElement(doc, businessName, "BusinessNameLine1Txt",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyName());

        // US Address structure
        Element usAddress = doc.createElement("USAddress");
        filer.appendChild(usAddress);
        addElement(doc, usAddress, "AddressLine1Txt",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyAddress());
        addElement(doc, usAddress, "CityNm",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyCity());
        addElement(doc, usAddress, "StateAbbreviationCd",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyState());
        addElement(doc, usAddress, "ZIPCd",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyZipCode());
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private void addHeaderSection(Document doc, Element form940, Object company, int year) {
        Element header = doc.createElement("Header");
        form940.appendChild(header);

        // EIN (format: NN-NNNNNNN)
        Element ein = doc.createElement("EIN");
        String einValue = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN();
        if (!einValue.contains("-") && einValue.length() == 9) {
            einValue = einValue.substring(0, 2) + "-" + einValue.substring(2);
        }
        ein.setTextContent(einValue);
        header.appendChild(ein);

        // Business Name
        Element businessName = doc.createElement("BusinessName");
        businessName.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyName());
        header.appendChild(businessName);

        // Address
        Element address = doc.createElement("BusinessAddress");
        header.appendChild(address);

        Element addressLine1 = doc.createElement("AddressLine1");
        addressLine1.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyAddress());
        address.appendChild(addressLine1);

        Element city = doc.createElement("City");
        city.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyCity());
        address.appendChild(city);

        Element state = doc.createElement("State");
        state.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyState());
        address.appendChild(state);

        Element zipCode = doc.createElement("ZIPCode");
        zipCode.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyZipCode());
        address.appendChild(zipCode);

        // Tax Year
        Element taxYear = doc.createElement("TaxYear");
        taxYear.setTextContent(String.valueOf(year));
        header.appendChild(taxYear);
    }

    private void addPart1(Document doc, Element form940, Integer companyId, int year) {
        Element part1 = doc.createElement("Part1");
        form940.appendChild(part1);

        // Line 1a: Rate of pay (checkbox - usually 0.006)
        Element line1a = doc.createElement("Line1a");
        line1a.setTextContent("true"); // 0.6% rate applies
        part1.appendChild(line1a);

        // Line 1b: Rate if different (not applicable for standard rate)
        Element line1b = doc.createElement("Line1b");
        line1b.setTextContent("false");
        part1.appendChild(line1b);
    }
    

    private void addPart3(Document doc, Element form940) {
        Element part3 = doc.createElement("Part3");
        form940.appendChild(part3);

        // Line 9: If ALL payments made to employees are exempt from FUTA tax
        Element line9 = doc.createElement("Line9");
        line9.setTextContent("0.00");
        part3.appendChild(line9);

        // Line 10: If ANY payments made to employees are exempt from FUTA tax
        Element line10 = doc.createElement("Line10");
        line10.setTextContent("0.00");
        part3.appendChild(line10);
    }

    private void addPart4(Document doc, Element form940, Integer companyId, int year) {
        Element part4 = doc.createElement("Part4");
        form940.appendChild(part4);

        // Calculate values from Part 2
        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        BigDecimal line5 = calculateLine5ExcessWages(companyId, year);
        BigDecimal line7 = line3.subtract(line5);
        BigDecimal line8 = line7.multiply(new BigDecimal("0.006")).setScale(2, RoundingMode.HALF_UP);

        // Line 11: Total FUTA tax after adjustments
        BigDecimal scheduleACredit = fillForm940SA.getNYCreditReduction(companyId, year);
        BigDecimal line11 = line8.add(scheduleACredit);

        // Используем правильное IRS название
        Element totalFutaTax = doc.createElement("TotalFUTATaxAfterAdjustmentAmt");
        totalFutaTax.setTextContent(formatAmount(line11));
        part4.appendChild(totalFutaTax);

        // Line 12: Total FUTA tax deposited
        BigDecimal line12 = paymentHistoryIrsRepository.getTotalPaidForFUTA(companyId, year);
        Element totalDeposited = doc.createElement("TotalFUTATaxDepositedAmt");
        totalDeposited.setTextContent(formatAmount(line12));
        part4.appendChild(totalDeposited);

        // Line 13: Balance due
        BigDecimal balanceDue = line11.subtract(line12);
        if (balanceDue.compareTo(BigDecimal.ZERO) > 0) {
            Element balanceDueElement = doc.createElement("BalanceDueAmt");
            balanceDueElement.setTextContent(formatAmount(balanceDue));
            part4.appendChild(balanceDueElement);
        }

        // Line 14: Overpayment
        BigDecimal overpayment = line12.subtract(line11);
        if (overpayment.compareTo(BigDecimal.ZERO) > 0) {
            Element overpaymentElement = doc.createElement("OverpaymentAmt");
            overpaymentElement.setTextContent(formatAmount(overpayment));
            part4.appendChild(overpaymentElement);
        }
    }

    private void addPart5(Document doc, Element form940, Integer companyId, int year) {
        Element part5 = doc.createElement("Part5");
        form940.appendChild(part5);

        // Calculate quarterly FUTA liability
        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        BigDecimal line5 = calculateLine5ExcessWages(companyId, year);
        BigDecimal line7 = line3.subtract(line5);
        BigDecimal totalFutaLiability = line7.multiply(new BigDecimal("0.006")).setScale(2, RoundingMode.HALF_UP);

        // If total liability > $500, show quarterly breakdown
        if (totalFutaLiability.compareTo(new BigDecimal("500")) > 0) {
            // Calculate quarterly breakdown (simplified - equal distribution)
            BigDecimal quarterlyAmount = totalFutaLiability.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);

            // Используем IRS названия для квартальных платежей
            Element firstQuarter = doc.createElement("FirstQuarterLiabilityAmt");
            firstQuarter.setTextContent(formatAmount(quarterlyAmount));
            part5.appendChild(firstQuarter);

            Element secondQuarter = doc.createElement("SecondQuarterLiabilityAmt");
            secondQuarter.setTextContent(formatAmount(quarterlyAmount));
            part5.appendChild(secondQuarter);

            Element thirdQuarter = doc.createElement("ThirdQuarterLiabilityAmt");
            thirdQuarter.setTextContent(formatAmount(quarterlyAmount));
            part5.appendChild(thirdQuarter);

            Element fourthQuarter = doc.createElement("FourthQuarterLiabilityAmt");
            fourthQuarter.setTextContent(formatAmount(quarterlyAmount));
            part5.appendChild(fourthQuarter);

            Element totalLiability = doc.createElement("TotalFUTATaxLiabilityAmt");
            totalLiability.setTextContent(formatAmount(totalFutaLiability));
            part5.appendChild(totalLiability);
        } else {
            // If $500 or less, все равно нужно показать 0
            Element firstQuarter = doc.createElement("FirstQuarterLiabilityAmt");
            firstQuarter.setTextContent("0.00");
            part5.appendChild(firstQuarter);

            Element secondQuarter = doc.createElement("SecondQuarterLiabilityAmt");
            secondQuarter.setTextContent("0.00");
            part5.appendChild(secondQuarter);

            Element thirdQuarter = doc.createElement("ThirdQuarterLiabilityAmt");
            thirdQuarter.setTextContent("0.00");
            part5.appendChild(thirdQuarter);

            Element fourthQuarter = doc.createElement("FourthQuarterLiabilityAmt");
            fourthQuarter.setTextContent("0.00");
            part5.appendChild(fourthQuarter);

            Element totalLiability = doc.createElement("TotalFUTATaxLiabilityAmt");
            totalLiability.setTextContent("0.00");
            part5.appendChild(totalLiability);
        }
    }

    private void addPart6(Document doc, Element form940) {
        Element part6 = doc.createElement("Part6");
        form940.appendChild(part6);

        // Third party designee (usually No)
        Element thirdPartyDesignee = doc.createElement("ThirdPartyDesignee");
        thirdPartyDesignee.setTextContent("No");
        part6.appendChild(thirdPartyDesignee);
    }

    private void addPart7(Document doc, Element form940, Object admin) {
        Element part7 = doc.createElement("Part7");
        form940.appendChild(part7);

        // Signature section
        Element signature = doc.createElement("Signature");
        part7.appendChild(signature);

        Element signedBy = doc.createElement("SignedBy");
        signedBy.setTextContent(((com.zikpak.facecheck.entity.User)admin).fullName());
        signature.appendChild(signedBy);

        Element title = doc.createElement("Title");
        title.setTextContent("Owner");
        signature.appendChild(title);

        Element phone = doc.createElement("Phone");
        phone.setTextContent(((com.zikpak.facecheck.entity.User)admin).getPhoneNumber());
        signature.appendChild(phone);

        Element date = doc.createElement("Date");
        date.setTextContent(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        signature.appendChild(date);

        Element signatureMethod = doc.createElement("SignatureMethod");
        signatureMethod.setTextContent("Electronic");
        signature.appendChild(signatureMethod);
    }

    /**
     * Calculate line 5 - excess wages over $7,000 per employee
     */
    private BigDecimal calculateLine5ExcessWages(Integer companyId, int year) {
        List<Object[]> rows = employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(companyId, year);
        BigDecimal result = BigDecimal.ZERO;

        for (Object[] row : rows) {
            BigDecimal yearlyTotal = (BigDecimal) row[1];
            BigDecimal excess = yearlyTotal.subtract(new BigDecimal("7000"));
            if (excess.compareTo(BigDecimal.ZERO) > 0) {
                result = result.add(excess);
            }
        }

        return result;
    }

    // Helper methods
    private String getSchemaVersion(int year) {
        return switch (year) {
            case 2024 -> "1.0";
            case 2025 -> "1.0";
            default -> "1.0";
        };
    }

    private String generateSubmissionId(Integer companyId, int year) {
        return String.format("FACE940_%04d_%05d_%08d", year, companyId, System.currentTimeMillis() % 100000000);
    }

    private String generateDocumentId(Integer companyId, int year) {
        return String.format("940_%d_%d_%d", companyId, year, System.currentTimeMillis());
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
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
        return writer.getBuffer().toString();
    }

    private void uploadXmlToS3(Object company, Integer companyId, int year, String xmlContent) {
        String companyKeyPart = ((com.zikpak.facecheck.entity.Company)company).getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f940_%d_%d.xml", companyId, year);
        String key = String.format("%s/%d/940form/940Xml/%d/%s",
                companyKeyPart, companyId, year, fileName);

        amazonS3Service.uploadPdfToS3(xmlContent.getBytes(), key);
        log.info("✅ Form 940 XML uploaded to S3: {}", key);
    }

    private static final Map<String, String> IRS_FIELD_MAPPING = Map.of(
            "Line3", "TotalPaymentAmt",
            "Line4", "ExemptPaymentAmt",
            "Line5", "PaymentsExceeding7000Amt",
            "Line6", "TotalExemptPaymentAmt",
            "Line7", "TotalTaxableFUTAWagesAmt",
            "Line8", "FUTATaxBeforeAdjustmentAmt",
            "Line11", "TotalFUTATaxAfterAdjustmentAmt",
            "Line12", "TotalFUTATaxDepositedAmt",
            "Line13", "BalanceDueAmt",
            "Line14", "OverpaymentAmt"
    );

    private Element createElement(Document doc, String lineNumber, String value) {
        String irsElementName = IRS_FIELD_MAPPING.getOrDefault(lineNumber, lineNumber);
        Element element = doc.createElement(irsElementName);
        element.setTextContent(value);
        return element;
    }


    private void addPart2WithIRSNames(Document doc, Element form940, Integer companyId, int year) {
        Element part2 = doc.createElement("Part2");
        form940.appendChild(part2);

        // Ваша логика остается той же, меняются только названия элементов

        // Line 3: Total payments to all employees
        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        Element totalPayments = doc.createElement("TotalPaymentAmt"); // IRS название вместо "Line3"
        totalPayments.setTextContent(formatAmount(line3));
        part2.appendChild(totalPayments);

        // Line 4: Payments exempt from FUTA tax
        Element exemptPayments = doc.createElement("ExemptPaymentAmt"); // IRS название вместо "Line4"
        exemptPayments.setTextContent("0.00");
        part2.appendChild(exemptPayments);

        // Line 5: Payments exceeding $7,000
        BigDecimal line5 = calculateLine5ExcessWages(companyId, year);
        Element excessPayments = doc.createElement("PaymentsExceeding7000Amt"); // IRS название вместо "Line5"
        excessPayments.setTextContent(formatAmount(line5));
        part2.appendChild(excessPayments);

        // Line 6: Total exempt (Line 4 + Line 5)
        BigDecimal line6 = line5;
        Element totalExempt = doc.createElement("TotalExemptPaymentAmt"); // IRS название вместо "Line6"
        totalExempt.setTextContent(formatAmount(line6));
        part2.appendChild(totalExempt);

        // Line 7: Total taxable FUTA wages
        BigDecimal line7 = line3.subtract(line6);
        Element taxableWages = doc.createElement("TotalTaxableFUTAWagesAmt"); // IRS название вместо "Line7"
        taxableWages.setTextContent(formatAmount(line7));
        part2.appendChild(taxableWages);

        // Line 8: FUTA tax before adjustments
        BigDecimal line8 = line7.multiply(new BigDecimal("0.006")).setScale(2, RoundingMode.HALF_UP);
        Element futaTax = doc.createElement("FUTATaxBeforeAdjustmentAmt"); // IRS название вместо "Line8"
        futaTax.setTextContent(formatAmount(line8));
        part2.appendChild(futaTax);
    }


    private void addCreditReductionSection(Document doc, Element form940, Object company, Integer companyId, int year) {
        // Только для NY компаний в 2024
        if ("NY".equals(((com.zikpak.facecheck.entity.Company)company).getCompanyState()) && year >= 2024) {
            Element creditReductionGrp = doc.createElement("CreditReductionStateGrp");
            form940.appendChild(creditReductionGrp);

            Element stateAbbr = doc.createElement("StateAbbreviationCd");
            stateAbbr.setTextContent("NY");
            creditReductionGrp.appendChild(stateAbbr);

            BigDecimal creditReduction = fillForm940SA.getNYCreditReduction(companyId, year);
            Element creditReductionAmt = doc.createElement("CreditReductionAmt");
            creditReductionAmt.setTextContent(formatAmount(creditReduction));
            creditReductionGrp.appendChild(creditReductionAmt);
        }
    }



    private void addAllFieldsFlat(Document doc, Element irs940, Integer companyId, int year, Object company, Object admin) {
        // Business info (прямо в IRS940, без Header секции)
        Element businessName = doc.createElement("BusinessName");
        businessName.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyName());
        irs940.appendChild(businessName);

        String ein = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN().replaceAll("-", "");
        addElement(doc, irs940, "EIN", ein);

        // Address
        Element usAddress = doc.createElement("USAddress");
        irs940.appendChild(usAddress);
        addElement(doc, usAddress, "AddressLine1Txt", ((com.zikpak.facecheck.entity.Company)company).getCompanyAddress());
        addElement(doc, usAddress, "CityNm", ((com.zikpak.facecheck.entity.Company)company).getCompanyCity());
        addElement(doc, usAddress, "StateAbbreviationCd", ((com.zikpak.facecheck.entity.Company)company).getCompanyState());
        addElement(doc, usAddress, "ZIPCd", ((com.zikpak.facecheck.entity.Company)company).getCompanyZipCode());

        // Questions (бывший Part 1)
        Element futaQuestionGrp = doc.createElement("FUTATaxLiabilityQuestionGrp");
        irs940.appendChild(futaQuestionGrp);
        addElement(doc, futaQuestionGrp, "PaymentsExceeded100000Ind", "false");
        addElement(doc, futaQuestionGrp, "AllWagesExemptFromFUTAInd", "false");

        // Calculations (бывший Part 2) - прямо в IRS940
        BigDecimal line3 = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        addElement(doc, irs940, "TotalPaymentAmt", formatAmount(line3));
        addElement(doc, irs940, "ExemptPaymentAmt", "0");

        BigDecimal line5 = calculateLine5ExcessWages(companyId, year);
        addElement(doc, irs940, "PaymentsExceeding7000Amt", formatAmount(line5));
        addElement(doc, irs940, "TotalExemptPaymentAmt", formatAmount(line5));

        BigDecimal line7 = line3.subtract(line5);
        addElement(doc, irs940, "TotalTaxableFUTAWagesAmt", formatAmount(line7));

        BigDecimal line8 = line7.multiply(new BigDecimal("0.006")).setScale(2, RoundingMode.HALF_UP);
        addElement(doc, irs940, "FUTATaxBeforeAdjustmentAmt", formatAmount(line8));

        // Adjustments (бывший Part 3)
        addElement(doc, irs940, "TaxAdjustmentAmt", "0");

        // Balance (бывший Part 4)
        BigDecimal scheduleACredit = fillForm940SA.getNYCreditReduction(companyId, year);
        BigDecimal line11 = line8.add(scheduleACredit);
        addElement(doc, irs940, "TotalFUTATaxAfterAdjustmentAmt", formatAmount(line11));

        BigDecimal line12 = paymentHistoryIrsRepository.getTotalPaidForFUTA(companyId, year);
        addElement(doc, irs940, "TotalFUTATaxDepositedAmt", formatAmount(line12));

        BigDecimal balanceDue = line11.subtract(line12);
        if (balanceDue.compareTo(BigDecimal.ZERO) > 0) {
            addElement(doc, irs940, "BalanceDueAmt", formatAmount(balanceDue));
        }

        BigDecimal overpayment = line12.subtract(line11);
        if (overpayment.compareTo(BigDecimal.ZERO) > 0) {
            addElement(doc, irs940, "OverpaymentAmt", formatAmount(overpayment));
        }

        // Credit Reduction для NY
        if ("NY".equals(((com.zikpak.facecheck.entity.Company)company).getCompanyState()) && year >= 2024) {
            Element creditReductionGrp = doc.createElement("CreditReductionStateGrp");
            irs940.appendChild(creditReductionGrp);
            addElement(doc, creditReductionGrp, "StateAbbreviationCd", "NY");
            addElement(doc, creditReductionGrp, "CreditReductionAmt", formatAmount(scheduleACredit));
        }

        // Quarterly breakdown (бывший Part 5)
        BigDecimal totalFutaLiability = line7.multiply(new BigDecimal("0.006")).setScale(2, RoundingMode.HALF_UP);
        if (totalFutaLiability.compareTo(new BigDecimal("500")) > 0) {
            Element quarterlyGrp = doc.createElement("QuarterlyFUTATaxLiabilityGrp");
            irs940.appendChild(quarterlyGrp);

            BigDecimal quarterlyAmount = totalFutaLiability.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
            addElement(doc, quarterlyGrp, "FirstQuarterLiabilityAmt", formatAmount(quarterlyAmount));
            addElement(doc, quarterlyGrp, "SecondQuarterLiabilityAmt", formatAmount(quarterlyAmount));
            addElement(doc, quarterlyGrp, "ThirdQuarterLiabilityAmt", formatAmount(quarterlyAmount));
            addElement(doc, quarterlyGrp, "FourthQuarterLiabilityAmt", formatAmount(quarterlyAmount));
            addElement(doc, quarterlyGrp, "TotalFUTATaxLiabilityAmt", formatAmount(totalFutaLiability));
        }

        // Signature (бывший Part 7)
        Element businessOfficerGrp = doc.createElement("BusinessOfficerGrp");
        irs940.appendChild(businessOfficerGrp);
        addElement(doc, businessOfficerGrp, "PersonNm", ((com.zikpak.facecheck.entity.User)admin).fullName());
        addElement(doc, businessOfficerGrp, "PersonTitleTxt", "Owner");
        addElement(doc, businessOfficerGrp, "PhoneNum", ((com.zikpak.facecheck.entity.User)admin).getPhoneNumber());
        addElement(doc, businessOfficerGrp, "SignatureDt", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

}