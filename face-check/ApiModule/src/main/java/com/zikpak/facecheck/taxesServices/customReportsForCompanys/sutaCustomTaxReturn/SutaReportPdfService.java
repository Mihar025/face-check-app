package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
@Slf4j
public class SutaReportPdfService {

    private final AmazonS3Service amazonS3Service;
    private final MetricsForPdfServices metric;
    public byte[] generateSutaReportPdf(SutaReportDTO reportData) {
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

            // 1. HEADER
            addReportHeader(document, reportData, boldFont, regularFont);

            // 2. COMPANY INFO
            addCompanyInfo(document, reportData, boldFont, regularFont);

            // 3. SUTA SUMMARY
            addSutaSummary(document, reportData, boldFont, regularFont);

            // 4. EMPLOYEE DETAILS
            addEmployeeDetails(document, reportData, boldFont, regularFont);

            // 5. QUARTERLY BREAKDOWN (для годового)
            if ("Annual".equals(reportData.getReportType()) && reportData.getQuarterlyBreakdown() != null) {
                addQuarterlyBreakdown(document, reportData, boldFont, regularFont);
            }

            // 6. PAYMENT INFO & COMPLIANCE
            addPaymentInfo(document, reportData, boldFont, regularFont);

            // 7. FOOTER
            addReportFooter(document, regularFont);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            long ms = System.currentTimeMillis();
            uploadToS3(pdfBytes, reportData);
            long end = System.currentTimeMillis() - ms;

            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true,  end);
            metric.recordOperationTime(timer,"suta_report_success");

