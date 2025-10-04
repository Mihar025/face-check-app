package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;

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
import java.util.List;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
@RequiredArgsConstructor
public class TaxSummaryPdfService {

    private final AmazonS3Service amazonS3Service;
    private final MetricsForPdfServices metric;

    public byte[] generateTaxSummaryReport(TaxSummaryReportDTO reportData) throws Exception {
        final String FORM = "TaxSummaryReport";
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

            // 2. COMPANY & PERIOD INFO
            addCompanyAndPeriodInfo(document, reportData, boldFont, regularFont);

            // 3. TAX SUMMARY TOTALS (главная секция)
            addTaxSummaryTotals(document, reportData, boldFont, regularFont);

            // 4. TAX BREAKDOWN BY TYPE
            addTaxBreakdown(document, reportData, boldFont, regularFont);

            // 5. EMPLOYEE TAX SUMMARY
            addEmployeeTaxSummary(document, reportData, boldFont, regularFont);

            // 6. PAYMENT STATUS & COMPLIANCE
            addPaymentStatusAndCompliance(document, reportData, boldFont, regularFont);

            // 7. REQUIRED FORMS
            addRequiredForms(document, reportData, boldFont, regularFont);

            // 8. FOOTER
            addReportFooter(document, regularFont);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

// Генерируем правильный S3 ключ
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "_");

// Определяем период (квартал или годовой)
            String periodPart;
            String fileName;

            if (reportData.getQuarter() != null) {
                // Квартальный отчет
                periodPart = String.format("Q%d", reportData.getQuarter());
                fileName = String.format("tax_summary_%d_Q%d_%s.pdf",
                        reportData.getTaxYear(),
                        reportData.getQuarter(),
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                );
            } else {
                // Годовой отчет
                periodPart = "annual";
                fileName = String.format("tax_summary_%d_annual_%s.pdf",
                        reportData.getTaxYear(),
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                );
            }

            String key = String.format("%s_%d/reports/taxes/%d/%s/%s",
                    companyKeyPart,                    // "facecheck_corp"
                    reportData.getCompanyId(),          // "_123"
                    reportData.getTaxYear(),            // "/2024"
                    periodPart,                         // "/Q1" или "/annual"
                    fileName                            // "tax_summary_2024_Q1_20240415.pdf"
            );

// Результат:
// Квартальный: facecheck_corp_123/reports/taxes/2024/Q1/tax_summary_2024_Q1_20240415.pdf
// Годовой: facecheck_corp_123/reports/taxes/2024/annual/tax_summary_2024_annual_20250131.pdf

            long ms = System.currentTimeMillis();
            amazonS3Service.uploadPdfToS3(pdfBytes, key);
            long end = System.currentTimeMillis() - ms;

