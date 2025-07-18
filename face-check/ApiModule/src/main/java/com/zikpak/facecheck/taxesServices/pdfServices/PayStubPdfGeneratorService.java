package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayStubPdfGeneratorService {
    private final WorkerPayRollService workerPayRollService;
    private final MetricsForPdfServices metric;
    public byte[] generatePayStubPdf(PayStubDTO stub) {
        final String FORM = "Paystubs";
        metric.recordRequest(FORM);

        Timer.Sample timer = metric.startTimer();
        long ms1 = System.currentTimeMillis();
        var yearToDateData = workerPayRollService.findAllYearToDateForWorker(stub.getWorkerId(), stub.getCompanyId(), stub.getYear());
        long end1 = System.currentTimeMillis() - ms1;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.setMargins(25, 25, 35, 25);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // 0. "Powered by Facecheck" сверху
            document.add(new Paragraph("Powered by Facecheck")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10));

            // 1. Header: логотип + две колонки (сотрудник / компания)
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);

            document.add(new Paragraph(String.format("Pay Period: %s to %s",
                    stub.getPeriodStart().toString(),
                    stub.getPeriodEnd().toString()))
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setMarginBottom(12)
            );

         //   String logoPath = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/logo.jpg";
      //     ImageData logoData = ImageDataFactory.create(logoPath);
            InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
            ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());

            Cell logoCell = new Cell()
                    .add(new com.itextpdf.layout.element.Image(logoData).scaleToFit(60, 60))
                    .setBorder(null);
            headerTable.addCell(logoCell);

            Cell employeeCell = new Cell().setBorder(null);
            employeeCell.add(new Paragraph(stub.getEmployeeName()).setFont(boldFont).setFontSize(11));
            employeeCell.add(new Paragraph("SSN: " + stub.getEmployeeSsn()).setFont(regularFont).setFontSize(9));
            employeeCell.add(new Paragraph("Address: "
                    + stub.getEmployeeAddress() + ", "
                    + stub.getEmployeeCity() + ", "
                    + stub.getEmployeeState() + " "
                    + stub.getEmployeeZipCode())
                    .setFont(regularFont).setFontSize(9));
            employeeCell.add(new Paragraph("Phone: " + stub.getEmployeePhoneNumber())
                    .setFont(regularFont).setFontSize(9));
            headerTable.addCell(employeeCell);

            Cell companyCell = new Cell().setBorder(null);
            companyCell.add(new Paragraph(stub.getCompanyName()).setFont(boldFont).setFontSize(11));
            companyCell.add(new Paragraph("Address: "
                    + stub.getEmployerAddress() + ", "
                    + stub.getCompanyCity() + ", "
                    + stub.getCompanyState() + " "
                    + stub.getCompanyZipCode())
                    .setFont(regularFont).setFontSize(9));
            companyCell.add(new Paragraph("Phone: " + stub.getCompanyPhoneNumber())
                    .setFont(regularFont).setFontSize(9));
            headerTable.addCell(companyCell);

            document.add(headerTable);

            // 2. Таблица рабочих дней
            Map<LocalDate, BigDecimal> hoursWorkedMap = stub.getHoursWorkedPerDate();
            Map<LocalDate, BigDecimal> grossPayMap = stub.getGrossPayPerDate();
            Map<LocalDate, DayOfWeek> dateToDayOfWeek = stub.getDateToDayOfWeek();


            if (hoursWorkedMap != null && grossPayMap != null) {
                Table daysTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1}))
                        .useAllAvailableWidth()
                        .setMarginBottom(12);

                daysTable.addHeaderCell(createHeaderCell("Day", 9));
                daysTable.addHeaderCell(createHeaderCell("Date", 9));
                daysTable.addHeaderCell(createHeaderCell("Hours", 9));
                daysTable.addHeaderCell(createHeaderCell("Rate", 9));
                daysTable.addHeaderCell(createHeaderCell("Gross", 9));

                // Проходим по отсортированным датам (TreeMap гарантирует порядок)
                for (Map.Entry<LocalDate, BigDecimal> entry : hoursWorkedMap.entrySet()) {
                    LocalDate date = entry.getKey();
                    BigDecimal hoursWorked = entry.getValue();

                    if (hoursWorked.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal grossPay = grossPayMap.getOrDefault(date, BigDecimal.ZERO);
                        DayOfWeek dayOfWeek = dateToDayOfWeek.get(date);

                        daysTable.addCell(createValueCell(
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                regularFont, 9));
                        daysTable.addCell(createValueCell(date.toString(), regularFont, 9));
                        daysTable.addCell(createValueCell(hoursWorked.toString(), regularFont, 9));
                        daysTable.addCell(createValueCell("$" + stub.getBaseHourlyRate(), regularFont, 9));
                        daysTable.addCell(createValueCell("$" + grossPay.toString(), regularFont, 9));
                    }
                }
                document.add(daysTable);
            }



            // 3. Секция Insurance (если включено)
            if (Boolean.TRUE.equals(stub.getUserActivatedInsurance())) {
                Table insTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                        .useAllAvailableWidth()
                        .setMarginBottom(12);

                Cell insHeader = new Cell(1, 2)
                        .add(new Paragraph("INSURANCE DEDUCTIONS").setFont(boldFont).setFontSize(11))
                        .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(240, 240, 240))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(4);
                insTable.addCell(insHeader);

                insTable.addCell(createCompactCell("Insurance Deduction per Period:", boldFont, 9));
                insTable.addCell(createCompactCell("$" + stub.getHealthInsuranceChargePeriod(), regularFont, 9));



                document.add(insTable);
            }

            // 4. Итоговый блок TOTAL GROSS / TOTAL NET
            Table highlightedTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            Cell grossPayLabelCell = createColorCell("TOTAL GROSS PAY:", boldFont, 12,
                    new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.LEFT);
            Cell grossPayValueCell = createColorCell("$" + stub.getTotalGrossPay(), boldFont, 12,
                    new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.RIGHT);

            Cell netPayLabelCell = createColorCell("TOTAL NET PAY:", boldFont, 12,
                    new com.itextpdf.kernel.color.DeviceRgb(200, 240, 200), TextAlignment.LEFT);
            Cell netPayValueCell = createColorCell("$" + stub.getNetPay(), boldFont, 12,
                    new com.itextpdf.kernel.color.DeviceRgb(200, 240, 200), TextAlignment.RIGHT);

            highlightedTable.addCell(grossPayLabelCell);
            highlightedTable.addCell(grossPayValueCell);
            highlightedTable.addCell(netPayLabelCell);
            highlightedTable.addCell(netPayValueCell);

            document.add(highlightedTable);

            // 5. КРАСИВО РАЗДЕЛЕННАЯ СЕКЦИЯ НАЛОГОВ
            // Главная таблица с заголовком
            Table mainDeductionsTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            // Заголовок для всей секции
            Cell mainHeader = new Cell()
                    .add(new Paragraph("DEDUCTIONS & YEAR-TO-DATE SUMMARY").setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                    .setFontColor(Color.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            mainDeductionsTable.addCell(mainHeader);
            document.add(mainDeductionsTable);

            // Таблица с двумя колонками для налогов
            Table taxesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            // ЛЕВАЯ КОЛОНКА - CURRENT PERIOD DEDUCTIONS
            Cell currentPeriodHeader = new Cell()
                    .add(new Paragraph("CURRENT PERIOD DEDUCTIONS").setFont(boldFont).setFontSize(10))
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(6);

            // ПРАВАЯ КОЛОНКА - YEAR TO DATE TOTALS
            Cell ytdHeader = new Cell()
                    .add(new Paragraph("YEAR-TO-DATE TOTALS").setFont(boldFont).setFontSize(10))
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(245, 255, 250))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(6);

            taxesTable.addCell(currentPeriodHeader);
            taxesTable.addCell(ytdHeader);

            // Создаем отдельные таблицы для каждой колонки
            Table currentPeriodTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .useAllAvailableWidth();

            Table ytdTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .useAllAvailableWidth();

            // ЗАПОЛНЯЕМ ЛЕВУЮ КОЛОНКУ (Current Period)
            currentPeriodTable.addCell(createStyledTaxCell("Total Hours:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell(String.format("%.2f", stub.getTotalHours()), regularFont, 9, false));

            currentPeriodTable.addCell(createStyledTaxCell("Social Security Tax:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell("$" + formatAmount(stub.getSocialSecurityTax()), regularFont, 9, false));

            currentPeriodTable.addCell(createStyledTaxCell("Medicare Tax:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell("$" + formatAmount(stub.getMedicareTax()), regularFont, 9, false));

            currentPeriodTable.addCell(createStyledTaxCell("Federal Tax:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell("$" + formatAmount(stub.getFederalTax()), regularFont, 9, false));

            currentPeriodTable.addCell(createStyledTaxCell("State Tax:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell("$" + formatAmount(stub.getStateTax()), regularFont, 9, false));

            currentPeriodTable.addCell(createStyledTaxCell("Local Tax:", boldFont, 9, false));
            currentPeriodTable.addCell(createStyledTaxCell("$" + formatAmount(stub.getLocalTax()), regularFont, 9, false));

            // ЗАПОЛНЯЕМ ПРАВУЮ КОЛОНКУ (Year To Date)
            ytdTable.addCell(createStyledTaxCell("YTD Social Security:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateSocialSecurityEmployee()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD Medicare:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateMedicare()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD Federal Tax:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateFederalWithholding()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD State Tax:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateNyStateWithholding()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD Local Tax:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateNyLocalWithholding()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD Disability:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateNyDisabilityWithholding()), regularFont, 9, true));

            ytdTable.addCell(createStyledTaxCell("YTD Family Leave:", boldFont, 9, true));
            ytdTable.addCell(createStyledTaxCell("$" + formatAmount(yearToDateData.getYearToDateNyPaidFamilyLeave()), regularFont, 9, true));

            // Добавляем таблицы в основную структуру
            Cell currentPeriodCell = new Cell().add(currentPeriodTable).setBorder(null).setPadding(5);
            Cell ytdCell = new Cell().add(ytdTable).setBorder(null).setPadding(5);

            taxesTable.addCell(currentPeriodCell);
            taxesTable.addCell(ytdCell);

            document.add(taxesTable);

            // 6. ИТОГОВАЯ СЕКЦИЯ YTD GROSS & NET
            Table ytdSummaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            Cell ytdGrossLabelCell = createColorCell("YEAR-TO-DATE GROSS PAY:", boldFont, 11,
                    new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT);
            Cell ytdGrossValueCell = createColorCell("$" + formatAmount(yearToDateData.getYearToDateGrossPay()), boldFont, 11,
                    new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT);

            Cell ytdNetLabelCell = createColorCell("YEAR-TO-DATE NET PAY:", boldFont, 11,
                    new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.LEFT);
            Cell ytdNetValueCell = createColorCell("$" + formatAmount(yearToDateData.getYearToDateNetPay()), boldFont, 11,
                    new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.RIGHT);

            ytdSummaryTable.addCell(ytdGrossLabelCell);
            ytdSummaryTable.addCell(ytdGrossValueCell);
            ytdSummaryTable.addCell(ytdNetLabelCell);
            ytdSummaryTable.addCell(ytdNetValueCell);

            document.add(ytdSummaryTable);

            // 7. Секция Sick Leave
            Table sickTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(12);

            Cell sickHeader = new Cell(1, 2)
                    .add(new Paragraph("SICK LEAVE SUMMARY").setFont(boldFont).setFontSize(11))
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(240, 240, 240))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4);
            sickTable.addCell(sickHeader);

            sickTable.addCell(createCompactCell("Sick Leave Accrued (hrs):", boldFont, 9));
            sickTable.addCell(createCompactCell(stub.getSickLeaveAccrued().toString(), regularFont, 9));

            sickTable.addCell(createCompactCell("Sick Leave Used (hrs):", boldFont, 9));
            sickTable.addCell(createCompactCell(stub.getSickLeaveUsed().toString(), regularFont, 9));

            sickTable.addCell(createCompactCell("Sick Leave Remaining (hrs):", boldFont, 9));
            sickTable.addCell(createCompactCell(stub.getSickLeaveRemaining().toString(), regularFont, 9));

            document.add(sickTable);

            // 8. Футер
            document.add(new Paragraph("This document is an official payroll record.")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Generated by Face-Check Corporation " + LocalDate.now().toString())
                    .setFont(regularFont)
                    .setFontSize(7)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();



            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end1);
            metric.recordOperationTime(timer,"paystubs_success");

            return baos.toByteArray();

        } catch (Exception e) {
            metric.recordOperationTime(timer,"pasystubs_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("paystubs_failed", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Вспомогательные методы для создания ячеек
    private Cell createHeaderCell(String text, float fontSize) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFontSize(fontSize)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER))
                .setPadding(4)
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(230, 230, 230));
    }

    private Cell createColorCell(String text, PdfFont font, float fontSize,
                                 com.itextpdf.kernel.color.Color backgroundColor,
                                 TextAlignment alignment) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setBackgroundColor(backgroundColor)
                .setPadding(5);
    }

    private Cell createCompactCell(String text, PdfFont font, float fontSize) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(3);
    }

    private Cell createValueCell(String text, PdfFont font, float fontSize) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(4);
    }

    // НОВЫЕ методы для стилизованных налоговых ячеек
    private Cell createStyledTaxCell(String text, PdfFont font, float fontSize, boolean isYTD) {
        if (text == null) text = "";

        com.itextpdf.kernel.color.Color backgroundColor = isYTD ?
                new com.itextpdf.kernel.color.DeviceRgb(248, 255, 248) :  // Светло-зеленый для YTD
                new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255);   // Светло-голубой для current

        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(backgroundColor)
                .setPadding(4);
              //  .setBorder(new com.itextpdf.kernel.color.DeviceRgb(220, 220, 220));
    }

    // Метод для форматирования сумм
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }
}