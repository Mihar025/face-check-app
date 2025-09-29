package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
public class PayrollSummaryReportService {

    private final AmazonS3Service amazonS3Service;

    // Черно-белая цветовая схема
    private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);
    private static final DeviceRgb GRAY_LIGHT = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb GRAY_MEDIUM = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb GRAY_DARK = new DeviceRgb(100, 100, 100);

    // Date formatters с явным указанием Locale
    private static final DateTimeFormatter DATE_FORMAT_FULL = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_FORMAT_MONTH_DAY_YEAR = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT_FULL = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH);

    public byte[] generatePayrollSummaryReport(PayrollSummaryReportDTO reportData) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.setMargins(30, 30, 30, 30);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // 1. HEADER с логотипом
            addCompanyHeader(document, reportData, boldFont, regularFont);

            // 2. PERIOD DETAILS
            addPeriodDetails(document, reportData, boldFont, regularFont);

            // 3. GROSS PAY SUMMARY (без налогов)
            addGrossPaySummary(document, reportData, boldFont, regularFont);

            // 4. EMPLOYEE GROSS BREAKDOWN
            addEmployeeGrossBreakdown(document, reportData, boldFont, regularFont);

            // 5. FOOTER
            addReportFooter(document, regularFont);

            document.close();

            byte[] pdfByte = baos.toByteArray();

            // Сохраняем в S3 с тем же ключом
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "_");

            String periodPart;
            String fileName;

            if (reportData.getReportType().equals("Monthly")) {
                periodPart = String.format("monthly/%02d", reportData.getPeriodStart().getMonthValue());
                fileName = String.format("payroll_report_%d_%02d_%s.pdf",
                        reportData.getPeriodStart().getYear(),
                        reportData.getPeriodStart().getMonthValue(),
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            else if (reportData.getReportType().equals("Quarterly")) {
                int quarter = (reportData.getPeriodStart().getMonthValue() - 1) / 3 + 1;
                periodPart = String.format("Q%d", quarter);
                fileName = String.format("payroll_report_%d_Q%d_%s.pdf",
                        reportData.getPeriodStart().getYear(),
                        quarter,
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            else {
                periodPart = "custom";
                fileName = String.format("payroll_report_%s_to_%s.pdf",
                        reportData.getPeriodStart().format(DateTimeFormatter.BASIC_ISO_DATE),
                        reportData.getPeriodEnd().format(DateTimeFormatter.BASIC_ISO_DATE));
            }

            String key = String.format("%s_%d/reports/payroll/%d/%s/%s",
                    companyKeyPart,
                    reportData.getCompanyId(),
                    reportData.getPeriodStart().getYear(),
                    periodPart,
                    fileName
            );

            amazonS3Service.uploadPdfToS3(pdfByte, key);
            return pdfByte;

        } catch (Exception e) {
            throw new RuntimeException("Error generating Payroll Report", e);
        }
    }

    private void addCompanyHeader(Document document, PayrollSummaryReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) throws Exception {
        // Таблица для логотипа и информации
        Table headerTable = new Table(new float[]{1, 3})
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Логотип Facecheck
        InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
        if (logoStream != null) {
            ImageData logoData = ImageDataFactory.create(StreamUtil.inputStreamToArray(logoStream));
            Image logo = new Image(logoData).scaleToFit(80, 80);
            Cell logoCell = new Cell()
                    .add(logo)
                    .setBorder(null)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(logoCell);
        } else {
            headerTable.addCell(new Cell().setBorder(null));
        }

        // Информация о компании
        Cell companyCell = new Cell().setBorder(null);
        companyCell.add(new Paragraph(data.getCompanyName())
                .setFont(boldFont)
                .setFontSize(20)
                .setMarginBottom(5));

        String address = String.format("%s, %s, %s %s",
                data.getCompanyAddress(),
                data.getCompanyCity(),
                data.getCompanyState(),
                data.getCompanyZipCode());
        companyCell.add(new Paragraph(address)
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(GRAY_DARK));

        if (data.getCompanyPhone() != null) {
            companyCell.add(new Paragraph("Phone: " + data.getCompanyPhone())
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setFontColor(GRAY_DARK));
        }

        headerTable.addCell(companyCell);
        document.add(headerTable);

        // Разделительная линия
        Table lineTable = new Table(1).useAllAvailableWidth();
        lineTable.addCell(new Cell()
                .setBorderTop(new SolidBorder(GRAY_MEDIUM, 2))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(1));
        document.add(lineTable);

        // Заголовок отчета
        document.add(new Paragraph("GROSS PAY REPORT")
                .setFont(boldFont)
                .setFontSize(26)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(15)
                .setMarginBottom(5));

        // Подзаголовок
        document.add(new Paragraph("Powered by Facecheck")
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_DARK)
                .setMarginBottom(20));
    }

    private void addPeriodDetails(Document document, PayrollSummaryReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) {
        Table periodTable = new Table(new float[]{1, 1})
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Заголовок секции
        Cell periodHeader = new Cell(1, 2)
                .add(new Paragraph("REPORT PERIOD")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        periodTable.addCell(periodHeader);

        // Тип отчета
        periodTable.addCell(createInfoCell("Report Type:", boldFont, 11, false));
        periodTable.addCell(createInfoCell(data.getReportType(), boldFont, 11, false));

        // Дата начала в формате MM/dd/yyyy
        periodTable.addCell(createInfoCell("Period Start:", boldFont, 11, true));
        String startDate = data.getPeriodStart().format(DATE_FORMAT_SHORT);
        periodTable.addCell(createInfoCell(startDate, regularFont, 11, true));

        // Дата окончания в формате MM/dd/yyyy
        periodTable.addCell(createInfoCell("Period End:", boldFont, 11, false));
        String endDate = data.getPeriodEnd().format(DATE_FORMAT_SHORT);
        periodTable.addCell(createInfoCell(endDate, regularFont, 11, false));

        // Полная дата начала (с днем недели)
        periodTable.addCell(createInfoCell("Start Day:", boldFont, 11, true));
        String fullStartDate = data.getPeriodStart().format(DATE_FORMAT_FULL);
        periodTable.addCell(createInfoCell(fullStartDate, regularFont, 11, true));

        // Полная дата окончания (с днем недели)
        periodTable.addCell(createInfoCell("End Day:", boldFont, 11, false));
        String fullEndDate = data.getPeriodEnd().format(DATE_FORMAT_FULL);
        periodTable.addCell(createInfoCell(fullEndDate, regularFont, 11, false));

        // Общее количество дней
        periodTable.addCell(createInfoCell("Total Days:", boldFont, 11, true));
        long totalDays = ChronoUnit.DAYS.between(data.getPeriodStart(), data.getPeriodEnd()) + 1;
        periodTable.addCell(createInfoCell(String.valueOf(totalDays) + " days", regularFont, 11, true));

        // Дата и время генерации
        periodTable.addCell(createInfoCell("Generated:", boldFont, 11, false));
        String generatedDateTime = LocalDate.now().format(DATE_FORMAT_MONTH_DAY_YEAR) +
                " at " + LocalTime.now().format(TIME_FORMAT);
        periodTable.addCell(createInfoCell(generatedDateTime, regularFont, 11, false));

        document.add(periodTable);
    }

    private void addGrossPaySummary(Document document, PayrollSummaryReportDTO data,
                                    PdfFont boldFont, PdfFont regularFont) {
        Table summaryTable = new Table(new float[]{2, 1})
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Заголовок секции
        Cell headerCell = new Cell(1, 2)
                .add(new Paragraph("GROSS PAY SUMMARY")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        summaryTable.addCell(headerCell);

        // Total Employees
        summaryTable.addCell(createSummaryCell("Total Active Employees:", boldFont, 11, false));
        summaryTable.addCell(createSummaryCell(data.getTotalEmployees().toString() + " employees", boldFont, 11, false));

        // Total Hours
        summaryTable.addCell(createSummaryCell("Total Hours Worked:", boldFont, 11, true));
        summaryTable.addCell(createSummaryCell(formatDecimal(data.getTotalHoursWorked()) + " hours", boldFont, 11, true));

        // Пустая строка для разделения
        Cell spacerCell = new Cell(1, 2)
                .setHeight(5)
                .setBorder(null);
        summaryTable.addCell(spacerCell);

        // TOTAL GROSS PAY - крупнее и выделено
        Cell grossLabelCell = new Cell()
                .add(new Paragraph("TOTAL GROSS PAY:")
                        .setFont(boldFont)
                        .setFontSize(14))
                .setBackgroundColor(GRAY_LIGHT)
                .setBorderTop(new SolidBorder(BLACK, 2))
                .setBorderBottom(new SolidBorder(BLACK, 2))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(12);
        summaryTable.addCell(grossLabelCell);

        Cell grossValueCell = new Cell()
                .add(new Paragraph("$" + formatDecimal(data.getTotalGrossPay()))
                        .setFont(boldFont)
                        .setFontSize(14))
                .setBackgroundColor(GRAY_LIGHT)
                .setBorderTop(new SolidBorder(BLACK, 2))
                .setBorderBottom(new SolidBorder(BLACK, 2))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(12);
        summaryTable.addCell(grossValueCell);

        document.add(summaryTable);
    }

    private void addEmployeeGrossBreakdown(Document document, PayrollSummaryReportDTO data,
                                           PdfFont boldFont, PdfFont regularFont) {
        if (data.getEmployeeBreakdown() == null || data.getEmployeeBreakdown().isEmpty()) {
            document.add(new Paragraph("No employee data available for this period.")
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));
            return;
        }

        // Упрощенная таблица: Имя | Regular Hrs | OT Hrs | Total Hrs | Rate | Gross Pay
        Table employeeTable = new Table(new float[]{3f, 1f, 1f, 1f, 1f, 1.5f})
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Заголовок секции
        Cell breakdownHeader = new Cell(1, 6)
                .add(new Paragraph("EMPLOYEE GROSS PAY BREAKDOWN")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(breakdownHeader);

        // Информация о периоде для всех сотрудников - используем короткий формат дат
        String periodInfo = String.format("Pay Period: %s to %s",
                data.getPeriodStart().format(DATE_FORMAT_SHORT),
                data.getPeriodEnd().format(DATE_FORMAT_SHORT));
        Cell periodCell = new Cell(1, 6)
                .add(new Paragraph(periodInfo)
                        .setFont(regularFont)
                        .setFontSize(10)
                        .setItalic())
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        employeeTable.addCell(periodCell);

        // Заголовки колонок
        employeeTable.addCell(createHeaderCell("Employee Name", boldFont));
        employeeTable.addCell(createHeaderCell("Regular", boldFont));
        employeeTable.addCell(createHeaderCell("Overtime", boldFont));
        employeeTable.addCell(createHeaderCell("Total", boldFont));
        employeeTable.addCell(createHeaderCell("Rate/Hr", boldFont));
        employeeTable.addCell(createHeaderCell("Gross Pay", boldFont));

        // Подзаголовки с единицами
        employeeTable.addCell(createSubHeaderCell("", regularFont));
        employeeTable.addCell(createSubHeaderCell("hours", regularFont));
        employeeTable.addCell(createSubHeaderCell("hours", regularFont));
        employeeTable.addCell(createSubHeaderCell("hours", regularFont));
        employeeTable.addCell(createSubHeaderCell("$", regularFont));
        employeeTable.addCell(createSubHeaderCell("$", regularFont));

        // Данные по сотрудникам
        boolean isEven = false;
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalRegular = BigDecimal.ZERO;
        BigDecimal totalOvertime = BigDecimal.ZERO;
        BigDecimal totalHours = BigDecimal.ZERO;

        for (EmployeeSummaryDTO employee : data.getEmployeeBreakdown()) {
            Color bgColor = isEven ? GRAY_LIGHT : Color.WHITE;

            BigDecimal empTotalHours = employee.getRegularHours().add(employee.getOvertimeHours());

            // Имя сотрудника
            employeeTable.addCell(createDataCell(employee.getEmployeeName(), regularFont, bgColor, TextAlignment.LEFT));

            // Часы
            employeeTable.addCell(createDataCell(formatDecimal(employee.getRegularHours()), regularFont, bgColor, TextAlignment.CENTER));
            employeeTable.addCell(createDataCell(formatDecimal(employee.getOvertimeHours()), regularFont, bgColor, TextAlignment.CENTER));
            employeeTable.addCell(createDataCell(formatDecimal(empTotalHours), boldFont, bgColor, TextAlignment.CENTER));

            // Ставка
            employeeTable.addCell(createDataCell(formatDecimal(employee.getHourlyRate()), regularFont, bgColor, TextAlignment.CENTER));

            // Gross Pay
            employeeTable.addCell(createDataCell(formatDecimal(employee.getGrossPay()), boldFont, bgColor, TextAlignment.RIGHT));

            totalGross = totalGross.add(employee.getGrossPay());
            totalRegular = totalRegular.add(employee.getRegularHours());
            totalOvertime = totalOvertime.add(employee.getOvertimeHours());
            totalHours = totalHours.add(empTotalHours);
            isEven = !isEven;
        }

        // Разделительная линия
        Cell separatorCell = new Cell(1, 6)
                .setBorderTop(new SolidBorder(BLACK, 2))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(2);
        employeeTable.addCell(separatorCell);

        // Итоговая строка
        Cell totalLabelCell = new Cell()
                .add(new Paragraph("TOTALS:")
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(8);
        employeeTable.addCell(totalLabelCell);

        // Total Regular Hours
        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatDecimal(totalRegular))
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        // Total Overtime Hours
        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatDecimal(totalOvertime))
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        // Total Hours
        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatDecimal(totalHours))
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        // Empty cell for Rate
        employeeTable.addCell(new Cell()
                .add(new Paragraph("—")
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        // Total Gross
        employeeTable.addCell(new Cell()
                .add(new Paragraph("$" + formatDecimal(totalGross))
                        .setFont(boldFont)
                        .setFontSize(12))
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(8));

        document.add(employeeTable);
    }

    private void addReportFooter(Document document, PdfFont regularFont) {
        document.add(new Paragraph("\n"));

        // Разделительная линия
        Table lineTable = new Table(1).useAllAvailableWidth().setMarginTop(10);
        lineTable.addCell(new Cell()
                .setBorderTop(new SolidBorder(GRAY_MEDIUM, 1))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(1));
        document.add(lineTable);

        document.add(new Paragraph("This report was generated by Facecheck")
                .setFont(regularFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_DARK)
                .setMarginTop(15));

        document.add(new Paragraph("All calculations are based on recorded hours and approved hourly rates")
                .setFont(regularFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_DARK));

        String dateTime = LocalDate.now().format(DATE_FORMAT_FULL) +
                " at " + LocalTime.now().format(TIME_FORMAT_FULL);
        document.add(new Paragraph("Report generated on " + dateTime)
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_DARK)
                .setMarginTop(5));
    }

    // Вспомогательные методы
    private Cell createInfoCell(String text, PdfFont font, float fontSize, boolean isGray) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setBackgroundColor(isGray ? GRAY_LIGHT : Color.WHITE)
                .setPadding(5)
                .setBorderBottom(new SolidBorder(GRAY_MEDIUM, 0.5f))
                .setBorderTop(null)
                .setBorderLeft(null)
                .setBorderRight(null);
    }

    private Cell createSummaryCell(String text, PdfFont font, float fontSize, boolean isGray) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setBackgroundColor(isGray ? GRAY_LIGHT : Color.WHITE)
                .setPadding(6)
                .setBorderBottom(new SolidBorder(GRAY_MEDIUM, 0.5f))
                .setBorderTop(null)
                .setBorderLeft(null)
                .setBorderRight(null);
    }

    private Cell createHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10).setFontColor(Color.WHITE))
                .setBackgroundColor(new DeviceRgb(50, 50, 50))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(6);
    }

    private Cell createSubHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(8).setItalic())
                .setBackgroundColor(GRAY_LIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3);
    }

    private Cell createDataCell(String text, PdfFont font, Color bgColor, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBackgroundColor(bgColor)
                .setTextAlignment(align)
                .setPadding(5)
                .setBorderBottom(new SolidBorder(GRAY_MEDIUM, 0.5f))
                .setBorderTop(null)
                .setBorderLeft(null)
                .setBorderRight(null);
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }
}