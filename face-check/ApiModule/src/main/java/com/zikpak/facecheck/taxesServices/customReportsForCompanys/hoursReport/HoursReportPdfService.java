package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
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
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
public class HoursReportPdfService {

    private final AmazonS3Service amazonS3Service;
    private final MetricsForPdfServices metric;

    public byte[] generateHoursReport(HoursReportDTO reportData, Integer companyId) {
        final String FORM = "HoursReport";
        metric.recordRequest(FORM);
        Timer.Sample timer = metric.startTimer();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.setMargins(25, 25, 35, 25);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // 1. HEADER с логотипом и названием компании
            addReportHeader(document, reportData, boldFont, regularFont);

            // 2. PERIOD INFO
            addPeriodInfo(document, reportData, boldFont, regularFont);

            // 3. SUMMARY TOTALS (главные цифры часов)
            addHoursSummaryTotals(document, reportData, boldFont, regularFont);

            // 4. EMPLOYEE HOURS BREAKDOWN
            addEmployeeHoursBreakdown(document, reportData, boldFont, regularFont);

            // 5. DAILY HOURS BREAKDOWN
            addDailyHoursBreakdown(document, reportData, boldFont, regularFont);

            // 6. TOP PERFORMERS
            addTopPerformers(document, reportData, boldFont, regularFont);

            // 7. FOOTER
            addReportFooter(document, regularFont);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            // ✅ S3 key для Hours Report (как payroll, но hoursReport)
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("hoursReport_%d_%d.pdf",
                    companyId,
                    reportData.getPeriodStart().getYear()
            );

            String key = String.format("%s/%d/hoursReport/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );
            long ms = System.currentTimeMillis();
            amazonS3Service.uploadPdfToS3(pdfBytes, key);
            long end = System.currentTimeMillis() - ms;

            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end);
            metric.recordOperationTime(timer,"hours_report_success");

            return pdfBytes;

        } catch (Exception e) {
            metric.recordOperationTime(timer,"hours_report_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("hours_report_failed", e.getMessage(), e);
            throw new RuntimeException("Error generating Hours Report", e);
        }
    }

