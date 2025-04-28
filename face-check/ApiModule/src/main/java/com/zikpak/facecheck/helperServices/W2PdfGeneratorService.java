package com.zikpak.facecheck.helperServices;

import java.io.ByteArrayOutputStream;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

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

            String destination = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/logo.jpg";
            ImageData data = ImageDataFactory.create(destination);
            Image imageData = new Image(data);
            imageData.scaleToFit(50, 50);


            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

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


            document.add(new Paragraph(" "));


            document.add(new Paragraph(" "));

            Table mainInfo = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();

            mainInfo.addCell(createLabelCell("Employee's Name :", boldFont));
            mainInfo.addCell(createValueCell(worker.getFirstName() + " " + worker.getLastName(), regularFont));

            mainInfo.addCell(createLabelCell("Employee's Address :", boldFont));
            mainInfo.addCell(createValueCell(worker.getHomeAddress() , regularFont));

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

            document.add(new Paragraph(" "));

            document.add(new Paragraph("2.   Your " + year + " Year-to-Date Pay Stub")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20)
                    .setMarginBottom(10));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Gross wages: " + payrollSummary.getGrossPayTotal() + "$")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph("These are the taxes withheld from your gross pay:")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            Table boxes = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth();

            document.add(new Paragraph("3. Your W-2 and Gross Wages Explained:")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20)
                    .setMarginBottom(10));
            document.add(new Paragraph(" "));


            document.add(new Paragraph("You might notice that your gross wages above differ from the taxable wages \n " +
                    "reported on your W-2. This difference is usually due to pre-tax deductions\n" +
                    "which reduce your taxable income. Additionally, if you reached the taxable\n" +
                    "wage limit for certain taxes, any earnings above that limit are not subject to\n" +
                    "taxation.")
                    .setFont(regularFont)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));
            document.add(new Paragraph(" "));


            boxes.addCell(createBoxCell("Box 1", "Wages, tips, other compensation", payrollSummary.getGrossPayTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 2", "Federal income tax withheld", payrollSummary.getFederalWithholdingTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 3", "Social security wages", payrollSummary.getGrossPayTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 4", "Social security tax withheld", payrollSummary.getSocialSecurityEmployeeTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 5", "Medicare wages and tips", payrollSummary.getGrossPayTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 6", "Medicare tax withheld", payrollSummary.getMedicareTotal().toString(), regularFont));

            boxes.addCell(createBoxCell("Box 7", "Social security tips", payrollSummary.getSocialSecurityTips() != null ? payrollSummary.getSocialSecurityTips().toString() : "0.00", regularFont));
            boxes.addCell(createBoxCell("Box 8", "Allocated tips", payrollSummary.getAllocatedTips() != null ? payrollSummary.getAllocatedTips().toString() : "0.00", regularFont));


            boxes.addCell(createBoxCell("Box 10", "Dependent care benefits", payrollSummary.getDependentCareBenefits() != null ? payrollSummary.getDependentCareBenefits().toString() : "0.00", regularFont));
            boxes.addCell(createBoxCell("Box 11", "Nonqualified plans", payrollSummary.getNonqualifiedPlans() != null ? payrollSummary.getNonqualifiedPlans().toString() : "0.00", regularFont));
            Table box12Table = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                    .useAllAvailableWidth();
            document.add(new Paragraph("4. Additional Information (Box 12)")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph(" "));


            box12Table.addCell(createLabelCell("Box 12 - Code D (401(k) Contributions):", boldFont));
            box12Table.addCell(createValueCell("$0.00", regularFont));

            box12Table.addCell(createLabelCell("Box 12 - Code DD (Health Insurance Cost):", boldFont));
            box12Table.addCell(createValueCell("$0.00", regularFont));

            document.add(box12Table);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("5. Retirement Plan Status (Box 13)").setFont(boldFont).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph(" "));

            if (Boolean.TRUE.equals(payrollSummary.getHasRetirementPlan())) {
                document.add(new Paragraph("Retirement Plan: ☑ YES").setFont(boldFont).setFontSize(12));
            } else {
                document.add(new Paragraph("Retirement Plan: ☐ NO").setFont(boldFont).setFontSize(12));
            }

            document.add(new Paragraph(" "));


            boxes.addCell(createBoxCell("Box 16", "State wages, tips", payrollSummary.getGrossPayTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 17", "State income tax", payrollSummary.getNyStateWithholdingTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 18", "Local wages (NYC)", payrollSummary.getGrossPayTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 19", "Local income tax (NYC)", payrollSummary.getNyLocalWithholdingTotal().toString(), regularFont));
            boxes.addCell(createBoxCell("Box 20", "Locality Name", "NYC", regularFont));

            document.add(new Paragraph("Total Tax Withheld: " + payrollSummary.getTotalAllTaxes()+ "$")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(boxes);


            document.showTextAligned(
                    new Paragraph("Generated by Face-Check Corporation")
                            .setFontSize(8)
                            .setFont(regularFont)
                            .setItalic(),
                    297.5f,
                    20,
                    TextAlignment.CENTER
            );

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        }
    }

    private Cell createLabelCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(5)) // добавил padding внутрь
                .setBorder(Border.NO_BORDER);
    }

    private Cell createValueCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }



    private Cell createBoxCell(String boxNumber, String description, String amount, PdfFont font) {
        Table innerTable = new Table(1);
        innerTable.addCell(new Paragraph(boxNumber)
                .setFont(font)
                .setFontSize(10)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setPaddingLeft(5));
        innerTable.addCell(new Paragraph(description)
                .setFont(font)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.LEFT)
                .setPaddingLeft(5));
        innerTable.addCell(new Paragraph("$" + amount)
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(5));

        Cell cell = new Cell().add(innerTable);
        cell.setBorder(new SolidBorder(BLACK, 0.5f)); // тонкая рамка
        return cell;
    }

}