            metric.recordGenerated(FORM, true);
            metric.recordS3UploadTime(FORM, true, end);
            metric.recordOperationTime(timer, "tax_summary_success");
            return pdfBytes;

        } catch (Exception e) {
            metric.recordOperationTime(timer, "tax_summary_failed");
            metric.recordGenerated(FORM, false);
            metric.recordError("tax_summary_failed", e.getMessage(), e);
            throw e;
        }
    }


    private void addReportHeader(Document document, TaxSummaryReportDTO data,
                                 PdfFont boldFont, PdfFont regularFont) throws Exception {

        // Powered by Facecheck
        document.add(new Paragraph("Powered by Facecheck")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(10));

        // Header table: Logo + Report Title
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Facecheck Logo
        InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
        ImageData facecheckLogoData = ImageDataFactory.create(StreamUtil.inputStreamToArray(logoStream));
        Cell logoCell = new Cell()
                .add(new com.itextpdf.layout.element.Image(facecheckLogoData).scaleToFit(80, 80))
                .setBorder(null);
        headerTable.addCell(logoCell);

        // Report Title
        Cell titleCell = new Cell().setBorder(null);
        titleCell.add(new Paragraph("TAX SUMMARY REPORT")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));
        titleCell.add(new Paragraph("Tax Compliance & Liability Summary")
                .setFont(regularFont)
                .setFontSize(12)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));
        titleCell.add(new Paragraph("Generated: " + LocalDate.now())
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));
        headerTable.addCell(titleCell);

        document.add(headerTable);
    }

    private void addCompanyAndPeriodInfo(Document document, TaxSummaryReportDTO data,
                                         PdfFont boldFont, PdfFont regularFont) {

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Company Info Header
        Cell companyHeader = new Cell(1, 2)
                .add(new Paragraph("COMPANY & PERIOD INFORMATION").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        infoTable.addCell(companyHeader);

        // Company Info
        infoTable.addCell(createInfoCell("Company Name:", boldFont, 10));
        infoTable.addCell(createInfoCell(data.getCompanyName(), regularFont, 10));

        infoTable.addCell(createInfoCell("Employer EIN:", boldFont, 10));
        infoTable.addCell(createInfoCell(data.getEmployerEIN(), regularFont, 10));

        infoTable.addCell(createInfoCell("Address:", boldFont, 10));
        infoTable.addCell(createInfoCell(
                data.getCompanyAddress() + ", " + data.getCompanyCity() + ", " +
                        data.getCompanyState() + " " + data.getCompanyZipCode(), regularFont, 10));

        infoTable.addCell(createInfoCell("Report Type:", boldFont, 10));
        infoTable.addCell(createInfoCell(data.getReportType(), regularFont, 10));

        infoTable.addCell(createInfoCell("Tax Year:", boldFont, 10));
        infoTable.addCell(createInfoCell(data.getTaxYear().toString(), regularFont, 10));

        if (data.getQuarter() != null) {
            infoTable.addCell(createInfoCell("Quarter:", boldFont, 10));
            infoTable.addCell(createInfoCell("Q" + data.getQuarter(), regularFont, 10));
        }

        infoTable.addCell(createInfoCell("Period:", boldFont, 10));
        infoTable.addCell(createInfoCell(
                data.getPeriodStart() + " to " + data.getPeriodEnd(), regularFont, 10));

        document.add(infoTable);
    }

    private void addTaxSummaryTotals(Document document, TaxSummaryReportDTO data,
                                     PdfFont boldFont, PdfFont regularFont) {

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell summaryHeader = new Cell(1, 2)
                .add(new Paragraph("TAX SUMMARY TOTALS").setFont(boldFont).setFontSize(14))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(220, 20, 60))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        summaryTable.addCell(summaryHeader);

        // Key metrics with highlighting
        summaryTable.addCell(createHighlightCell("TOTAL GROSS WAGES:", boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalGrossWages()), boldFont, 12,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT));

        summaryTable.addCell(createHighlightCell("TOTAL EMPLOYEE TAXES:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalEmployeeTaxes()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 240, 240), TextAlignment.RIGHT));

        summaryTable.addCell(createHighlightCell("TOTAL EMPLOYER TAXES:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalEmployerTaxes()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.RIGHT));

        summaryTable.addCell(createHighlightCell("TOTAL TAX LIABILITY:", boldFont, 13,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalTaxLiability()), boldFont, 13,
                new com.itextpdf.kernel.color.DeviceRgb(220, 220, 250), TextAlignment.RIGHT));

        summaryTable.addCell(createHighlightCell("TOTAL EMPLOYEES:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255), TextAlignment.LEFT));
        summaryTable.addCell(createHighlightCell(data.getTotalEmployees().toString(), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 248, 255), TextAlignment.RIGHT));

        document.add(summaryTable);
    }

    private void addTaxBreakdown(Document document, TaxSummaryReportDTO data,
                                 PdfFont boldFont, PdfFont regularFont) {

        if (data.getTaxBreakdown() == null || data.getTaxBreakdown().isEmpty()) {
            return;
        }

        Table breakdownTable = new Table(UnitValue.createPercentArray(new float[]{2.5f, 1, 1, 1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell breakdownHeader = new Cell(1, 5)
                .add(new Paragraph("TAX BREAKDOWN BY TYPE").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(34, 139, 34))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        breakdownTable.addCell(breakdownHeader);

        // Column headers
        breakdownTable.addHeaderCell(createHeaderCell("Tax Type", boldFont, 10));
        breakdownTable.addHeaderCell(createHeaderCell("Employer", boldFont, 10));
        breakdownTable.addHeaderCell(createHeaderCell("Employee", boldFont, 10));
        breakdownTable.addHeaderCell(createHeaderCell("Total", boldFont, 10));
        breakdownTable.addHeaderCell(createHeaderCell("Description", boldFont, 10));

        // Tax breakdown rows
        boolean isEvenRow = true;
        for (TaxBreakdownDTO breakdown : data.getTaxBreakdown()) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240) : Color.WHITE;

            breakdownTable.addCell(createEmployeeCell(breakdown.getTaxType(), regularFont, 9, rowColor));
            breakdownTable.addCell(createEmployeeCell("$" + formatDecimal(breakdown.getEmployerPortion()), regularFont, 9, rowColor));
            breakdownTable.addCell(createEmployeeCell("$" + formatDecimal(breakdown.getEmployeePortion()), regularFont, 9, rowColor));
            breakdownTable.addCell(createEmployeeCell("$" + formatDecimal(breakdown.getTotalAmount()), boldFont, 9, rowColor));
            breakdownTable.addCell(createEmployeeCell(breakdown.getDescription(), regularFont, 8, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(breakdownTable);
    }

    private void addEmployeeTaxSummary(Document document, TaxSummaryReportDTO data,
                                       PdfFont boldFont, PdfFont regularFont) {

        if (data.getEmployeeTaxSummary() == null || data.getEmployeeTaxSummary().isEmpty()) {
            return;
        }

        // Show only top 10 employees to keep report concise
        List<EmployeeTaxSummaryDTO> topEmployees = data.getEmployeeTaxSummary().stream()
                .sorted((a, b) -> b.getGrossWages().compareTo(a.getGrossWages()))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        Table employeeTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell employeeHeader = new Cell(1, 6)
                .add(new Paragraph("EMPLOYEE TAX SUMMARY (Top 10)").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(255, 140, 0))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        employeeTable.addCell(employeeHeader);

        // Column headers
        employeeTable.addHeaderCell(createHeaderCell("Employee", boldFont, 9));
        employeeTable.addHeaderCell(createHeaderCell("Gross Wages", boldFont, 9));
        employeeTable.addHeaderCell(createHeaderCell("Fed Tax", boldFont, 9));
        employeeTable.addHeaderCell(createHeaderCell("SS Tax", boldFont, 9));
        employeeTable.addHeaderCell(createHeaderCell("Medicare", boldFont, 9));
        employeeTable.addHeaderCell(createHeaderCell("Total Tax", boldFont, 9));

        // Employee rows
        boolean isEvenRow = true;
        for (EmployeeTaxSummaryDTO employee : topEmployees) {
            com.itextpdf.kernel.color.Color rowColor = isEvenRow ?
                    new com.itextpdf.kernel.color.DeviceRgb(255, 248, 235) : Color.WHITE;

            employeeTable.addCell(createEmployeeCell(employee.getEmployeeName(), regularFont, 8, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getGrossWages()), regularFont, 8, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getFederalWithholding()), regularFont, 8, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getSocialSecurityWithholding()), regularFont, 8, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getMedicareWithholding()), regularFont, 8, rowColor));
            employeeTable.addCell(createEmployeeCell("$" + formatDecimal(employee.getTotalWithholdings()), boldFont, 8, rowColor));

            isEvenRow = !isEvenRow;
        }

        document.add(employeeTable);
    }

    private void addPaymentStatusAndCompliance(Document document, TaxSummaryReportDTO data,
                                               PdfFont boldFont, PdfFont regularFont) {

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

        // Total Tax Liability
        paymentTable.addCell(createHighlightCell("TOTAL TAX LIABILITY:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.LEFT));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalTaxLiability()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(255, 248, 220), TextAlignment.RIGHT));

        // Total Taxes Paid
        paymentTable.addCell(createHighlightCell("TOTAL TAXES PAID:", boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.LEFT));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getTotalTaxesPaid()), boldFont, 11,
                new com.itextpdf.kernel.color.DeviceRgb(240, 255, 240), TextAlignment.RIGHT));

        // Remaining Liability with improved color logic
        com.itextpdf.kernel.color.Color liabilityColor;
        if (data.getRemainingTaxLiability().compareTo(BigDecimal.ZERO) <= 0) {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(200, 255, 200); // Green if no liability
        } else if (data.getRemainingTaxLiability().compareTo(BigDecimal.valueOf(100)) <= 0) {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(255, 255, 200); // Yellow if small liability
        } else {
            liabilityColor = new com.itextpdf.kernel.color.DeviceRgb(255, 220, 220); // Red if significant liability
        }

        paymentTable.addCell(createHighlightCell("REMAINING LIABILITY:", boldFont, 11,
                liabilityColor, TextAlignment.LEFT));
        paymentTable.addCell(createHighlightCell("$" + formatDecimal(data.getRemainingTaxLiability()), boldFont, 11,
                liabilityColor, TextAlignment.RIGHT));

        // Compliance Status with better text
        com.itextpdf.kernel.color.Color complianceColor = data.getComplianceStatus() ?
                new com.itextpdf.kernel.color.DeviceRgb(200, 255, 200) : // Green if compliant
                new com.itextpdf.kernel.color.DeviceRgb(255, 200, 200);   // Red if not compliant

        String complianceText;
        if (data.getComplianceStatus()) {
            complianceText = "✓ COMPLIANT";
        } else {
            complianceText = "⚠ NON-COMPLIANT (Payment Required)";
        }

        paymentTable.addCell(createHighlightCell("COMPLIANCE STATUS:", boldFont, 11,
                complianceColor, TextAlignment.LEFT));
        paymentTable.addCell(createHighlightCell(complianceText, boldFont, 10,
                complianceColor, TextAlignment.RIGHT));

        document.add(paymentTable);
    }

    private void addRequiredForms(Document document, TaxSummaryReportDTO data,
                                  PdfFont boldFont, PdfFont regularFont) {

        if (data.getFormsRequired() == null || data.getFormsRequired().isEmpty()) {
            return;
        }

        Table formsTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Header
        Cell formsHeader = new Cell()
                .add(new Paragraph("REQUIRED TAX FORMS").setFont(boldFont).setFontSize(12))
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(70, 130, 180))
                .setFontColor(Color.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        formsTable.addCell(formsHeader);

        // Forms list
        for (String form : data.getFormsRequired()) {
            formsTable.addCell(createEmployeeCell("• " + form, regularFont, 10,
                    new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255)));
        }

        document.add(formsTable);
    }

    private void addReportFooter(Document document, PdfFont regularFont) {
        document.add(new Paragraph("\n"));

        // Important disclaimer
        document.add(new Paragraph("IMPORTANT: This report is for informational purposes only. " +
                "Please consult with a qualified tax professional before making tax payments or filing returns. " +
                "Verify all calculations before submitting to tax authorities.")
                .setFont(regularFont)
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10)
                .setBold());

        // Generated by with proper date formatting
        document.add(new Paragraph("Generated by Facecheck Tax Compliance System - " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setFont(regularFont)
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5));
    }

    // Helper methods (same as other reports)
    private Cell createInfoCell(String text, PdfFont font, float fontSize) {
        if (text == null) text = "";
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(new com.itextpdf.kernel.color.DeviceRgb(248, 248, 255))
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
