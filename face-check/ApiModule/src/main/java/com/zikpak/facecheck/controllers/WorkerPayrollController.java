package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.helperServices.CompanyPayrollService;
import com.zikpak.facecheck.helperServices.W2PdfGeneratorService;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("payroll")
@RequiredArgsConstructor
public class WorkerPayrollController {
    @Autowired
    private  WorkerPayRollService workerPayRollService;
    private final W2PdfGeneratorService w2PdfGeneratorService;
    private final UserRepository userRepository;
    private final CompanyPayrollService companyPayrollService;


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



}
