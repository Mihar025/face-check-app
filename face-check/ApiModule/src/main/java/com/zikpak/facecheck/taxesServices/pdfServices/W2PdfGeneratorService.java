package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.border.Border;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;
import static com.itextpdf.kernel.color.Color.BLACK;

@Slf4j
@Service
@RequiredArgsConstructor
public class W2PdfGeneratorService {

    public byte[] generateW2Pdf(User worker,
                                    WorkerYearlySummaryDto payrollSummary,
                                    String ssn,
                                    String employerName,
                                    String employerEin,
                                    String employerAddress,
                                    int year) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // === Earnings Summary Page (вставляется как первая страница) ===
            String destination = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/logo.jpg";
            ImageData data = ImageDataFactory.create(destination);
            Image imageData = new Image(data);
            imageData.scaleToFit(50, 50);
            document.add(imageData);

            document.add(new Paragraph("Face-Check Corporation")
                    .setFont(boldFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.LEFT));

            document.add(new Paragraph(year + " Earnings Summary \n and W-2 Forms")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("Form W-2 Wage and Tax Statement")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new Paragraph("1. Your W-4 Profile: ")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));

            Table mainInfo = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();

            mainInfo.addCell(createLabelCell("Employee's Name :", boldFont));
            mainInfo.addCell(createValueCell(worker.getFirstName() + " " + worker.getLastName(), regularFont));

            mainInfo.addCell(createLabelCell("Employee's Address :", boldFont));
            mainInfo.addCell(createValueCell(worker.getHomeAddress(), regularFont));

            mainInfo.addCell(createLabelCell("City, State, ZipCode :", boldFont));
            mainInfo.addCell(createValueCell(worker.getCity() + " " + worker.getState() + " " + worker.getZipcode(), regularFont));

            mainInfo.addCell(createLabelCell("Marital Status :", boldFont));
            mainInfo.addCell(createValueCell(worker.getFilingStatus().name(), regularFont));

            mainInfo.addCell(createLabelCell("Employee's SSN:", boldFont));
            mainInfo.addCell(createValueCell(ssn, regularFont));

            mainInfo.addCell(createLabelCell("Employer's Name:", boldFont));
            mainInfo.addCell(createValueCell(employerName, regularFont));

            mainInfo.addCell(createLabelCell("Employer's EIN:", boldFont));
            mainInfo.addCell(createValueCell(employerEin, regularFont));

            mainInfo.addCell(createLabelCell("Employer's Address:", boldFont));
            mainInfo.addCell(createValueCell(employerAddress, regularFont));

            document.add(mainInfo);

            document.add(new Paragraph("2.   Your " + year + " Year-to-Date Pay Stub")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            document.add(new Paragraph("Gross wages: " + payrollSummary.getGrossPayTotal() + "$")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph("These are the taxes withheld from your gross pay:")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("3. Your W-2 and Gross Wages Explained:")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            document.add(new Paragraph("You might notice that your gross wages above differ from the taxable wages \n " +
                    "reported on your W-2. This difference is usually due to pre-tax deductions\n" +
                    "which reduce your taxable income. Additionally, if you reached the taxable\n" +
                    "wage limit for certain taxes, any earnings above that limit are not subject to\n" +
                    "taxation.")
                    .setFont(regularFont)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // === Добавим страницы с Copy B, C, 2 ===
            List<String> copies = List.of("Copy B - For IRS", "Copy C - For Employee", "Copy 2 - For State/City");

            for (String copyTitle : copies) {
                document.add(new AreaBreak());
                document.add(new Paragraph("Face-Check Corporation").setFont(boldFont).setFontSize(12));
                document.add(new Paragraph(copyTitle).setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph("Form W-2 - Wage and Tax Statement").setFont(boldFont).setFontSize(14).setTextAlignment(TextAlignment.CENTER));

                Table boxTable = new Table(UnitValue.createPercentArray(new float[]{1f, 1f, 1f})).useAllAvailableWidth();
                boxTable.addCell(createBox("Box 1", "Wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 2", "Federal Tax", payrollSummary.getFederalWithholdingTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 3", "Social Security Wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 4", "SS Tax Withheld", payrollSummary.getSocialSecurityEmployeeTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 5", "Medicare Wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 6", "Medicare Tax", payrollSummary.getMedicareTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 16", "State Wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 17", "State Income Tax", payrollSummary.getNyStateWithholdingTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 18", "Local Wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 19", "Local Tax", payrollSummary.getNyLocalWithholdingTotal().toString(), regularFont));
                boxTable.addCell(createBox("Box 20", "Locality Name", "NYC", regularFont));

                document.add(boxTable);
                document.add(new Paragraph("Total Tax Withheld: $" + payrollSummary.getTotalAllTaxes()).setFont(boldFont).setTextAlignment(TextAlignment.CENTER).setFontSize(12).setMarginTop(10));
                document.add(new Paragraph("Generated by Face-Check").setFontSize(8).setTextAlignment(TextAlignment.CENTER).setItalic());
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        }
    }

    private Cell createLabelCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9)).setBorder(Border.NO_BORDER);
    }

    private Cell createValueCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9)).setBorder(Border.NO_BORDER);
    }

    private Cell createBox(String title, String desc, String value, PdfFont font) {
        Table inner = new Table(1);
        inner.addCell(new Cell().add(new Paragraph(title).setFont(font).setFontSize(9).setBold()).setBorder(Border.NO_BORDER));
        inner.addCell(new Cell().add(new Paragraph(desc).setFont(font).setFontSize(8)).setBorder(Border.NO_BORDER));
        inner.addCell(new Cell().add(new Paragraph("$" + value).setFont(font).setFontSize(9).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        return new Cell().add(inner).setBorder(new SolidBorder(BLACK, 0.5f));
    }
}