package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class HoursReportPdfService {

    private final AmazonS3Service amazonS3Service;

    // Цвета для таблиц - черно-белый стиль
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(0, 0, 0);  // Черный
    private static final DeviceRgb GRAY_COLOR = new DeviceRgb(245, 245, 245); // Очень светлый серый
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(200, 200, 200); // Серый для границ

    // Форматтеры дат с явным указанием Locale
    private static final DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_FORMAT_FULL = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_FORMAT_MONTH_DAY = DateTimeFormatter.ofPattern("MM/dd");
    private static final DateTimeFormatter DATE_FORMAT_MONTH_DAY_YEAR = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' h:mm a", Locale.ENGLISH);

    public byte[] generateHoursReport(HoursReportDTO reportData, Integer companyId) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.setMargins(30, 30, 30, 30);

            PdfFont regularFont = PdfFontFactory.createFont(FontConstants.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

            // 1. HEADER - Название компании и отчета
            addCompanyHeader(document, reportData, boldFont);

            // 2. PERIOD INFO - Информация о периоде
            addPeriodSection(document, reportData, boldFont, regularFont);

            // 3. HOURS SUMMARY - Общая статистика часов
            addHoursSummary(document, reportData, boldFont, regularFont);

            // 4. EMPLOYEE HOURS TABLE - Таблица с часами по сотрудникам
            addEmployeeHoursTable(document, reportData, boldFont, regularFont);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            // Сохраняем в S3
            String fileName = generateFileName(reportData);
            String key = generateS3Key(reportData, companyId, fileName);
            amazonS3Service.uploadPdfToS3(pdfBytes, key);

            return pdfBytes;

        } catch (Exception e) {
            throw new RuntimeException("Error generating Hours Report", e);
        }
    }

    private void addCompanyHeader(Document document, HoursReportDTO data, PdfFont boldFont) throws Exception {
        // Таблица для логотипа и информации о компании
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
                .setFont(boldFont)
                .setFontSize(10)
                .setFontColor(new DeviceRgb(100, 100, 100)));

        if (data.getCompanyPhone() != null) {
            companyCell.add(new Paragraph("Phone: " + data.getCompanyPhone())
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setFontColor(new DeviceRgb(100, 100, 100)));
        }

        headerTable.addCell(companyCell);
        document.add(headerTable);

        // Разделительная линия
        Table lineTable = new Table(1).useAllAvailableWidth();
        lineTable.addCell(new Cell()
                .setBorderTop(new com.itextpdf.layout.border.SolidBorder(BORDER_COLOR, 2))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(1));
        document.add(lineTable);

        // Заголовок отчета
        document.add(new Paragraph("HOURS REPORT")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(15)
                .setMarginBottom(5));

        // Подзаголовок "Powered by Facecheck"
        document.add(new Paragraph("Powered by Facecheck Payroll System")
                .setFont(boldFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(150, 150, 150))
                .setMarginBottom(20));
    }

    private void addPeriodSection(Document document, HoursReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) {
        Table periodTable = new Table(new float[]{1, 2})
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Заголовок секции
        Cell periodHeader = new Cell(1, 2)
                .add(new Paragraph("REPORT DETAILS")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        periodTable.addCell(periodHeader);

        // Тип отчета
        periodTable.addCell(createInfoCell("Report Type:", boldFont, 11, false));
        periodTable.addCell(createInfoCell(data.getReportType(), regularFont, 11, false));

        // Период отчета в формате MM/dd/yyyy
        periodTable.addCell(createInfoCell("Report Period:", boldFont, 11, true));
        String period = String.format("%s to %s",
                data.getPeriodStart().format(DATE_FORMAT_SHORT),
                data.getPeriodEnd().format(DATE_FORMAT_SHORT));
        periodTable.addCell(createInfoCell(period, regularFont, 11, true));

        // Дата начала с днем недели
        periodTable.addCell(createInfoCell("Start Date:", boldFont, 11, false));
        String startDateFull = data.getPeriodStart().format(DATE_FORMAT_FULL);
        periodTable.addCell(createInfoCell(startDateFull, regularFont, 11, false));

        // Дата окончания с днем недели
        periodTable.addCell(createInfoCell("End Date:", boldFont, 11, true));
        String endDateFull = data.getPeriodEnd().format(DATE_FORMAT_FULL);
        periodTable.addCell(createInfoCell(endDateFull, regularFont, 11, true));

        // Количество дней в периоде
        periodTable.addCell(createInfoCell("Total Days:", boldFont, 11, false));
        long totalDays = ChronoUnit.DAYS.between(data.getPeriodStart(), data.getPeriodEnd()) + 1;
        periodTable.addCell(createInfoCell(totalDays + " days", regularFont, 11, false));

        // Дата и время генерации
        periodTable.addCell(createInfoCell("Generated:", boldFont, 11, true));
        String generatedDateTime = LocalDateTime.now().format(DATETIME_FORMAT);
        periodTable.addCell(createInfoCell(generatedDateTime, regularFont, 11, true));

        document.add(periodTable);
    }

    private Cell createInfoCell(String text, PdfFont font, float fontSize, boolean isGray) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setBackgroundColor(isGray ? GRAY_COLOR : Color.WHITE)
                .setPadding(5)
                .setBorderBottom(new com.itextpdf.layout.border.SolidBorder(BORDER_COLOR, 0.5f))
                .setBorderTop(null)
                .setBorderLeft(null)
                .setBorderRight(null);
    }

    private void addHoursSummary(Document document, HoursReportDTO data,
                                 PdfFont boldFont, PdfFont regularFont) {
        Table summaryTable = new Table(new float[]{2, 1})
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Заголовок секции
        Cell headerCell = new Cell(1, 2)
                .add(new Paragraph("HOURS SUMMARY")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        summaryTable.addCell(headerCell);

        // Данные
        summaryTable.addCell(createSummaryCell("Total Active Employees:", boldFont, 11, false));
        summaryTable.addCell(createSummaryCell(data.getTotalEmployees().toString(), boldFont, 11, false));

        summaryTable.addCell(createSummaryCell("Regular Hours Worked:", regularFont, 11, true));
        summaryTable.addCell(createSummaryCell(formatHours(data.getTotalRegularHours()) + " hrs", regularFont, 11, true));

        summaryTable.addCell(createSummaryCell("Overtime Hours Worked:", regularFont, 11, false));
        summaryTable.addCell(createSummaryCell(formatHours(data.getTotalOvertimeHours()) + " hrs", regularFont, 11, false));

        // Total с выделением
        Cell totalLabelCell = new Cell()
                .add(new Paragraph("TOTAL HOURS WORKED:")
                        .setFont(boldFont)
                        .setFontSize(12))
                .setBackgroundColor(GRAY_COLOR)
                .setBorderTop(new com.itextpdf.layout.border.SolidBorder(HEADER_COLOR, 1))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(8);
        summaryTable.addCell(totalLabelCell);

        Cell totalValueCell = new Cell()
                .add(new Paragraph(formatHours(data.getTotalHours()) + " hrs")
                        .setFont(boldFont)
                        .setFontSize(12))
                .setBackgroundColor(GRAY_COLOR)
                .setBorderTop(new com.itextpdf.layout.border.SolidBorder(HEADER_COLOR, 1))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(8);
        summaryTable.addCell(totalValueCell);

        document.add(summaryTable);
    }

    private void addEmployeeHoursTable(Document document, HoursReportDTO data,
                                       PdfFont boldFont, PdfFont regularFont) {
        if (data.getEmployeeHours() == null || data.getEmployeeHours().isEmpty()) {
            document.add(new Paragraph("No employee hours data available for this period.")
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));
            return;
        }

        // Таблица с периодами: Имя | Period | Regular | Overtime | Total
        Table employeeTable = new Table(new float[]{2.5f, 2.5f, 1, 1, 1})
                .useAllAvailableWidth();

        // Заголовок секции
        Cell headerCell = new Cell(1, 5)
                .add(new Paragraph("EMPLOYEE HOURS DETAILS")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(Color.WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(headerCell);

        // Заголовки колонок
        employeeTable.addCell(createHeaderCell("Employee Name", boldFont));
        employeeTable.addCell(createHeaderCell("Work Period", boldFont));
        employeeTable.addCell(createHeaderCell("Regular", boldFont));
        employeeTable.addCell(createHeaderCell("Overtime", boldFont));
        employeeTable.addCell(createHeaderCell("Total", boldFont));

        // Подзаголовки с единицами измерения
        employeeTable.addCell(createSubHeaderCell("", regularFont));
        employeeTable.addCell(createSubHeaderCell("(From - To)", regularFont));
        employeeTable.addCell(createSubHeaderCell("(hours)", regularFont));
        employeeTable.addCell(createSubHeaderCell("(hours)", regularFont));
        employeeTable.addCell(createSubHeaderCell("(hours)", regularFont));

        // Данные по сотрудникам
        boolean isEven = false;
        for (EmployeeHoursDTO employee : data.getEmployeeHours()) {
            Color bgColor = isEven ? GRAY_COLOR : Color.WHITE;

            // Имя сотрудника
            employeeTable.addCell(createEmployeeDataCell(employee.getEmployeeName(), regularFont, bgColor, TextAlignment.LEFT));

            // Период работы - исправленный формат
            String period = String.format("%s - %s",
                    data.getPeriodStart().format(DATE_FORMAT_SHORT),
                    data.getPeriodEnd().format(DATE_FORMAT_SHORT));
            employeeTable.addCell(createEmployeeDataCell(period, regularFont, bgColor, TextAlignment.CENTER));

            // Часы
            employeeTable.addCell(createEmployeeDataCell(formatHours(employee.getRegularHours()), regularFont, bgColor, TextAlignment.CENTER));
            employeeTable.addCell(createEmployeeDataCell(formatHours(employee.getOvertimeHours()), regularFont, bgColor, TextAlignment.CENTER));
            employeeTable.addCell(createEmployeeDataCell(formatHours(employee.getTotalHours()), boldFont, bgColor, TextAlignment.CENTER));

            isEven = !isEven;
        }

        // Разделительная линия перед итогами
        Cell separatorCell = new Cell(1, 5)
                .setBorderTop(new com.itextpdf.layout.border.SolidBorder(HEADER_COLOR, 2))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(1);
        employeeTable.addCell(separatorCell);

        // Итоговая строка
        Cell totalLabelCell = new Cell(1, 2)
                .add(new Paragraph("GRAND TOTAL:")
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_COLOR)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(8);
        employeeTable.addCell(totalLabelCell);

        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatHours(data.getTotalRegularHours()))
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatHours(data.getTotalOvertimeHours()))
                        .setFont(boldFont)
                        .setFontSize(11))
                .setBackgroundColor(GRAY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        employeeTable.addCell(new Cell()
                .add(new Paragraph(formatHours(data.getTotalHours()))
                        .setFont(boldFont)
                        .setFontSize(12))
                .setBackgroundColor(GRAY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));

        document.add(employeeTable);

        // Футер с информацией
        document.add(new Paragraph("\n"));

        // Разделительная линия
        Table lineTable = new Table(1).useAllAvailableWidth().setMarginTop(10);
        lineTable.addCell(new Cell()
                .setBorderTop(new com.itextpdf.layout.border.SolidBorder(BORDER_COLOR, 1))
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setHeight(1));
        document.add(lineTable);

        document.add(new Paragraph("This report was generated by Facecheck Payroll System")
                .setFont(regularFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(100, 100, 100))
                .setMarginTop(15));

        document.add(new Paragraph("All hours are calculated based on actual clock-in/clock-out records")
                .setFont(regularFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(100, 100, 100)));

        // Дата и время генерации отчета
        String generatedAt = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm:ss a", Locale.ENGLISH));
        document.add(new Paragraph("Report generated on " + generatedAt)
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(150, 150, 150))
                .setMarginTop(5));
    }

    // Вспомогательные методы
    private Cell createCell(String text, PdfFont font, float fontSize, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setPadding(5)
                .setBorder(null);
    }

    private Cell createSummaryCell(String text, PdfFont font, float fontSize, boolean isGray) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setBackgroundColor(isGray ? GRAY_COLOR : Color.WHITE)
                .setPadding(6)
                .setBorderBottom(new com.itextpdf.layout.border.SolidBorder(BORDER_COLOR, 0.5f))
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
                .setBackgroundColor(GRAY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3);
    }

    private Cell createEmployeeDataCell(String text, PdfFont font, Color bgColor, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBackgroundColor(bgColor)
                .setTextAlignment(align)
                .setPadding(5)
                .setBorderBottom(new com.itextpdf.layout.border.SolidBorder(BORDER_COLOR, 0.5f))
                .setBorderTop(null)
                .setBorderLeft(null)
                .setBorderRight(null);
    }

    private Cell createDataCell(String text, PdfFont font, Color bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private String formatHours(BigDecimal hours) {
        if (hours == null) return "0.00";
        return String.format("%.2f", hours);
    }

    private String generateFileName(HoursReportDTO data) {
        if (data.getReportType().equals("Weekly")) {
            return String.format("hours_report_week_%s.pdf",
                    data.getPeriodStart().format(DateTimeFormatter.BASIC_ISO_DATE));
        } else if (data.getReportType().equals("Monthly")) {
            return String.format("hours_report_%d_%02d.pdf",
                    data.getPeriodStart().getYear(),
                    data.getPeriodStart().getMonthValue());
        }
        return String.format("hours_report_%s.pdf",
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    private String generateS3Key(HoursReportDTO data, Integer companyId, String fileName) {
        String companyKeyPart = data.getCompanyName()
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_");

        String periodPart = data.getReportType().equals("Monthly") ? "monthly" : "weekly";

        return String.format("%s_%d/reports/hours/%d/%s/%s",
                companyKeyPart,
                companyId,
                data.getPeriodStart().getYear(),
                periodPart,
                fileName);
    }
}