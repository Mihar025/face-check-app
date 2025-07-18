package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes.YearToDateCustomReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("ytd")
public class YearToDateCustomReportController {

    private final YearToDateCustomReportService yearToDateCustomReportService;



    @GetMapping("/generate/{companyId}")
    public ResponseEntity<byte[]> generateYtdReport(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        byte[] pdfBytes = yearToDateCustomReportService.generateYearToDateReport(companyId, startDate, endDate);

        String filename = String.format("YTD_Report_%s_%s_to_%s.pdf",
                companyId,
                startDate.toString(),
                endDate.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


}
