package com.zikpak.facecheck.taxesServices.pdfServices;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.zikpak.facecheck.taxesServices.dto.Form940SummaryDto;
import org.springframework.stereotype.Service;
import com.itextpdf.layout.Document;



import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;

@Service
public class Form940PdfGeneratorService {

    public byte[] generate940Pdf(Form940SummaryDto s) {
        try (var baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf  = new PdfDocument(writer);
            Document doc     = new Document(pdf);

            PdfFont b = PdfFontFactory.createFont(HELVETICA_BOLD);
            PdfFont r = PdfFontFactory.createFont(HELVETICA);

            doc.add(new Paragraph("Form 940 — Employer’s Annual Federal Unemployment (FUTA) Tax Return")
                    .setFont(b).setFontSize(14).setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            Table t = new Table(UnitValue.createPercentArray(new float[]{1,2})).useAllAvailableWidth();
            t.addCell(label("Employer Name:", b));
            t.addCell(value(s.getEmployerName(), r));
            t.addCell(label("Employer EIN:", b));
            t.addCell(value(s.getEmployerEin(), r));
            t.addCell(label("Address:", b));
            t.addCell(value(s.getEmployerAddress(), r));
            t.addCell(label("Year:", b));
            t.addCell(value(String.valueOf(s.getYear()), r));
            t.addCell(label("Number of Employees:", b));
            t.addCell(value(String.valueOf(s.getTotalEmployees()), r));
            t.addCell(label("Total FUTA Tax:", b));
            t.addCell(value("$" + s.getTotalFutaTax(), r));

            doc.add(t);

            doc.add(new Paragraph("\nSignature: ______________________   Date: ____________")
                    .setFont(r).setFontSize(10).setMarginTop(30)
                    .setTextAlignment(TextAlignment.LEFT));

            doc.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Error generating Form 940 PDF", ex);
        }
    }

    private Cell label(String txt, PdfFont f) {
        return new Cell().add(new Paragraph(txt).setFont(f).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }
    private Cell value(String txt, PdfFont f) {
        return new Cell().add(new Paragraph(txt).setFont(f).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }




}