            return pdfBytes;

        } catch (Exception e) {
            metric.recordOperationTime(timer,"suta_report_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("suta_report_failed", e.getMessage(), e);
            log.error("Error generating SUTA report PDF", e);
            throw new RuntimeException("Error generating SUTA Report PDF", e);
        }
    }

    private void addReportHeader(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) throws Exception {
        // Powered by Facecheck
        document.add(new Paragraph("Powered by Facecheck")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(10));

        // Main title
        String title = data.getReportType().equals("Quarterly") ?
                String.format("NY SUTA TAX REPORT - Q%d %d", data.getQuarter(), data.getTaxYear()) :
                String.format("NY SUTA TAX REPORT - ANNUAL %d", data.getTaxYear());

        document.add(new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new com.itextpdf.kernel.color.DeviceRgb(0, 80, 140)) // NY blue
                .setMarginBottom(5));

        document.add(new Paragraph("New York State Unemployment Tax Report")
                .setFont(regularFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15));

        document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setFont(regularFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void addCompanyInfo(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell header = new Cell(1, 2)
                .add(new Paragraph("COMPANY INFORMATION").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(0, 80, 140))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        infoTable.addCell(header);

        // Company details
        infoTable.addCell(createInfoCell("Company Name:", boldFont));
        infoTable.addCell(createInfoCell(data.getCompanyName(), regularFont));

        infoTable.addCell(createInfoCell("EIN:", boldFont));
        infoTable.addCell(createInfoCell(data.getEmployerEIN(), regularFont));

        infoTable.addCell(createInfoCell("NY SUTA Account:", boldFont));
        infoTable.addCell(createInfoCell(data.getSutaAccountNumber(), regularFont));

        infoTable.addCell(createInfoCell("Address:", boldFont));
        infoTable.addCell(createInfoCell(String.format("%s, %s, %s %s",
                data.getCompanyAddress(), data.getCompanyCity(), data.getCompanyState(), data.getCompanyZipCode()), regularFont));

        infoTable.addCell(createInfoCell("Report Period:", boldFont));
        infoTable.addCell(createInfoCell(String.format("%s to %s",
                data.getPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                data.getPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))), regularFont));

        document.add(infoTable);
    }

    private void addSutaSummary(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) {
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell summaryHeader = new Cell(1, 2)
                .add(new Paragraph("NY SUTA TAX SUMMARY").setFont(boldFont).setFontSize(14))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0)) // NY orange
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        summaryTable.addCell(summaryHeader);

        // Key metrics
        summaryTable.addCell(createHighlightCell("TOTAL GROSS WAGES:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240)));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalGrossWages()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240)));

        summaryTable.addCell(createHighlightCell("SUTA WAGE BASE:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220)));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalSutaWageBase()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220)));

        summaryTable.addCell(createHighlightCell("COMPANY SUTA RATE:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        summaryTable.addCell(createHighlightCell(formatPercentage(data.getSutaRate()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));

        summaryTable.addCell(createHighlightCell("NY STANDARD RATE:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 220, 220)));
        summaryTable.addCell(createHighlightCell(formatPercentage(data.getStandardSutaRate()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 220, 220)));

        summaryTable.addCell(createHighlightCell("EXPERIENCE ADJUSTMENT:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200)));
        summaryTable.addCell(createHighlightCell("+" + formatPercentage(data.getExperienceRate()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200)));

        summaryTable.addCell(createHighlightCell("TOTAL SUTA TAX OWED:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250)));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalSutaTaxOwed()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250)));

        summaryTable.addCell(createHighlightCell("TOTAL EMPLOYEES:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255)));
        summaryTable.addCell(createHighlightCell(data.getTotalEmployees().toString(), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255)));

        summaryTable.addCell(createHighlightCell("EMPLOYEES SUBJECT TO SUTA:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(248, 255, 248)));
        summaryTable.addCell(createHighlightCell(data.getEmployeesSubjectToSuta().toString(), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(248, 255, 248)));

        document.add(summaryTable);
    }

    private void addEmployeeDetails(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) {
        if (data.getEmployeeDetails() == null || data.getEmployeeDetails().isEmpty()) {
            return;
        }

        // Show top 15 employees
        List<EmployeeSutaDTO> topEmployees = data.getEmployeeDetails().stream()
                .limit(15)
                .collect(Collectors.toList());

        Table employeeTable = new Table(UnitValue.createPercentArray(new float[]{2.5f, 1.5f, 1.5f, 1.5f, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell employeeHeader = new Cell(1, 5)
                .add(new Paragraph("EMPLOYEE SUTA DETAILS").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(employeeHeader);

        // Column headers
        employeeTable.addHeaderCell(createHeaderCell("Employee Name", boldFont));
        employeeTable.addHeaderCell(createHeaderCell("Gross Wages", boldFont));
        employeeTable.addHeaderCell(createHeaderCell("SUTA Wage Base", boldFont));
        employeeTable.addHeaderCell(createHeaderCell("SUTA Tax", boldFont));
        employeeTable.addHeaderCell(createHeaderCell("$13K Limit", boldFont));

        // Employee rows
        boolean isEvenRow = true;
        for (EmployeeSutaDTO employee : topEmployees) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220) : Color.WHITE;

            employeeTable.addCell(createEmployeeCell(employee.getEmployeeName(), regularFont, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getGrossWages()), regularFont, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getSutaWageBase()), regularFont, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getSutaTax()), regularFont, rowColor));

            // Highlight if limit exceeded
            com.itextpdf.kernel.color.Color limitColor = employee.getExceededLimit() ?
                    new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200) : rowColor;
            employeeTable.addCell(createEmployeeCell(employee.getExceededLimit() ? "Exceeded" : "Active",
                    employee.getExceededLimit() ? boldFont : regularFont, limitColor));

            isEvenRow = !isEvenRow;
        }

        // Summary row if more employees
        if (data.getEmployeeDetails().size() > 15) {
            Cell summaryCell = new Cell(1, 5)
                    .add(new Paragraph(String.format("... and %d more employees (Total: %d)",
                            data.getEmployeeDetails().size() - 15, data.getTotalEmployees()))
                            .setFont(regularFont).setFontSize(8).setItalic())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(240, 240, 240))
                    .setPadding(5);
            employeeTable.addCell(summaryCell);
        }

        document.add(employeeTable);
    }

    private void addQuarterlyBreakdown(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) {
        Table quarterlyTable = new Table(UnitValue.createPercentArray(new float[]{1, 1.5f, 1.5f, 1.5f, 1.5f, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell quarterlyHeader = new Cell(1, 6)
                .add(new Paragraph("QUARTERLY BREAKDOWN").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(0, 80, 140))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        quarterlyTable.addCell(quarterlyHeader);

        // Column headers
        quarterlyTable.addHeaderCell(createHeaderCell("Quarter", boldFont));
        quarterlyTable.addHeaderCell(createHeaderCell("Gross Wages", boldFont));
        quarterlyTable.addHeaderCell(createHeaderCell("SUTA Base", boldFont));
        quarterlyTable.addHeaderCell(createHeaderCell("Tax Owed", boldFont));
        quarterlyTable.addHeaderCell(createHeaderCell("Tax Paid", boldFont));
        quarterlyTable.addHeaderCell(createHeaderCell("Liable?", boldFont));

        // Quarterly data rows
        boolean isEvenRow = true;
        for (QuarterlySutaDTO quarter : data.getQuarterlyBreakdown()) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(245, 255, 245) : Color.WHITE;

            quarterlyTable.addCell(createEmployeeCell("Q" + quarter.getQuarter(), boldFont, rowColor));
            quarterlyTable.addCell(createEmployeeCell("$" + formatDecimal(quarter.getGrossWages()), regularFont, rowColor));
            quarterlyTable.addCell(createEmployeeCell("$" + formatDecimal(quarter.getSutaWageBase()), regularFont, rowColor));
            quarterlyTable.addCell(createEmployeeCell("$" + formatDecimal(quarter.getSutaTaxOwed()), regularFont, rowColor));
            quarterlyTable.addCell(createEmployeeCell("$" + formatDecimal(quarter.getSutaTaxPaid()), regularFont, rowColor));
            quarterlyTable.addCell(createEmployeeCell(quarter.getIsLiable() ? "Yes" : "No",
                    quarter.getIsLiable() ? boldFont : regularFont,
                    quarter.getIsLiable() ? new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240) : rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(quarterlyTable);
    }

    private void addPaymentInfo(Document document, SutaReportDTO data, PdfFont boldFont, PdfFont regularFont) {
        Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell paymentHeader = new Cell(1, 2)
                .add(new Paragraph("PAYMENT STATUS & COMPLIANCE").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(138, 43, 226))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        paymentTable.addCell(paymentHeader);

        // Payment status
        paymentTable.addCell(createHighlightCell("TOTAL SUTA TAX OWED:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220)));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalSutaTaxOwed()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220)));

        paymentTable.addCell(createHighlightCell("TOTAL SUTA TAX PAID:", boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240)));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalSutaTaxPaid()), boldFont,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240)));

        // Remaining liability with color coding
        com.itextpdf.kernel.color.Color liabilityColor;
        if (data.getRemainingSutaLiability().compareTo(BigDecimal.ZERO) <= 0) {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(200, 255, 200); // Green
        } else if (data.getRemainingSutaLiability().compareTo(new BigDecimal("100.00")) <= 0) {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(255, 255, 200); // Yellow
        } else {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(255, 220, 220); // Red
        }

        paymentTable.addCell(createHighlightCell("REMAINING LIABILITY:", boldFont, liabilityColor));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getRemainingSutaLiability()), boldFont, liabilityColor));

        // Payment needed
        com.itextpdf.kernel.color.Color paymentColor = data.getNeedsPayment() ?
                new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200) :
                new com.itextpdf.kernel.color.DeviceRgb(200, 255, 200);

        String paymentText = data.getNeedsPayment() ? "⚠️ PAYMENT REQUIRED" : "✅ NO PAYMENT NEEDED";

        paymentTable.addCell(createHighlightCell("PAYMENT STATUS:", boldFont, paymentColor));
        paymentTable.addCell(createHighlightCell(paymentText, boldFont, paymentColor));

        // Compliance status
        com.itextpdf.kernel.color.Color complianceColor = data.getComplianceStatus() ?
                new com.itextpdf.kernel.color.DeviceRgb(200, 255, 200) :
                new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200);

        String complianceText = data.getComplianceStatus() ? "✅ COMPLIANT" : "⚠️ NON-COMPLIANT";

        paymentTable.addCell(createHighlightCell("COMPLIANCE STATUS:", boldFont, complianceColor));
        paymentTable.addCell(createHighlightCell(complianceText, boldFont, complianceColor));

        if (data.getNextPaymentDue() != null) {
            paymentTable.addCell(createHighlightCell("NEXT PAYMENT DUE:", boldFont,
                    new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
            paymentTable.addCell(createHighlightCell(data.getNextPaymentDue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    boldFont, new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255)));
        }

        document.add(paymentTable);

        // Compliance Notes
        if (data.getNotes() != null && !data.getNotes().isEmpty()) {
            Table notesTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            Cell notesHeader = new Cell()
                    .add(new Paragraph("COMPLIANCE NOTES").setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0))
                    .setFontColor(Color.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            notesTable.addCell(notesHeader);

            for (String note : data.getNotes()) {
                notesTable.addCell(createEmployeeCell("• " + note, regularFont,
                        new com.itextpdf.kernel.color.DeviceRgb(255, 248, 240)));
            }

            document.add(notesTable);
        }
    }

    private void addReportFooter(Document document, PdfFont regularFont) {
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("IMPORTANT: This NY SUTA report is for informational purposes only. " +
                "Consult with a qualified tax professional before making payments or filing returns. " +
                "File quarterly returns using Form NYS-45 with NY Department of Labor.")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        document.add(new Paragraph("Generated by Facecheck SUTA System - " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5));
    }

    private void uploadToS3(byte[] pdfBytes, SutaReportDTO reportData) {
        String companyKeyPart = reportData.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName;
        if ("Quarterly".equals(reportData.getReportType())) {
            fileName = String.format("sutaReport_Q%d_%d_%d.pdf",
                    reportData.getQuarter(), reportData.getTaxYear(), reportData.getCompanyId());
        } else {
            fileName = String.format("sutaReport_Annual_%d_%d.pdf",
                    reportData.getTaxYear(), reportData.getCompanyId());
        }

        String key = String.format("%s/%d/sutaReports/%s",
                companyKeyPart,
                reportData.getCompanyId(),
                fileName);

        amazonS3Service.uploadPdfToS3(pdfBytes, key);
        log.info("SUTA report uploaded to S3: {}", key);
    }

    // Helper methods (same as FUTA)
    private Cell createInfoCell(String text, PdfFont font) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255))
                .setPadding(5);
    }

    private Cell createHighlightCell(String text, PdfFont font, com.itextpdf.kernel.color.Color backgroundColor) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(backgroundColor)
                .setPadding(6);
    }

    private Cell createHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(230, 230, 230))
                .setPadding(5);
    }

    private Cell createEmployeeCell(String text, PdfFont font, com.itextpdf.kernel.color.Color backgroundColor) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(8))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(backgroundColor)
                .setPadding(4);
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }

    private String formatPercentage(BigDecimal value) {
        if (value == null) return "0.00%";
        return String.format("%.2f%%", value.multiply(new BigDecimal("100")));
    }
}