package com.zikpak.facecheck.helperServices;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.zikpak.facecheck.requestsResponses.finance.CompanyYearlySummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;
import static com.itextpdf.kernel.color.Color.BLACK;

@Service
@RequiredArgsConstructor
public class W3PdfGeneratorService {
    public byte[] generateW3Pdf(CompanyYearlySummaryDTO summary, String companyName, String companyEin, String companyAddress) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // Логотип
            String logoPath = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/logo.jpg";
            ImageData logoData = ImageDataFactory.create(logoPath);
            Image logo = new Image(logoData);
            logo.scaleToFit(60, 60);
            document.add(logo);

            // Красивый заголовок Face-Check
            document.add(new Paragraph("Face-Check Corporation")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));

            // Основной заголовок формы
            document.add(new Paragraph("Form W-3")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Transmittal of Wage and Tax Statements")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("For Year: " + summary.getYear())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(" "));

            // Информация о компании
            Table companyTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            companyTable.addCell(createLabelCell("Employer Name:", boldFont));
            companyTable.addCell(createValueCell(companyName, regularFont));
            companyTable.addCell(createLabelCell("Employer EIN:", boldFont));
            companyTable.addCell(createValueCell(companyEin, regularFont));
            companyTable.addCell(createLabelCell("Employer Address:", boldFont));
            companyTable.addCell(createValueCell(companyAddress, regularFont));
            companyTable.addCell(createLabelCell("Total Number of W-2 Forms Issued:", boldFont));
            companyTable.addCell(createValueCell(String.valueOf(summary.getTotalEmployees()), regularFont));

            document.add(companyTable);

            // Сводка по налогам
            document.add(new Paragraph("Summary of Reported Wages and Taxes")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));

            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();

            totalsTable.addCell(createLabelCell("Total wages, tips, and other compensation:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalWages(), regularFont));
            totalsTable.addCell(createLabelCell("Total federal income tax withheld:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalFederalIncomeTax(), regularFont));
            totalsTable.addCell(createLabelCell("Total social security wages:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalSocialSecurityWages(), regularFont));
            totalsTable.addCell(createLabelCell("Total social security tax withheld:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalSocialSecurityTax(), regularFont));
            totalsTable.addCell(createLabelCell("Total Medicare wages and tips:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalMedicareWages(), regularFont));
            totalsTable.addCell(createLabelCell("Total Medicare tax withheld:", boldFont));
            totalsTable.addCell(createValueCell("$" + summary.getTotalMedicareTax(), regularFont));

            document.add(totalsTable);

            document.add(new Paragraph("Generated by Face-Check Payroll System")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setFontColor(com.itextpdf.kernel.color.Color.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));


            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации W-3 PDF", e);
        }
    }

    private Cell createLabelCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10))
                .setBorder(new SolidBorder(BLACK, 0.5f))
                .setPadding(5);
    }

    private Cell createValueCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10))
                .setBorder(new SolidBorder(BLACK, 0.5f))
                .setPadding(5);
    }
}
