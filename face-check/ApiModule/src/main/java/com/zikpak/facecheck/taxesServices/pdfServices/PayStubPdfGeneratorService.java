package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationRequest;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
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
    private final NotificationService notificationService;

    public byte[] generatePayStubPdf(PayStubDTO stub) {
        final String FORM = "Paystubs";
        metric.recordRequest(FORM);

        Timer.Sample timer = metric.startTimer();
        long ms1 = System.currentTimeMillis();
        var yearToDateData = workerPayRollService.findAllYearToDateForWorker(
                stub.getWorkerId(), stub.getCompanyId(), stub.getYear());
        long end1 = System.currentTimeMillis() - ms1;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Компактные margins для экономии бумаги
            document.setMargins(20, 20, 20, 20);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // Header с логотипом и "Powered by"
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                    .useAllAvailableWidth()
                    .setMarginBottom(8);

            // Логотип
            InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
            ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());
            Cell logoCell = new Cell()
                    .add(new com.itextpdf.layout.element.Image(logoData).scaleToFit(40, 40))
                    .setBorder(Border.NO_BORDER);
            headerTable.addCell(logoCell);

            // Powered by Facecheck
            Cell poweredByCell = new Cell()
                    .add(new Paragraph("Powered by Facecheck")
                            .setFont(regularFont)
                            .setFontSize(7)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER);
            headerTable.addCell(poweredByCell);

            document.add(headerTable);

            // EARNINGS STATEMENT заголовок
            document.add(new Paragraph("EARNINGS STATEMENT")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8));

            // Информация о компании и сотруднике в две колонки
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(8);

            // Левая колонка - Employer
            Cell employerCell = new Cell().setBorder(Border.NO_BORDER);
            employerCell.add(new Paragraph("EMPLOYER").setFont(boldFont).setFontSize(8).setMarginBottom(2));
            employerCell.add(new Paragraph(stub.getCompanyName()).setFont(regularFont).setFontSize(8));
            employerCell.add(new Paragraph(stub.getEmployerAddress()).setFont(regularFont).setFontSize(8));
            employerCell.add(new Paragraph(stub.getCompanyCity() + ", " + stub.getCompanyState() + " " + stub.getCompanyZipCode())
                    .setFont(regularFont).setFontSize(8));
            employerCell.add(new Paragraph("Tel: " + stub.getCompanyPhoneNumber()).setFont(regularFont).setFontSize(8));
            infoTable.addCell(employerCell);

            // Правая колонка - Employee
            Cell employeeCell = new Cell().setBorder(Border.NO_BORDER);
            employeeCell.add(new Paragraph("EMPLOYEE").setFont(boldFont).setFontSize(8).setMarginBottom(2));
            employeeCell.add(new Paragraph(stub.getEmployeeName()).setFont(regularFont).setFontSize(8));
            employeeCell.add(new Paragraph("SSN: XXX-XX-" + stub.getEmployeeSsn().substring(stub.getEmployeeSsn().length() - 4))
                    .setFont(regularFont).setFontSize(8));
            employeeCell.add(new Paragraph(stub.getEmployeeAddress()).setFont(regularFont).setFontSize(8));
            employeeCell.add(new Paragraph(stub.getEmployeeCity() + ", " + stub.getEmployeeState() + " " + stub.getEmployeeZipCode())
                    .setFont(regularFont).setFontSize(8));
            infoTable.addCell(employeeCell);

            document.add(infoTable);

            // Pay Period информация
            document.add(new Paragraph("Pay Period: " + stub.getPeriodStart() + " to " + stub.getPeriodEnd())
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginBottom(6));

            addSeparatorLine(document);

            document.add(new Paragraph("EARNINGS")
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginTop(6)
                    .setMarginBottom(4));

            // Таблица рабочих дней - очень компактная
            Map<LocalDate, BigDecimal> hoursWorkedMap = stub.getHoursWorkedPerDate();
            Map<LocalDate, BigDecimal> grossPayMap = stub.getGrossPayPerDate();
            Map<LocalDate, DayOfWeek> dateToDayOfWeek = stub.getDateToDayOfWeek();

            if (hoursWorkedMap != null && grossPayMap != null) {
                Table earningsTable = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1, 1, 1}))
                        .useAllAvailableWidth()
                        .setMarginBottom(6);

                // Заголовки таблицы
                earningsTable.addCell(createSimpleCell("Date", boldFont, 7, true));
                earningsTable.addCell(createSimpleCell("Day", boldFont, 7, true));
                earningsTable.addCell(createSimpleCell("Hours", boldFont, 7, true));
                earningsTable.addCell(createSimpleCell("Rate", boldFont, 7, true));
             //  earningsTable.addCell(createSimpleCell("Amount", boldFont, 7, true));

                BigDecimal totalHours = BigDecimal.ZERO;
                BigDecimal totalGross = BigDecimal.ZERO;

                   for (Map.Entry<LocalDate, BigDecimal> entry : hoursWorkedMap.entrySet()) {
                    LocalDate date = entry.getKey();
                    BigDecimal hoursWorked = entry.getValue();

                    if (hoursWorked.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal grossPay = grossPayMap.getOrDefault(date, BigDecimal.ZERO);
                        DayOfWeek dayOfWeek = dateToDayOfWeek.get(date);

                        earningsTable.addCell(createSimpleCell(date.toString(), regularFont, 7, false));
                        earningsTable.addCell(createSimpleCell(
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                regularFont, 7, false));
                        earningsTable.addCell(createSimpleCell(hoursWorked.toString(), regularFont, 7, false));
                        earningsTable.addCell(createSimpleCell("$" + stub.getBaseHourlyRate(), regularFont, 7, false));
                     //   earningsTable.addCell(createSimpleCell("$" + grossPay.toString(), regularFont, 7, false));

                        totalHours = totalHours.add(hoursWorked);

                        totalGross = totalGross.add(grossPay);
                    }
                }


                earningsTable.addCell(createSimpleCell("TOTAL", boldFont, 7, true));
                earningsTable.addCell(createSimpleCell("", regularFont, 7, false));
                earningsTable.addCell(createSimpleCell(totalHours.toString(), boldFont, 7, true));
                earningsTable.addCell(createSimpleCell("", regularFont, 7, false));
                earningsTable.addCell(createSimpleCell("$" + stub.getTotalGrossPay(), boldFont, 7, true));

                document.add(earningsTable);
            }

            // Линия-разделитель
            addSeparatorLine(document);

            // DEDUCTIONS секция
            document.add(new Paragraph("DEDUCTIONS")
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginTop(6)
                    .setMarginBottom(4));

            Table deductionsTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(6);

            // Заголовки
            deductionsTable.addCell(createSimpleCell("Description", boldFont, 7, true));
            deductionsTable.addCell(createSimpleCell("Current", boldFont, 7, true));
            deductionsTable.addCell(createSimpleCell("YTD", boldFont, 7, true));

            // Federal Tax
            deductionsTable.addCell(createSimpleCell("Federal Income Tax", regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getFederalTax()), regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateFederalWithholding()), regularFont, 7, false));

            // Social Security
            deductionsTable.addCell(createSimpleCell("Social Security Tax", regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getSocialSecurityTax()), regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateSocialSecurityEmployee()), regularFont, 7, false));

            // Medicare
            deductionsTable.addCell(createSimpleCell("Medicare Tax", regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getMedicareTax()), regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateMedicare()), regularFont, 7, false));

            // State Tax
            deductionsTable.addCell(createSimpleCell("State Income Tax", regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getStateTax()), regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateNyStateWithholding()), regularFont, 7, false));

            // Local Tax
            deductionsTable.addCell(createSimpleCell("Local Tax", regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getLocalTax()), regularFont, 7, false));
            deductionsTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateNyLocalWithholding()), regularFont, 7, false));

            // Insurance если активно
            if (Boolean.TRUE.equals(stub.getUserActivatedInsurance())) {
                deductionsTable.addCell(createSimpleCell("Health Insurance", regularFont, 7, false));
                deductionsTable.addCell(createSimpleCell("$" + formatAmount(stub.getHealthInsuranceChargePeriod()), regularFont, 7, false));
                deductionsTable.addCell(createSimpleCell("-", regularFont, 7, false));
            }

            document.add(deductionsTable);

            // Линия-разделитель
            addSeparatorLine(document);

            // SUMMARY секция
            document.add(new Paragraph("PAY SUMMARY")
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginTop(6)
                    .setMarginBottom(4));

            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(6);

            // Gross Pay
            summaryTable.addCell(createSimpleCell("Gross Pay", boldFont, 8, false));
            summaryTable.addCell(createSimpleCell("$" + formatAmount(stub.getTotalGrossPay()), boldFont, 8, false));
            summaryTable.addCell(createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateGrossPay()), boldFont, 8, false));

            // Total Deductions
            BigDecimal totalDeductions = stub.getTotalGrossPay().subtract(stub.getNetPay());
            BigDecimal ytdDeductions = yearToDateData.getYearToDateGrossPay().subtract(yearToDateData.getYearToDateNetPay());

            summaryTable.addCell(createSimpleCell("Total Deductions", regularFont, 8, false));
            summaryTable.addCell(createSimpleCell("$" + formatAmount(totalDeductions), regularFont, 8, false));
            summaryTable.addCell(createSimpleCell("$" + formatAmount(ytdDeductions), regularFont, 8, false));

            // Net Pay - с верхней границей для выделения
            Cell netPayLabel = createSimpleCell("NET PAY", boldFont, 8, false);
            netPayLabel.setBorderTop(new SolidBorder(0.5f));
            summaryTable.addCell(netPayLabel);

            Cell netPayCurrent = createSimpleCell("$" + formatAmount(stub.getNetPay()), boldFont, 8, false);
            netPayCurrent.setBorderTop(new SolidBorder(0.5f));
            summaryTable.addCell(netPayCurrent);

            Cell netPayYTD = createSimpleCell("$" + formatAmount(yearToDateData.getYearToDateNetPay()), boldFont, 8, false);
            netPayYTD.setBorderTop(new SolidBorder(0.5f));
            summaryTable.addCell(netPayYTD);

            document.add(summaryTable);

            // Sick Leave информация
            if (stub.getSickLeaveRemaining() != null) {
                addSeparatorLine(document);

                Table sickLeaveTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                        .useAllAvailableWidth()
                        .setMarginBottom(8);

                sickLeaveTable.addCell(createSimpleCell("Sick Leave Accrued: " + stub.getSickLeaveAccrued() + " hrs",
                        regularFont, 7, false));
                sickLeaveTable.addCell(createSimpleCell("Used: " + stub.getSickLeaveUsed() + " hrs",
                        regularFont, 7, false));
                sickLeaveTable.addCell(createSimpleCell("Balance: " + stub.getSickLeaveRemaining() + " hrs",
                        regularFont, 7, false));

                document.add(sickLeaveTable);
            }

            // Footer
            document.add(new Paragraph("This is an official payroll record. Please retain for your records.")
                    .setFont(regularFont)
                    .setFontSize(6)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            document.add(new Paragraph("Generated by Face-Check Corporation • " + LocalDate.now())
                    .setFont(regularFont)
                    .setFontSize(6)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true, end1);
            metric.recordOperationTime(timer, "paystubs_success");


            NotificationRequest notification = NotificationRequest.builder()
                    .message("Paystub for worker: " +stub.getEmployeeName() + " "+
                            " was successfully generated")
                    .build();

            notificationService.createNotification(stub.getCompanyId(), notification);

            return baos.toByteArray();

        } catch (Exception e) {
            metric.recordOperationTime(timer, "paystubs_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("paystubs_failed", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Простая ячейка без украшательств
    private Cell createSimpleCell(String text, PdfFont font, float fontSize, boolean isHeader) {
        if (text == null) text = "";
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setPadding(2)
                .setBorder(Border.NO_BORDER);

        // Только для заголовков таблицы добавляем нижнюю границу
        if (isHeader) {
            cell.setBorderBottom(new SolidBorder(0.5f));
        }

        return cell;
    }

    // Добавление тонкой линии-разделителя
    private void addSeparatorLine(Document document) {
        Table line = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setMarginTop(2)
                .setMarginBottom(2);

        Cell lineCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(0.5f))
                .setHeight(1);

        line.addCell(lineCell);
        document.add(line);
    }

    // Форматирование сумм
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }
}