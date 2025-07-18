package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskCsvService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskServiceForPDF;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcCodeRequest;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcRiskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("wc-classes")
@RequiredArgsConstructor
public class WcRiskClassController {

    private final WcRiskService service;
    private final WcRiskServiceForPDF pdfService;
    private final WcRiskCsvService csvService;
    @GetMapping
    public PageResponse<WcRiskResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String industryTag
    ) {
        return service.findAllCodes(page, size, industryTag);
    }

    @GetMapping("/{code}")
    public WcRiskResponse getByCode(@PathVariable String code) {
        return service.findCodeById(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WcRiskResponse create(@Valid @RequestBody WcCodeRequest req) {
        return service.create(req);
    }

    @PutMapping("/{code}")
    public WcRiskResponse update(@PathVariable String code, @Valid @RequestBody WcCodeRequest req) {
        return service.update(code, req);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }


    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Integer companyId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd
    ) {
        byte[] pdfBytes = pdfService.generateWcReportPdf(companyId, periodStart, periodEnd);

        String filename = String.format("wc_report_%d_%s_to_%s.pdf",
                companyId, periodStart, periodEnd);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> generateCsv(
            @PathVariable Integer companyId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd
    ) {
        byte[] csvBytes = csvService.generateCsv(companyId, periodStart, periodEnd);

        String filename = String.format("wc_report_%d_%s_to_%s.csv",
                companyId, periodStart, periodEnd);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

}
