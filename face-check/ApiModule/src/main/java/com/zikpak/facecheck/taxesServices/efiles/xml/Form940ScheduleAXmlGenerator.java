package com.zikpak.facecheck.taxesServices.efiles.xml;

import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class Form940ScheduleAXmlGenerator {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final AmazonS3Service amazonS3Service;

    // NY 2024 Credit Reduction Rate
    private static final BigDecimal NY_2024_REDUCTION_RATE = new BigDecimal("0.009"); // 0.9%

    /**
     * Generate official IRS Form 940 Schedule A XML e-file
     */
    public String generateForm940ScheduleAXml(Integer userId, Integer companyId, int year) throws Exception {
        log.info("🏛️ Generating official IRS Form 940 Schedule A XML e-file for company {} year {}", companyId, year);

        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        // Only generate for NY companies
        if (!"NY".equals(company.getCompanyState())) {
            log.warn("⚠️ Schedule A not required for state: {}", company.getCompanyState());
            return "";
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Правильный корневой элемент - Return, не ReturnPackage
        Element returnElement = doc.createElementNS("http://www.irs.gov/efile", "Return");
        doc.appendChild(returnElement);
        returnElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        returnElement.setAttribute("xsi:schemaLocation",
                "http://www.irs.gov/efile https://www.irs.gov/pub/irs-schema/" + year + "/IRS940ScheduleAv" + getSchemaVersion(year) + ".xsd");

        // Return Header с правильной структурой
        addReturnHeaderCorrected(doc, returnElement, company, year);

        // Return Data
        Element returnData = doc.createElement("ReturnData");
        returnData.setAttribute("documentCnt", "1");
        returnElement.appendChild(returnData);

        // IRS940ScheduleA element
        Element scheduleA = doc.createElement("IRS940ScheduleA");
        scheduleA.setAttribute("documentId", generateDocumentId(companyId, year));
        returnData.appendChild(scheduleA);

        // Добавляем все поля плоской структурой
        addScheduleAFieldsFlat(doc, scheduleA, company, companyId, year, admin);

        String xmlContent = documentToString(doc);
        uploadXmlToS3(company, companyId, year, xmlContent);

        log.info("✅ Form 940 Schedule A XML e-file generated successfully");
        return xmlContent;
    }


    /**
     * Calculate FUTA taxable wages (same as Form 940 line 7)
     */
    private BigDecimal calculateFUTATaxableWages(Integer companyId, int year) {
        // Total gross wages
        BigDecimal totalWages = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);

        // Excess wages over $7,000 per employee
        BigDecimal excessWages = calculateExcessWages(companyId, year);

        // FUTA taxable wages = total wages - excess wages
        return totalWages.subtract(excessWages);
    }

    /**
     * Calculate excess wages over $7,000 per employee
     */
    private BigDecimal calculateExcessWages(Integer companyId, int year) {
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

    private String generateDocumentId(Integer companyId, int year) {
        return String.format("940SA_%d_%d_%d", companyId, year, System.currentTimeMillis());
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
        String companyKeyPart = ((com.zikpak.facecheck.entity.Company) company).getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f940sa_%d_%d.xml", companyId, year);
        String key = String.format("%s/%d/940saform/940saXml/%d/%s",
                companyKeyPart, companyId, year, fileName);

        amazonS3Service.uploadPdfToS3(xmlContent.getBytes(), key);
        log.info("✅ Form 940 Schedule A XML uploaded to S3: {}", key);
    }

    private void addReturnHeaderCorrected(Document doc, Element returnElement, Object company, int year) {
        Element returnHeader = doc.createElement("ReturnHeader");
        returnHeader.setAttribute("binaryAttachmentCnt", "0");
        returnElement.appendChild(returnHeader);

        // Правильные элементы для IRS MeF
        addElement(doc, returnHeader, "TaxYr", String.valueOf(year));
        addElement(doc, returnHeader, "TaxPeriodBeginDt", year + "-01-01");
        addElement(doc, returnHeader, "TaxPeriodEndDt", year + "-12-31");

        // Software ID (должен быть зарегистрирован в IRS)
        addElement(doc, returnHeader, "SoftwareId", "00000000"); // Placeholder
        addElement(doc, returnHeader, "SoftwareVersionNum", "2024.1.0");

        // Originator Group
        Element originatorGrp = doc.createElement("OriginatorGrp");
        returnHeader.appendChild(originatorGrp);
        addElement(doc, originatorGrp, "EFIN", "000000"); // Нужен реальный EFIN
        addElement(doc, originatorGrp, "OriginatorTypeCd", "ERO");

        addElement(doc, returnHeader, "ReturnTypeCd", "940SA");

        // Filer information
        Element filer = doc.createElement("Filer");
        returnHeader.appendChild(filer);

        String ein = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN().replaceAll("-", "");
        addElement(doc, filer, "EIN", ein);

        Element businessName = doc.createElement("BusinessName");
        filer.appendChild(businessName);
        addElement(doc, businessName, "BusinessNameLine1Txt",
                ((com.zikpak.facecheck.entity.Company)company).getCompanyName());

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

    private void addScheduleAFieldsFlat(Document doc, Element scheduleA, Object company, Integer companyId, int year, Object admin) {
        // Business info
        Element businessName = doc.createElement("BusinessName");
        businessName.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyName());
        scheduleA.appendChild(businessName);

        String ein = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN().replaceAll("-", "");
        addElement(doc, scheduleA, "EIN", ein);

        // Расчет FUTA taxable wages
        BigDecimal totalWages = employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year);
        BigDecimal excessWages = calculateExcessWages(companyId, year);
        BigDecimal futaTaxableWages = totalWages.subtract(excessWages);

        // Credit Reduction State Group - правильные IRS названия
        Element creditReductionStateGrp = doc.createElement("CreditReductionStateGrp");
        scheduleA.appendChild(creditReductionStateGrp);

        // State
        addElement(doc, creditReductionStateGrp, "StateAbbreviationCd", "NY");

        // FUTA Taxable Wages
        addElement(doc, creditReductionStateGrp, "FUTATaxableWagesAmt", formatAmount(futaTaxableWages));

        // Credit Reduction Rate
        addElement(doc, creditReductionStateGrp, "CreditReductionRt", "0.009");

        // Credit Reduction Amount
        BigDecimal creditReduction = futaTaxableWages.multiply(NY_2024_REDUCTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        addElement(doc, creditReductionStateGrp, "CreditReductionAmt", formatAmount(creditReduction));

        // Total Credit Reduction
        addElement(doc, scheduleA, "TotalCreditReductionAmt", formatAmount(creditReduction));

        // Signature
        Element businessOfficerGrp = doc.createElement("BusinessOfficerGrp");
        scheduleA.appendChild(businessOfficerGrp);
        addElement(doc, businessOfficerGrp, "PersonNm", ((com.zikpak.facecheck.entity.User)admin).fullName());
        addElement(doc, businessOfficerGrp, "PersonTitleTxt", "Owner");

        String phone = ((com.zikpak.facecheck.entity.User)admin).getPhoneNumber();
        if (phone != null && !phone.isEmpty()) {
            phone = phone.replaceAll("[^0-9]", "");
            if (phone.length() == 10) {
                addElement(doc, businessOfficerGrp, "PhoneNum", phone);
            }
        }

        addElement(doc, businessOfficerGrp, "SignatureDt", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }

}