package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.ASCIIservices.EFW2GeneratorService;
import com.zikpak.facecheck.taxesServices.ASCIIservices.Form941FlatFileService;
import com.zikpak.facecheck.taxesServices.pdfServices.Form940PdfGeneratorService;
import com.zikpak.facecheck.taxesServices.services.CompanyPayrollService;
import com.zikpak.facecheck.taxesServices.services.Form940Service;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("taxes-forms")
@RequiredArgsConstructor
public class TaxesController {
    private final AmazonS3Service amazonS3Service;
    private final CompanyPayrollService companyPayrollService;
    private final PayStubService payStubService;
    private final WorkerPayRollService workerPayRollService;
    private final EFW2GeneratorService efw2GeneratorService;
    private final Form940Service form940Service;
    private final Form941FlatFileService form941FlatFileService;
    private final Form940PdfGeneratorService form940PdfGeneratorService;

    @GetMapping("/download-w2/{workerId}")
    public ResponseEntity<byte[]> downloadW2FromS3(@PathVariable Integer workerId,
                                                   @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        String fileName = "w2-statements/" + year + "/" + "w2_" + workerId + ".pdf";

        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w2_statement_" + workerId + ".pdf")
                .body(fileDTO.data());
    }


    @GetMapping("/generate-w3/{companyId}")
    public ResponseEntity<byte[]> generateW3Pdf(@PathVariable Integer companyId,
                                                @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var pdfContent = companyPayrollService.generatePdfW3(companyId, year);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w3_summary_" + companyId + ".pdf")
                .body(pdfContent);
    }

    @GetMapping("/download-w3/{companyId}")
    public ResponseEntity<byte[]> downloadW3FromS3(@PathVariable Integer companyId,
                                                   @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        String fileName = "w3-summary/" + year + "/" + "w3_" + companyId + ".pdf";
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w3-summary_" + companyId + ".pdf")

                .body(fileDTO.data());
    }


    @GetMapping("/download/{payrollId}")
    public ResponseEntity<byte[]> downloadPayStub(@PathVariable Integer payrollId) {
        String fileName = "paystubs/" + payrollId + ".pdf";
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pay_stub_" + payrollId + ".pdf")
                .body(fileDTO.data());
    }


    @GetMapping("/generate/{payrollId}")
    public ResponseEntity<byte[]> generatePayStub(@PathVariable Integer payrollId) {
        byte[] pdf = payStubService.generatePayStubPdf(payrollId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pay_stub_" + payrollId + ".pdf")
                .body(pdf);
    }


    @GetMapping("/worker/{workerId}/year/{year}")
    public WorkerYearlySummaryDto getWorkerYearlySummary(
            @PathVariable Integer workerId,
            @PathVariable int year
    ) {
        return workerPayRollService.calculateWorkerYearlyTotals(workerId, year);
    }


    @GetMapping("/generate-w2/{workerId}")
    public ResponseEntity<byte[]> generateW2Pdf(@PathVariable Integer workerId,
                                                @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var pdfContent = workerPayRollService.generatePDF(workerId, year);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w2_statement_" + workerId + ".pdf")
                .body(pdfContent);
    }

    @GetMapping("/company/{companyId}/year/{year}")
    public ResponseEntity<byte[]> downloadEfw2File(
            @PathVariable Integer companyId,
            @PathVariable int year) {
        byte[] content = efw2GeneratorService.generateEfw2File(companyId, year);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=EFW2_" + companyId + "_" + year + ".txt");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }



    @GetMapping("/generate-940/{companyId}")
    public ResponseEntity<byte[]> пgenerateForm940(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year
    ) throws IOException {

        byte[] pdfBytes = form940Service.generateForm940(companyId, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Form940_" + companyId + "_" + year + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/940/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadForm940(
            @PathVariable Integer companyId,
            @PathVariable int year) throws IOException
    {
        try {
            byte[] pdfBytes = form940Service.generateForm940(companyId, year);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("form940_" + companyId + "_" + year + ".pdf")
                            .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-941/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> generate941(
            @PathVariable Integer companyId,
            @PathVariable int year,
            @PathVariable int quarter) {
        byte[] content = form941FlatFileService.generate941FlatFile(companyId, year, quarter);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData(
                "attachment",
                String.format("F941_%d_%d_%d.txt", companyId, year, quarter)
        );
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    }

