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
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;
import static com.itextpdf.kernel.color.Color.BLACK;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayStubPdfGeneratorService {
    public byte[] generatePayStubPdf(PayStubDTO stub) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont regularFont = PdfFontFactory.createFont(HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(HELVETICA_BOLD);

            // Логотип
            String logoPath = "/Users/mishamaydanskiy/face-check-app/face-check/ApiModule/src/main/resources/assets/logo.jpg";
            ImageData logoData = ImageDataFactory.create(logoPath);
            document.add(new com.itextpdf.layout.element.Image(logoData).scaleToFit(80, 80).setTextAlignment(TextAlignment.CENTER));

            // Название компании
            document.add(new Paragraph("Face-Check Corporation")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph("PAY STUB")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new Paragraph("Pay Period: " + stub.getPeriodStart() + " - " + stub.getPeriodEnd())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30));

            // Информация о сотруднике
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();
            infoTable.addCell(createLabelCell("Employee Name:", boldFont));
            infoTable.addCell(createValueCell(stub.getEmployeeName(), regularFont));
            infoTable.addCell(createLabelCell("Employee SSN:", boldFont));
            infoTable.addCell(createValueCell(stub.getEmployeeSsn(), regularFont));
            infoTable.addCell(createLabelCell("Position:", boldFont));
            infoTable.addCell(createValueCell(stub.getPosition(), regularFont));
            document.add(infoTable);

            document.add(new Paragraph(" "));

            // Таблица по дням недели
            Table daysTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2})).useAllAvailableWidth();
            daysTable.addHeaderCell(createHeaderCell("Day"));
            daysTable.addHeaderCell(createHeaderCell("Hours Worked"));
            daysTable.addHeaderCell(createHeaderCell("Gross Pay"));

            for (DayOfWeek day : DayOfWeek.values()) {
                BigDecimal hoursWorked = stub.getHoursWorkedPerDay() != null
                        ? stub.getHoursWorkedPerDay().getOrDefault(day, BigDecimal.ZERO)
                        : BigDecimal.ZERO;

                BigDecimal grossPay = stub.getGrossPayPerDay() != null
                        ? stub.getGrossPayPerDay().getOrDefault(day, BigDecimal.ZERO)
                        : BigDecimal.ZERO;

                daysTable.addCell(createValueCell(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH), regularFont));
                daysTable.addCell(createValueCell(hoursWorked.toString(), regularFont));
                daysTable.addCell(createValueCell("$" + grossPay.toString(), regularFont));
            }


            document.add(daysTable);

            document.add(new Paragraph(" "));

            BigDecimal totalHours = stub.getTotalHours() != null ? stub.getTotalHours() : BigDecimal.ZERO;
            BigDecimal totalGrossPay = stub.getTotalGrossPay() != null ? stub.getTotalGrossPay() : BigDecimal.ZERO;
            BigDecimal federalTax = stub.getFederalTax() != null ? stub.getFederalTax() : BigDecimal.ZERO;
            BigDecimal socialSecurityTax = stub.getSocialSecurityTax() != null ? stub.getSocialSecurityTax() : BigDecimal.ZERO;
            BigDecimal medicareTax = stub.getMedicareTax() != null ? stub.getMedicareTax() : BigDecimal.ZERO;
            BigDecimal stateTax = stub.getStateTax() != null ? stub.getStateTax() : BigDecimal.ZERO;
            BigDecimal localTax = stub.getLocalTax() != null ? stub.getLocalTax() : BigDecimal.ZERO;
            BigDecimal netPay = stub.getNetPay() != null ? stub.getNetPay() : BigDecimal.ZERO;

            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            summaryTable.addCell(createLabelCell("Total Hours:", boldFont));
            summaryTable.addCell(createValueCell(totalHours.toString(), regularFont));
            summaryTable.addCell(createLabelCell("Total Gross Pay:", boldFont));
            summaryTable.addCell(createValueCell("$" + totalGrossPay, regularFont));
            summaryTable.addCell(createLabelCell("Federal Tax Withheld:", boldFont));
            summaryTable.addCell(createValueCell("$" + federalTax, regularFont));
            summaryTable.addCell(createLabelCell("Social Security Tax:", boldFont));
            summaryTable.addCell(createValueCell("$" + socialSecurityTax, regularFont));
            summaryTable.addCell(createLabelCell("Medicare Tax:", boldFont));
            summaryTable.addCell(createValueCell("$" + medicareTax, regularFont));
            summaryTable.addCell(createLabelCell("State Tax:", boldFont));
            summaryTable.addCell(createValueCell("$" + stateTax, regularFont));
            summaryTable.addCell(createLabelCell("Local Tax:", boldFont));
            summaryTable.addCell(createValueCell("$" + localTax, regularFont));
            summaryTable.addCell(createLabelCell("Net Pay:", boldFont));
            summaryTable.addCell(createValueCell("$" + netPay, regularFont));


            document.add(summaryTable);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации Pay Stub PDF", e);
        }
    }

    private Cell createLabelCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }

    private Cell createValueCell(String text, PdfFont font) {
        if (text == null) {
            text = "";
        }
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }


    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFontSize(11)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER))
                .setPadding(5);
    }
}