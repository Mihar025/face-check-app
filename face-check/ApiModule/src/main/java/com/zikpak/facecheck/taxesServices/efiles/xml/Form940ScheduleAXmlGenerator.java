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

        // Get user and company data
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

        // Root element - IRS MeF Package structure for Schedule A
        Element returnPackage = doc.createElement("ReturnPackage");
        returnPackage.setAttribute("xmlns", "http://www.irs.gov/efile");
        returnPackage.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        returnPackage.setAttribute("xsi:schemaLocation",
                "http://www.irs.gov/efile https://www.irs.gov/pub/irs-schema/" + year + "/IRS940ScheduleAv" + getSchemaVersion(year) + ".xsd");
        doc.appendChild(returnPackage);

        // Return Header (required by MeF)
        addReturnHeader(doc, returnPackage, company, year);

        // Return Data container
        Element returnData = doc.createElement("ReturnData");
        returnPackage.appendChild(returnData);

        // Form 940 Schedule A element
        Element scheduleA = doc.createElement("IRS940ScheduleA");
        scheduleA.setAttribute("documentId", generateDocumentId(companyId, year));
        scheduleA.setAttribute("taxYear", String.valueOf(year));
        returnData.appendChild(scheduleA);

        // Header section with company info
        addHeaderSection(doc, scheduleA, company, year);

        // Credit Reduction States section
        addCreditReductionSection(doc, scheduleA, companyId, year);

        // Total Credit Reduction
        addTotalCreditReduction(doc, scheduleA, companyId, year);

        // Signature section
        addSignatureSection(doc, scheduleA, admin);

        String xmlContent = documentToString(doc);

        // Upload to S3
        uploadXmlToS3(company, companyId, year, xmlContent);

        log.info("✅ Form 940 Schedule A XML e-file generated successfully for company {} year {}", companyId, year);
        return xmlContent;
    }

    /**
     * Add IRS MeF Return Header (required for production e-filing)
     */
    private void addReturnHeader(Document doc, Element returnPackage, Object company, int year) {
        Element returnHeader = doc.createElement("ReturnHeader");
        returnPackage.appendChild(returnHeader);

        // Submission ID (unique identifier for this submission)
        Element submissionId = doc.createElement("SubmissionId");
        submissionId.setTextContent(generateSubmissionId(((com.zikpak.facecheck.entity.Company) company).getId(), year));
        returnHeader.appendChild(submissionId);

        // Tax Year
        Element taxYear = doc.createElement("TaxYear");
        taxYear.setTextContent(String.valueOf(year));
        returnHeader.appendChild(taxYear);

        // Tax Period End Date (December 31)
        Element taxPeriodEndDate = doc.createElement("TaxPeriodEndDate");
        taxPeriodEndDate.setTextContent(year + "-12-31");
        returnHeader.appendChild(taxPeriodEndDate);

        // Software ID
        Element softwareId = doc.createElement("SoftwareId");
        softwareId.setTextContent("FACECHECK940SAV1.0");
        returnHeader.appendChild(softwareId);

        // Software Version
        Element softwareVersion = doc.createElement("SoftwareVersion");
        softwareVersion.setTextContent("1.0");
        returnHeader.appendChild(softwareVersion);

        // Transmitter (your software/company info)
        Element transmitter = doc.createElement("Transmitter");
        returnHeader.appendChild(transmitter);

        Element transmitterEIN = doc.createElement("EIN");
        transmitterEIN.setTextContent(((com.zikpak.facecheck.entity.Company) company).getEmployerEIN());
        transmitter.appendChild(transmitterEIN);

        Element transmitterName = doc.createElement("Name");
        transmitterName.setTextContent("FaceCheck Payroll Services");
        transmitter.appendChild(transmitterName);

        // Return Type
        Element returnType = doc.createElement("ReturnType");
        returnType.setTextContent("940SA"); // Schedule A return type
        returnHeader.appendChild(returnType);
    }

    private void addHeaderSection(Document doc, Element scheduleA, Object company, int year) {
        Element header = doc.createElement("Header");
        scheduleA.appendChild(header);

        // EIN (format: NN-NNNNNNN)
        Element ein = doc.createElement("EIN");
        String einValue = ((com.zikpak.facecheck.entity.Company) company).getEmployerEIN();
        if (!einValue.contains("-") && einValue.length() == 9) {
            einValue = einValue.substring(0, 2) + "-" + einValue.substring(2);
        }
        ein.setTextContent(einValue);
        header.appendChild(ein);

        // Business Name
        Element businessName = doc.createElement("BusinessName");
        businessName.setTextContent(((com.zikpak.facecheck.entity.Company) company).getCompanyName());
        header.appendChild(businessName);

        // Tax Year
        Element taxYear = doc.createElement("TaxYear");
        taxYear.setTextContent(String.valueOf(year));
        header.appendChild(taxYear);
    }

    private void addCreditReductionSection(Document doc, Element scheduleA, Integer companyId, int year) {
        Element creditReductionStates = doc.createElement("CreditReductionStates");
        scheduleA.appendChild(creditReductionStates);

        // New York entry
        Element nyEntry = doc.createElement("StateEntry");
        creditReductionStates.appendChild(nyEntry);

        // State code
        Element stateCode = doc.createElement("StateCode");
        stateCode.setTextContent("NY");
        nyEntry.appendChild(stateCode);

        // State name
        Element stateName = doc.createElement("StateName");
        stateName.setTextContent("New York");
        nyEntry.appendChild(stateName);

        // FUTA taxable wages for this state
        BigDecimal futaTaxableWages = calculateFUTATaxableWages(companyId, year);
        Element taxableWages = doc.createElement("FUTATaxableWages");
        taxableWages.setTextContent(formatAmount(futaTaxableWages));
        nyEntry.appendChild(taxableWages);

        // Credit reduction rate
        Element reductionRate = doc.createElement("CreditReductionRate");
        reductionRate.setTextContent("0.009"); // 0.9% for NY 2024
        nyEntry.appendChild(reductionRate);

        // Credit reduction amount
        BigDecimal creditReduction = futaTaxableWages.multiply(NY_2024_REDUCTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        Element creditAmount = doc.createElement("CreditReductionAmount");
        creditAmount.setTextContent(formatAmount(creditReduction));
        nyEntry.appendChild(creditAmount);
    }

    private void addTotalCreditReduction(Document doc, Element scheduleA, Integer companyId, int year) {
        Element totalSection = doc.createElement("TotalCreditReduction");
        scheduleA.appendChild(totalSection);

        // Calculate total credit reduction (only NY for now)
        BigDecimal futaTaxableWages = calculateFUTATaxableWages(companyId, year);
        BigDecimal totalCreditReduction = futaTaxableWages.multiply(NY_2024_REDUCTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        Element totalAmount = doc.createElement("TotalAmount");
        totalAmount.setTextContent(formatAmount(totalCreditReduction));
        totalSection.appendChild(totalAmount);

        // Instructions reference
        Element instructions = doc.createElement("Instructions");
        instructions.setTextContent("Enter this amount on Form 940, Line 11");
        totalSection.appendChild(instructions);
    }

    private void addSignatureSection(Document doc, Element scheduleA, Object admin) {
        Element signature = doc.createElement("Signature");
        scheduleA.appendChild(signature);

        Element signedBy = doc.createElement("SignedBy");
        signedBy.setTextContent(((com.zikpak.facecheck.entity.User) admin).fullName());
        signature.appendChild(signedBy);

        Element title = doc.createElement("Title");
        title.setTextContent("Owner");
        signature.appendChild(title);

        Element phone = doc.createElement("Phone");
        phone.setTextContent(((com.zikpak.facecheck.entity.User) admin).getPhoneNumber());
        signature.appendChild(phone);

        Element date = doc.createElement("Date");
        date.setTextContent(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        signature.appendChild(date);

        Element signatureMethod = doc.createElement("SignatureMethod");
        signatureMethod.setTextContent("Electronic");
        signature.appendChild(signatureMethod);
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

    private String generateSubmissionId(Integer companyId, int year) {
        return String.format("FACE940SA_%04d_%05d_%08d", year, companyId, System.currentTimeMillis() % 100000000);
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
}