    private void addReportHeader(Document document, HoursReportDTO data,
                                 PdfFont boldFont, PdfFont regularFont) throws Exception {

        // Powered by Facecheck
        document.add(new Paragraph("Powered by Facecheck")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(10));

        // Header table: Logo + Company Info + Report Title
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Facecheck Logo
        InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
        ImageData facecheckLogoData = ImageDataFactory.create(StreamUtil.inputStreamToArray(logoStream));
        Cell logoCell = new Cell()
                .add(new com.itextpdf.layout.element.Image(facecheckLogoData).scaleToFit(80, 80))
                .setBorder(null);
        headerTable.addCell(logoCell);

        // Company Info
        Cell companyCell = new Cell().setBorder(null);
        companyCell.add(new Paragraph(data.getCompanyName()).setFont(boldFont).setFontSize(14));
        companyCell.add(new Paragraph(data.getCompanyAddress()).setFont(regularFont).setFontSize(10));
        companyCell.add(new Paragraph(data.getCompanyCity() + ", " + data.getCompanyState() + " " + data.getCompanyZipCode())
                .setFont(regularFont).setFontSize(10));
        companyCell.add(new Paragraph("Phone: " + data.getCompanyPhone()).setFont(regularFont).setFontSize(10));
        headerTable.addCell(companyCell);

        // Report Title
        Cell titleCell = new Cell().setBorder(null);
        titleCell.add(new Paragraph("HOURS SUMMARY REPORT")
                .setFont(boldFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.RIGHT));
        titleCell.add(new Paragraph("Generated: " + LocalDate.now())
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
        headerTable.addCell(titleCell);

        document.add(headerTable);
    }

    private void addPeriodInfo(Document document, HoursReportDTO data,
                               PdfFont boldFont, PdfFont regularFont) {

        Table periodTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        Cell periodHeader = new Cell(1, 3)
                .add(new Paragraph("REPORT PERIOD").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        periodTable.addCell(periodHeader);

        periodTable.addCell(createStyledCell("Report Type:", boldFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        periodTable.addCell(createStyledCell(data.getReportType(), regularFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        periodTable.addCell(createStyledCell("", regularFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));

        periodTable.addCell(createStyledCell("Start Date:", boldFont, 10, Color.WHITE));
        periodTable.addCell(createStyledCell(data.getPeriodStart().toString(), regularFont, 10, Color.WHITE));
        periodTable.addCell(createStyledCell("", regularFont, 10, Color.WHITE));

        periodTable.addCell(createStyledCell("End Date:", boldFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        periodTable.addCell(createStyledCell(data.getPeriodEnd().toString(), regularFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        periodTable.addCell(createStyledCell("", regularFont, 10,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));

        document.add(periodTable);
    }

    private void addHoursSummaryTotals(Document document, HoursReportDTO data,
                                       PdfFont boldFont, PdfFont regularFont) {

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        Cell summaryHeader = new Cell(1, 2)
                .add(new Paragraph("HOURS SUMMARY TOTALS").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        summaryTable.addCell(summaryHeader);

        // Total Employees
        summaryTable.addCell(createHighlightCell("TOTAL EMPLOYEES:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(data.getTotalEmployees().toString(), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT));

        // Total Hours
        summaryTable.addCell(createHighlightCell("TOTAL HOURS WORKED:", boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(220, 255, 220), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getTotalHours()) + " hrs", boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(220, 255, 220), TextAlignment.RIGHT));

        // Regular Hours
        summaryTable.addCell(createHighlightCell("REGULAR HOURS:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getTotalRegularHours()) + " hrs", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.RIGHT));

        // Overtime Hours
        summaryTable.addCell(createHighlightCell("OVERTIME HOURS:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getTotalOvertimeHours()) + " hrs", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.RIGHT));

        // Overtime Percentage
        summaryTable.addCell(createHighlightCell("OVERTIME PERCENTAGE:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getOvertimePercentage()) + "%", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT));

        // Average Hours Per Employee
        summaryTable.addCell(createHighlightCell("AVERAGE HOURS PER EMPLOYEE:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getAverageHoursPerEmployee()) + " hrs", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255), TextAlignment.RIGHT));

        document.add(summaryTable);
    }

    private void addEmployeeHoursBreakdown(Document document, HoursReportDTO data,
                                           PdfFont boldFont, PdfFont regularFont) {

        if (data.getEmployeeHours() == null || data.getEmployeeHours().isEmpty()) {
            return;
        }

        Table employeeTable = new Table(UnitValue.createPercentArray(new float[]{2.5f, 1, 1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        Cell breakdownHeader = new Cell(1, 6)
                .add(new Paragraph("EMPLOYEE HOURS BREAKDOWN").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(breakdownHeader);

        // Column headers
        employeeTable.addHeaderCell(createHeaderCell("Employee Name", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Regular Hrs", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("OT Hrs", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Total Hrs", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Daily Avg", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("OT %", boldFont, 10));

        // Employee rows
        boolean isEvenRow = true;
        for (EmployeeHoursDTO employee : data.getEmployeeHours()) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255) : Color.WHITE;

            employeeTable.addCell(createEmployeeCell(employee.getEmployeeName(), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getRegularHours()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getOvertimeHours()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getTotalHours()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getDailyAverage()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getOvertimeRate()) + "%", regularFont, 9, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(employeeTable);
    }

    private void addDailyHoursBreakdown(Document document, HoursReportDTO data,
                                        PdfFont boldFont, PdfFont regularFont) {

        if (data.getDailyHours() == null || data.getDailyHours().isEmpty()) {
            return;
        }

        Table dailyTable = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        Cell dailyHeader = new Cell(1, 5)
                .add(new Paragraph("DAILY HOURS BREAKDOWN").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        dailyTable.addCell(dailyHeader);

        // Column headers
        dailyTable.addHeaderCell(createHeaderCell("Date", boldFont, 10));
        dailyTable.addHeaderCell(createHeaderCell("Total Hours", boldFont, 10));
        dailyTable.addHeaderCell(createHeaderCell("Employees", boldFont, 10));
        dailyTable.addHeaderCell(createHeaderCell("Avg/Employee", boldFont, 10));
        dailyTable.addHeaderCell(createHeaderCell("OT Hours", boldFont, 10));

        // Daily rows
        boolean isEvenRow = true;
        for (Map.Entry<LocalDate, DailyHoursDTO> entry : data.getDailyHours().entrySet()) {
            DailyHoursDTO daily = entry.getValue();
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(255, 248, 235) : Color.WHITE;

            dailyTable.addCell(createEmployeeCell(daily.getDate().toString(), regularFont, 9, rowColor));
            dailyTable.addCell(createEmployeeCell(formatDecimal(daily.getTotalHours()), regularFont, 9, rowColor));
            dailyTable.addCell(createEmployeeCell(daily.getEmployeesWorked().toString(), regularFont, 9, rowColor));
            dailyTable.addCell(createEmployeeCell(formatDecimal(daily.getAverageHoursPerEmployee()), regularFont, 9, rowColor));
            dailyTable.addCell(createEmployeeCell(formatDecimal(daily.getTotalOvertimeHours()), regularFont, 9, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(dailyTable);
    }

    private void addTopPerformers(Document document, HoursReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) {

        if (data.getTopPerformers() == null || data.getTopPerformers().isEmpty()) {
            return;
        }

        Table topTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        Cell topHeader = new Cell(1, 4)
                .add(new Paragraph("TOP 5 PERFORMERS (BY HOURS)").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(34, 139, 34))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        topTable.addCell(topHeader);

        // Column headers
        topTable.addHeaderCell(createHeaderCell("Employee Name", boldFont, 10));
        topTable.addHeaderCell(createHeaderCell("Total Hours", boldFont, 10));
        topTable.addHeaderCell(createHeaderCell("Daily Avg", boldFont, 10));
        topTable.addHeaderCell(createHeaderCell("Total Earnings", boldFont, 10));

        // Top performer rows
        boolean isEvenRow = true;
        for (EmployeeHoursDTO performer : data.getTopPerformers()) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240) : Color.WHITE;

            topTable.addCell(createEmployeeCell(performer.getEmployeeName(), regularFont, 9, rowColor));
            topTable.addCell(createEmployeeCell(formatDecimal(performer.getTotalHours()), regularFont, 9, rowColor));
            topTable.addCell(createEmployeeCell(formatDecimal(performer.getDailyAverage()), regularFont, 9, rowColor));
            topTable.addCell(createEmployeeCell("$" + formatDecimal(performer.getTotalEarnings()), regularFont, 9, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(topTable);
    }

    private void addReportFooter(Document document, PdfFont regularFont) {
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("This report provides detailed hours analysis for the specified period. " +
                "Use this data for labor management, project planning, and productivity optimization.")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        document.add(new Paragraph("Generated by Facecheck Payroll System - " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5));
    }

    // ✅ Вспомогательные методы (такие же как в payroll)
    private Cell createStyledCell(String text, PdfFont font, float fontSize,
                                  com.itextpdf.kernel.color.Color backgroundColor) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(backgroundColor)
                .setPadding(5);
    }

    private Cell createHighlightCell(String text, PdfFont font, float fontSize,
                                     com.itextpdf.kernel.color.Color backgroundColor,
                                     TextAlignment alignment) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(alignment)
                .setBackgroundColor(backgroundColor)
                .setPadding(6);
    }

    private Cell createHeaderCell(String text, PdfFont font, float fontSize) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(230, 230, 230))
                .setPadding(5);
    }

    private Cell createEmployeeCell(String text, PdfFont font, float fontSize,
                                    com.itextpdf.kernel.color.Color backgroundColor) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(backgroundColor)
                .setPadding(4);
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%.2f", value);
    }
}
