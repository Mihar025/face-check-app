package com.zikpak.facecheck.taxesServices.services.wcRiskService;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportDto;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcReportPdfGeneratorService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    static {
        NUMBER_FORMAT.setMinimumFractionDigits(2);
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }

    public byte[] generateWcReportPdf(WcReportDto report) {
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
            } catch (Exception e) {
                log.warn("Could not load logo: {}", e.getMessage());
                headerTable.addCell(new Cell().setBorder(null));
            }

            // Информация о компании и полисе
            Cell infoCell = new Cell().setBorder(null);
            infoCell.add(new Paragraph("WORKERS' COMPENSATION INSURANCE REPORT")
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            infoCell.add(new Paragraph(report.getCompanyName())
                    .setFont(bold)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            infoCell.add(new Paragraph(String.format("Period: %s to %s",
                    report.getPeriodStart().format(DF),
                    report.getPeriodEnd().format(DF)))
                    .setFont(regular)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            headerTable.addCell(infoCell);

            doc.add(headerTable);

            // 2. Policy Information Box
            Table policyTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            policyTable.addCell(createInfoCell("Policy Number", report.getPolicyNumber(), bold, regular));
            policyTable.addCell(createInfoCell("Company EMR", NUMBER_FORMAT.format(report.getEmr()), bold, regular));
            policyTable.addCell(createInfoCell("Report Date", LocalDate.now().format(DF), bold, regular));

            doc.add(policyTable);

            // 3. Таблица с классами риска
            float[] colWidths = {1, 3.5f, 2, 1.2f, 0.8f, 2};
            Table table = new Table(UnitValue.createPercentArray(colWidths))
                    .useAllAvailableWidth();

            // Заголовки таблицы
            String[] headers = {"Code", "Description", "Total Wages", "Rate (%)", "EMR", "Premium"};
            for (String h : headers) {
                table.addHeaderCell(
                        new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                                .setBackgroundColor(new DeviceRgb(70, 130, 180))
                                .setFontColor(com.itextpdf.kernel.color.Color.WHITE)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setPadding(5)
                );
            }

            // Данные таблицы
            boolean isEvenRow = false;
            for (WcReportLine line : report.getLines()) {
                DeviceRgb rowColor = isEvenRow ? new DeviceRgb(245, 245, 245) : new DeviceRgb(255, 255, 255);

                table.addCell(createTableCell(line.getCode(), regular, 9, rowColor, TextAlignment.CENTER));
                table.addCell(createTableCell(line.getDescription(), regular, 9, rowColor, TextAlignment.LEFT));
                table.addCell(createTableCell(CURRENCY_FORMAT.format(line.getTotalWages()), regular, 9, rowColor, TextAlignment.RIGHT));
                table.addCell(createTableCell(String.format("%.2f%%", line.getRate()), regular, 9, rowColor, TextAlignment.RIGHT));
                table.addCell(createTableCell(NUMBER_FORMAT.format(line.getEmr()), regular, 9, rowColor, TextAlignment.CENTER));
                table.addCell(createTableCell(CURRENCY_FORMAT.format(line.getTotalContribution()), bold, 9, rowColor, TextAlignment.RIGHT));

                isEvenRow = !isEvenRow;
            }

            doc.add(table);

            // 4. Итоговая сумма
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth()
                    .setMarginTop(10);

            Cell totalLabelCell = new Cell()
                    .add(new Paragraph("TOTAL PREMIUM DUE:").setFont(bold).setFontSize(12))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(new DeviceRgb(220, 220, 220))
                    .setPadding(8)
                    .setBorder(null);

            Cell totalValueCell = new Cell()
                    .add(new Paragraph(CURRENCY_FORMAT.format(report.getGrandTotal())).setFont(bold).setFontSize(12))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(new DeviceRgb(200, 240, 200))
                    .setPadding(8)
                    .setBorder(null);

            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);
            doc.add(totalTable);

            // 5. Важная информация
            doc.add(new Paragraph("\nIMPORTANT INFORMATION")
                    .setFont(bold)
                    .setFontSize(10)
                    .setMarginTop(20));

            doc.add(new Paragraph("• This report is based on actual payroll data for the period specified above.")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• Premium calculations include your company's Experience Modification Rate (EMR).")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• All rates are subject to state regulations and may vary by jurisdiction.")
                    .setFont(regular)
                    .setFontSize(8));
            doc.add(new Paragraph("• Please retain this document for your records and tax purposes.")
                    .setFont(regular)
                    .setFontSize(8));

            // 6. Футер
            doc.add(new Paragraph("\nThis document is an official workers' compensation insurance record.")
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
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating WC report PDF", e);
            throw new RuntimeException("Cannot generate WC report PDF", e);
        }
    }

    private Cell createInfoCell(String label, String value, PdfFont boldFont, PdfFont regularFont) {
        Cell cell = new Cell().setBorder(null);
        cell.add(new Paragraph(label).setFont(boldFont).setFontSize(9));
        cell.add(new Paragraph(value != null ? value : "N/A").setFont(regularFont).setFontSize(10));
        return cell;
    }

    private Cell createTableCell(String text, PdfFont font, float fontSize, DeviceRgb bgColor, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(fontSize))
                .setBackgroundColor(bgColor)
                .setTextAlignment(alignment)
                .setPadding(4);
    }
}