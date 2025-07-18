package com.zikpak.facecheck.taxesServices.efiles.xml;

import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Form941ScheduleBXmlGenerator {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private final AmazonS3Service amazonS3Service;

    public String generateForm941ScheduleBXml(Integer userId, Integer companyId, int year, int quarter) throws Exception {

        // Get user and company data (same as PDF service)
        var admin = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        // Get payment data for quarter (same as PDF service)
        List<PaymentHistoryIrs> payments = paymentHistoryIrsRepository
                .findAllByCompany_IdAndYearAndQuarter(companyId, year, quarter, Pageable.unpaged())
                .getContent();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Root element
        Element returnPackage = doc.createElement("Return");
        returnPackage.setAttribute("xmlns", "http://www.irs.gov/efile");
        returnPackage.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        returnPackage.setAttribute("xsi:schemaLocation",
                "http://www.irs.gov/efile https://www.irs.gov/pub/irs-schema/" + year + "/IRS941ScheduleBv1.0.xsd"); // исправить версию
        doc.appendChild(returnPackage);

        // Return Header
        addReturnHeader(doc, returnPackage, company, year, quarter);

        // Return Data
        Element returnData = doc.createElement("ReturnData");
        returnPackage.appendChild(returnData);

        // Schedule B
        Element scheduleB = doc.createElement("IRS941ScheduleB");
        returnData.appendChild(scheduleB);

        // Данные
        addHeaderSection(doc, scheduleB, company, year, quarter);
        addSemiweeklyDeposits(doc, scheduleB, payments, year, quarter);
        addTotalQuarterLiability(doc, scheduleB, payments); // добавить это

        String xmlContent = documentToString(doc);
        uploadXmlToS3(company, companyId, year, quarter, xmlContent);

        return xmlContent;
    }

    /**
     * Add IRS MeF Return Header (required for production e-filing)
     */
    private void addReturnHeader(Document doc, Element returnPackage, Object company, int year, int quarter) {
        Element returnHeader = doc.createElement("ReturnHeader");

        Element submissionId = doc.createElement("SubmissionId");
        submissionId.setTextContent("TEMP_SB_" + System.currentTimeMillis());
        returnHeader.appendChild(submissionId);

        // Tax Year
        Element taxYear = doc.createElement("TaxYear");
        taxYear.setTextContent(String.valueOf(year));
        returnHeader.appendChild(taxYear);

        // Quarter Ending Date
        Element quarterEndingDt = doc.createElement("QuarterEndingDt");
        quarterEndingDt.setTextContent(getQuarterEndDate(year, quarter));
        returnHeader.appendChild(quarterEndingDt);

        // Return Type
        Element returnType = doc.createElement("ReturnType");
        returnType.setTextContent("941SB");
        returnHeader.appendChild(returnType);
        returnPackage.appendChild(returnHeader);
    }

    private void addHeaderSection(Document doc, Element scheduleB, Object company, int year, int quarter) {


        Element ein = doc.createElement("EmployerEIN");
        String einValue = ((com.zikpak.facecheck.entity.Company)company).getEmployerEIN();
        einValue = einValue.replace("-", ""); // Убираем дефис
        ein.setTextContent(einValue);
        scheduleB.appendChild(ein);

        // Company name
        Element companyName = doc.createElement("BusinessNameLine1Txt");
        companyName.setTextContent(((com.zikpak.facecheck.entity.Company)company).getCompanyName());
        scheduleB.appendChild(companyName);

    }

    private void addSemiweeklyDeposits(Document doc, Element scheduleB, List<PaymentHistoryIrs> payments, int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;

        for (int monthNum = 1; monthNum <= 3; monthNum++) {
            int actualMonth = startMonth + monthNum - 1;

            // Фильтруем платежи для текущего месяца
            List<PaymentHistoryIrs> monthPayments = payments.stream()
                    .filter(p -> p.getPaymentDate().getMonthValue() == actualMonth)
                    .collect(Collectors.toList());

            // Если нет платежей в этом месяце, все равно создаем месячную запись
            Element monthlyDetail = doc.createElement("TaxLiabilityQtrMonthlyDetail");

            Element monthOfQuarter = doc.createElement("MonthOfQuarterCd");
            monthOfQuarter.setTextContent(String.valueOf(monthNum));
            monthlyDetail.appendChild(monthOfQuarter);

            // Группируем платежи по дням
            Map<Integer, BigDecimal> dailyTotals = monthPayments.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getPaymentDate().getDayOfMonth(),
                            Collectors.reducing(BigDecimal.ZERO, PaymentHistoryIrs::getAmount, BigDecimal::add)
                    ));

            // Добавляем дневные детали
            dailyTotals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        Element dailyDetail = doc.createElement("DailyTaxLiabilityDetail");

                        Element dayNum = doc.createElement("DayNum");
                        dayNum.setTextContent(String.valueOf(entry.getKey()));
                        dailyDetail.appendChild(dayNum);

                        Element taxLiability = doc.createElement("TaxLiabilityAmt");
                        taxLiability.setTextContent(formatAmount(entry.getValue()));
                        dailyDetail.appendChild(taxLiability);

                        monthlyDetail.appendChild(dailyDetail);
                    });

            // Месячный итог
            BigDecimal monthTotal = monthPayments.stream()
                    .map(PaymentHistoryIrs::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Element totalTaxLiability = doc.createElement("TotalTaxLiabilityAmt");
            totalTaxLiability.setTextContent(formatAmount(monthTotal));
            monthlyDetail.appendChild(totalTaxLiability);

            scheduleB.appendChild(monthlyDetail);
        }
    }

    /**
     * Get quarter end date in YYYY-MM-DD format
     */
    private String getQuarterEndDate(int year, int quarter) {
        LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
        return quarterEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);
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

        String xmlContent = writer.getBuffer().toString();

        // 🔧 ДОБАВИТЬ КОММЕНТАРИЙ:
        String comment = "<!-- \n" +
                "  ВАЖНО: Этот Schedule B XML файл сгенерирован для справки.\n" +
                "  Перед подачей в IRS необходимо:\n" +
                "  1. Добавить Transmitter информацию (EIN и Name вашего налогового агента)\n" +
                "  2. Заменить SubmissionId на уникальный ID от вашего софта\n" +
                "  3. Проверить все данные\n" +
                "  4. Валидировать против IRS XSD схемы\n" +
                "-->\n";

        return xmlContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + comment);
    }

    private void uploadXmlToS3(Object company, Integer companyId, int year, int quarter, String xmlContent) {
        String companyKeyPart = ((com.zikpak.facecheck.entity.Company)company).getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("f941sb_%d_%d_%d.xml", companyId, year, quarter);
        String key = String.format("%s/%d/941sbform/941Xml/%d/%d/%s",
                companyKeyPart, companyId, year, quarter, fileName);

        amazonS3Service.uploadPdfToS3(xmlContent.getBytes(), key);
    }

    private void addTotalQuarterLiability(Document doc, Element scheduleB, List<PaymentHistoryIrs> payments) {
        BigDecimal total = payments.stream()
                .map(PaymentHistoryIrs::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Element totalQuarter = doc.createElement("TotalQuarterTaxLiabilityAmt");
        totalQuarter.setTextContent(formatAmount(total));
        scheduleB.appendChild(totalQuarter);
    }


}