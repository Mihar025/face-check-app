package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;


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

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
public class PayrollSummaryReportService {

    private final AmazonS3Service amazonS3Service;
    private final MetricsForPdfServices metric;


    public byte[] generatePayrollSummaryReport(PayrollSummaryReportDTO reportData) {
        final String FORM = "SutaReport";
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

            // 3. SUMMARY TOTALS (главные цифры)
            addSummaryTotals(document, reportData, boldFont, regularFont);

            // 4. EMPLOYEE BREAKDOWN (по каждому сотруднику)
            addEmployeeBreakdown(document, reportData, boldFont, regularFont);

            addReportFooter(document, regularFont);

            document.close();

            byte[] pdfByte = baos.toByteArray();

            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("payrollReport_%d_%d.pdf",
                    reportData.getCompanyId(),
                    reportData.getPeriodStart().getYear()
            );

            String key = String.format("%s/%d/payrollReport_/%s",
                    companyKeyPart,
                    reportData.getCompanyId(),
                    fileName
            );
            long ms = System.currentTimeMillis();
            amazonS3Service.uploadPdfToS3(pdfByte, key);
            long end = System.currentTimeMillis() - ms;

            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end);
            metric.recordOperationTime(timer,"payroll_report_success");

            return pdfByte;

        } catch (Exception e) {
            metric.recordOperationTime(timer,"payroll_report_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("payroll_report_failed", e.getMessage(), e);
            throw new RuntimeException("Error generating Payroll Report", e);
        }
    }

    private void addReportHeader(Document document, PayrollSummaryReportDTO data,
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

        // Logo
        InputStream facecheckLogoPath = getClass().getResourceAsStream( "/assets/logo.jpg");
        ImageData facecheckLogoData = ImageDataFactory.create(StreamUtil.inputStreamToArray(facecheckLogoPath));
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
        titleCell.add(new Paragraph("PAYROLL SUMMARY REPORT")
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

    private void addPeriodInfo(Document document, PayrollSummaryReportDTO data,
                               PdfFont boldFont, PdfFont regularFont) {

        // Period Info Table
        Table periodTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Header
        Cell periodHeader = new Cell(1, 3)
                .add(new Paragraph("REPORT PERIOD").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        periodTable.addCell(periodHeader);

        // Period details
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


    private void addSummaryTotals(Document document, PayrollSummaryReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) {

        // Summary Totals Table
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell summaryHeader = new Cell(1, 2)
                .add(new Paragraph("PAYROLL SUMMARY TOTALS").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(34, 139, 34))
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
        summaryTable.addCell(createHighlightCell("TOTAL HOURS WORKED:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(formatDecimal(data.getTotalHoursWorked()) + " hrs", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.RIGHT));

        // Average Hourly Rate
        summaryTable.addCell(createHighlightCell("AVERAGE HOURLY RATE:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getAverageHourlyRate()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT));

        // Total Gross Pay
        summaryTable.addCell(createHighlightCell("TOTAL GROSS PAY:", boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalGrossPay()), boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.RIGHT));

        // Total Taxes Withheld
        summaryTable.addCell(createHighlightCell("TOTAL TAXES WITHHELD:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalTaxesWithheld()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.RIGHT));

        // Total Net Pay (highlighted)
        summaryTable.addCell(createHighlightCell("TOTAL NET PAY:", boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(200, 240, 200), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalNetPay()), boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(200, 240, 200), TextAlignment.RIGHT));

        document.add(summaryTable);
    }


    private void addEmployeeBreakdown(Document document, PayrollSummaryReportDTO data,
                                      PdfFont boldFont, PdfFont regularFont) {

        if (data.getEmployeeBreakdown() == null || data.getEmployeeBreakdown().isEmpty()) {
            return;
        }

        // Employee Breakdown Table
        Table employeeTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Header
        Cell breakdownHeader = new Cell(1, 6)
                .add(new Paragraph("EMPLOYEE BREAKDOWN").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(breakdownHeader);

        // Column headers
        employeeTable.addHeaderCell(createHeaderCell("Employee Name", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Regular Hrs", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("OT Hrs", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Rate", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Gross Pay", boldFont, 10));
        employeeTable.addHeaderCell(createHeaderCell("Net Pay", boldFont, 10));

        // Employee rows
        boolean isEvenRow = true;
        for (EmployeeSummaryDTO employee : data.getEmployeeBreakdown()) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255) : Color.WHITE;

            employeeTable.addCell(createEmployeeCell(employee.getEmployeeName(), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getRegularHours()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell(formatDecimal(employee.getOvertimeHours()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getHourlyRate()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getGrossPay()), regularFont, 9, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getNetPay()), regularFont, 9, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(employeeTable);
    }

    private void addReportFooter(Document document, PdfFont regularFont) {

        // Footer separator
        document.add(new Paragraph("\n"));

        // Disclaimer
        document.add(new Paragraph("This report provides a summary of payroll data for the specified period. " +
                "Please verify all information before processing payments or filing taxes.")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        // Generated by
        document.add(new Paragraph("Generated by Facecheck Payroll System - " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5));
    }


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
