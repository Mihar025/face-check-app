package com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class YearToDateCustomReportPdf {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private final MetricsForPdfServices metric;


    static {
        NUMBER_FORMAT.setMinimumFractionDigits(2);
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }

    public byte[] generatePdf(YearToDateDTO dto) {
        final String FORM = "YearToDateCustomReportPDF";
        metric.recordRequest(FORM);
        Timer.Sample timer = metric.startTimer();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.setMargins(25, 25, 35, 25);

            PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(FontConstants.HELVETICA);

            // 0. "Powered by Facecheck" сверху
            doc.add(new Paragraph("Powered by Facecheck")
                    .setFont(regular)
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10));

            // 1. Header с логотипом
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            // Логотип
            try {
                InputStream logoStream = getClass().getResourceAsStream("/assets/logo.jpg");
                if (logoStream != null) {
                    ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());
                    Cell logoCell = new Cell()
                            .add(new Image(logoData).scaleToFit(80, 80))
                            .setBorder(null);
                    headerTable.addCell(logoCell);
                } else {
                    headerTable.addCell(new Cell().setBorder(null));
                }
            } catch (IOException e) {
                log.warn("Could not load logo: {}", e.getMessage());
                headerTable.addCell(new Cell().setBorder(null));
            }

            // Информация о компании и периоде
            Cell infoCell = new Cell().setBorder(null);
            infoCell.add(new Paragraph("YEAR-TO-DATE PAYROLL SUMMARY REPORT")
                    .setFont(bold)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));
            infoCell.add(new Paragraph(dto.getCompanyName())
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            infoCell.add(new Paragraph(String.format("Period: %s to %s",
                    dto.getPeriodStart().format(DF),
                    dto.getPeriodEnd().format(DF)))
                    .setFont(regular)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            headerTable.addCell(infoCell);

            doc.add(headerTable);

            // 2. Summary Statistics Box
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            summaryTable.addCell(createSummaryCell("Total Gross Pay",
                    CURRENCY_FORMAT.format(dto.getTotalGross()), bold, regular, new DeviceRgb(240, 248, 255)));
            summaryTable.addCell(createSummaryCell("Total Deductions",
                    CURRENCY_FORMAT.format(dto.getTotalDeductions()), bold, regular, new DeviceRgb(255, 240, 240)));
            summaryTable.addCell(createSummaryCell("Total Net Pay",
                    CURRENCY_FORMAT.format(dto.getTotalNet()), bold, regular, new DeviceRgb(240, 255, 240)));
            summaryTable.addCell(createSummaryCell("Total Hours",
                    NUMBER_FORMAT.format(dto.getTotalRegularHours() + dto.getTotalOvertimeHours()),
                    bold, regular, new DeviceRgb(255, 255, 240)));

            doc.add(summaryTable);

            // 3. PAYROLL BREAKDOWN
            doc.add(new Paragraph("PAYROLL BREAKDOWN")
                    .setFont(bold)
                    .setFontSize(12)
                    .setMarginBottom(10)
                    .setBackgroundColor(new DeviceRgb(70, 130, 180))
                    .setFontColor(Color.WHITE)
                    .setPadding(5));

            // Hours & Wages Table
            Table wagesTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            // Headers
            wagesTable.addHeaderCell(createHeaderCell("CATEGORY", bold));
            wagesTable.addHeaderCell(createHeaderCell("HOURS", bold));
            wagesTable.addHeaderCell(createHeaderCell("AMOUNT", bold));

            // Data rows
            wagesTable.addCell(createDataCell("Regular Time", regular, false));
            wagesTable.addCell(createDataCell(NUMBER_FORMAT.format(dto.getTotalRegularHours()), regular, false));
            wagesTable.addCell(createDataCell(CURRENCY_FORMAT.format(dto.getTotalRegularPay()), regular, false));

            wagesTable.addCell(createDataCell("Overtime", regular, true));
            wagesTable.addCell(createDataCell(NUMBER_FORMAT.format(dto.getTotalOvertimeHours()), regular, true));
            wagesTable.addCell(createDataCell(CURRENCY_FORMAT.format(dto.getTotalOvertimePay()), regular, true));

            // Total Row
            wagesTable.addCell(createTotalCell("TOTAL GROSS PAY", bold));
            wagesTable.addCell(createTotalCell(NUMBER_FORMAT.format(dto.getTotalRegularHours() + dto.getTotalOvertimeHours()), bold));
            wagesTable.addCell(createTotalCell(CURRENCY_FORMAT.format(dto.getTotalGross()), bold));

            doc.add(wagesTable);

            // 4. TAX WITHHOLDINGS
            doc.add(new Paragraph("TAX WITHHOLDINGS & DEDUCTIONS")
                    .setFont(bold)
                    .setFontSize(12)
                    .setMarginBottom(10)
                    .setBackgroundColor(new DeviceRgb(70, 130, 180))
                    .setFontColor(Color.WHITE)
                    .setPadding(5));

            Table taxTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            // Federal Taxes Column
            Cell federalCell = new Cell().setBorder(null).setPadding(5);
            federalCell.add(new Paragraph("FEDERAL TAXES").setFont(bold).setFontSize(10).setUnderline());
            federalCell.add(createTaxLine("Federal Income Tax:", dto.getTotalFederalWithholding(), regular));
            federalCell.add(createTaxLine("Social Security:", dto.getTotalSocialSecurity(), regular));
            federalCell.add(createTaxLine("Medicare:", dto.getTotalMedicare(), regular));
            federalCell.add(createTaxLine("FUTA:", dto.getTotalFutaWithholding(), regular));

            // State & Local Taxes Column
            Cell stateCell = new Cell().setBorder(null).setPadding(5);
            stateCell.add(new Paragraph("STATE & LOCAL TAXES").setFont(bold).setFontSize(10).setUnderline());
            stateCell.add(createTaxLine("State Income Tax:", dto.getTotalStateWithHolding(), regular));
            stateCell.add(createTaxLine("Local Tax:", dto.getTotalLocalWithholding(), regular));
            stateCell.add(createTaxLine("SUTA:", dto.getTotalSutaWithholding(), regular));
            stateCell.add(createTaxLine("Disability Insurance:", dto.getTotalDisabilityWithholding(), regular));
            stateCell.add(createTaxLine("Paid Family Leave:", dto.getTotalPaidFamilyLeave(), regular));

            taxTable.addCell(federalCell);
            taxTable.addCell(stateCell);
            doc.add(taxTable);

            // 5. BENEFITS & OTHER DEDUCTIONS
            doc.add(new Paragraph("BENEFITS & OTHER DEDUCTIONS")
                    .setFont(bold)
                    .setFontSize(12)
                    .setMarginBottom(10)
                    .setBackgroundColor(new DeviceRgb(70, 130, 180))
                    .setFontColor(Color.WHITE)
                    .setPadding(5));

            Table benefitsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            benefitsTable.addCell(createBenefitCell("401(k) Contributions", regular));
            benefitsTable.addCell(createBenefitCell(CURRENCY_FORMAT.format(dto.getTotalRetirement401kContribution()), regular));

            benefitsTable.addCell(createBenefitCell("Health Insurance", regular));
            benefitsTable.addCell(createBenefitCell(CURRENCY_FORMAT.format(dto.getTotalHealthInsuranceCost()), regular));

            doc.add(benefitsTable);

            // 6. FINAL SUMMARY
            Table finalSummaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            finalSummaryTable.addCell(createFinalSummaryCell("Total Gross Wages", bold, new DeviceRgb(240, 240, 240)));
            finalSummaryTable.addCell(createFinalSummaryCell(CURRENCY_FORMAT.format(dto.getTotalGross()), bold, new DeviceRgb(240, 240, 240)));

            finalSummaryTable.addCell(createFinalSummaryCell("Total Deductions", bold, new DeviceRgb(255, 240, 240)));
            finalSummaryTable.addCell(createFinalSummaryCell(CURRENCY_FORMAT.format(dto.getTotalDeductions()), bold, new DeviceRgb(255, 240, 240)));

            finalSummaryTable.addCell(createFinalSummaryCell("NET PAY", bold, new DeviceRgb(200, 255, 200)));
            finalSummaryTable.addCell(createFinalSummaryCell(CURRENCY_FORMAT.format(dto.getTotalNet()), bold, new DeviceRgb(200, 255, 200)));

            doc.add(finalSummaryTable);

            // 7. Important Notes
            doc.add(new Paragraph("IMPORTANT NOTES")
                    .setFont(bold)
                    .setFontSize(10)
                    .setMarginTop(20));

            doc.add(new Paragraph("• This report summarizes all payroll activity for the specified period")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• All amounts include both employee and employer contributions where applicable")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• Please retain this document for tax and accounting purposes")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• For detailed employee-by-employee breakdown, please request a detailed report")
                    .setFont(regular)
                    .setFontSize(8));

            // 8. Footer
            doc.add(new Paragraph("\nThis document is an official payroll record.")
                    .setFont(regular)
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));

            doc.add(new Paragraph("Generated by Face-Check Corporation on " + LocalDate.now().toString())
                    .setFont(regular)
                    .setFontSize(7)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Questions? Contact us at support@facecheck.com or 1-800-FACE-CHK")
                    .setFont(regular)
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();



            metric.recordGenerated(FORM, true);
            metric.recordOperationTime(timer,"yearToDateCustomReport_success");



            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generating Year-to-Date report PDF", e);
            throw new RuntimeException("Cannot generate Year-to-Date report PDF", e);
        }
    }


    // Helper methods
    private Cell createSummaryCell(String label, String value, PdfFont boldFont, PdfFont regularFont, DeviceRgb color) {
        Cell cell = new Cell()
                .setBackgroundColor(color)
                .setPadding(10)
                .setBorder(null);
        cell.add(new Paragraph(label).setFont(boldFont).setFontSize(9));
        cell.add(new Paragraph(value).setFont(boldFont).setFontSize(12));
        return cell;
    }

    private Cell createHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBackgroundColor(new DeviceRgb(200, 200, 200))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private Cell createDataCell(String text, PdfFont font, boolean isAlternate) {
        DeviceRgb bgColor = isAlternate ? new DeviceRgb(245, 245, 245) : new DeviceRgb(255, 255, 255);
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setBackgroundColor(bgColor)
                .setPadding(4);
    }

    private Cell createTotalCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBackgroundColor(new DeviceRgb(220, 220, 220))
                .setPadding(5);
    }

    private Paragraph createTaxLine(String label, BigDecimal amount, PdfFont font) {
        String value = amount != null ? CURRENCY_FORMAT.format(amount) : "$0.00";
        return new Paragraph(label + " " + value)
                .setFont(font)
                .setFontSize(9)
                .setMarginBottom(3);
    }

    private Cell createBenefitCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setBackgroundColor(new DeviceRgb(250, 250, 250))
                .setPadding(5);
    }

    private Cell createFinalSummaryCell(String text, PdfFont font, DeviceRgb color) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setBackgroundColor(color)
                .setPadding(8)
                .setBorder(null);
    }
}