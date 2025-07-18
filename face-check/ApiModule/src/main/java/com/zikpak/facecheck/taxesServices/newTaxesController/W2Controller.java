package com.zikpak.facecheck.taxesServices.newTaxesController;


import com.zikpak.facecheck.taxesServices.pdfServices.FillFormMTA305;
import com.zikpak.facecheck.taxesServices.pdfServices.W2OfficialPDFService;
import com.zikpak.facecheck.taxesServices.pdfServices.W3OfficialPDFServicer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("w2")
@RequiredArgsConstructor
public class W2Controller {

    private final W2OfficialPDFService w2OfficialPDFService;
    private final W3OfficialPDFServicer w3OfficialPDFServicer   ;
    private final FillFormMTA305 fillFormMTA305;


    @GetMapping("/generate/{workerId}/{companyId}/{year}")
    public ResponseEntity<byte[]> generateW2(
            @PathVariable("workerId") Integer workerId,
            @PathVariable("companyId") Integer companyId,
            @PathVariable("year") Integer year
    ) throws IOException {
        byte[] pdf = w2OfficialPDFService.generateFilledPdf(workerId, companyId, year);

        // Формируем правильное имя файла с расширением .pdf
        String fileName = String.format("W2_%d_%d_%d.pdf", workerId, companyId, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }



    @GetMapping("/generate/{companyId}/{year}")
    public ResponseEntity<byte[]> generateW3(
            @PathVariable("companyId") Integer companyId,
            @PathVariable("year") Integer year
    ) throws IOException {
        byte[] pdf = w3OfficialPDFServicer.generateFilledPdf(companyId, year);

        // Формируем правильное имя файла с расширением .pdf
        String fileName = String.format("W3_%d_%d.pdf", companyId, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }


    @GetMapping("/MTA305/{companyId}/{quarter}/{year}")
    public ResponseEntity<byte[]> generateMTA305(
            @PathVariable("companyId") Integer companyId,
            @PathVariable("quarter")    int quarter,
            @PathVariable("year")       Integer year
    ) throws IOException {
        byte[] pdf = fillFormMTA305.generateFilledPdf(companyId, quarter, year);

        // Формируем имя файла с companyId, кварталом и годом
        String fileName = String.format("MTA305_%d_Q%d_%d.pdf",
                companyId, quarter, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }
}
