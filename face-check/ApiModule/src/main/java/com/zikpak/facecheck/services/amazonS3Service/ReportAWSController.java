package com.zikpak.facecheck.services.amazonS3Service;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("aws-reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ReportAWSController {
    private final ReportManagementService reportManagementService;

    /**
     * Получить все отчеты компании
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReportFileDTO>> getCompanyReports(
            @PathVariable Integer companyId,
            @RequestParam String companyName) {

        log.info("Getting reports for company: {} ({})", companyName, companyId);
        List<ReportFileDTO> reports = reportManagementService.getCompanyReports(companyId, companyName);
        return ResponseEntity.ok(reports);
    }

    /**
     * Получить отчеты определенного типа
     */
    @GetMapping("/company/{companyId}/type/{reportType}")
    public ResponseEntity<List<ReportFileDTO>> getReportsByType(
            @PathVariable Integer companyId,
            @PathVariable String reportType,
            @RequestParam String companyName) {

        log.info("Getting {} reports for company: {}", reportType, companyId);
        List<ReportFileDTO> reports = reportManagementService.getReportsByType(companyId, companyName, reportType);
        return ResponseEntity.ok(reports);
    }

    /**
     * Скачать отчет
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadReport(@RequestParam String key) {
        log.info("Downloading report: {}", key);

        S3FileDTO fileData = reportManagementService.downloadReport(key);

        // Извлекаем имя файла из ключа
        String fileName = key.substring(key.lastIndexOf("/") + 1);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileData.data());
    }

    /**
     * Просмотреть отчет (открыть в браузере)
     */
    @GetMapping("/view")
    public ResponseEntity<byte[]> viewReport(@RequestParam String key) {
        log.info("Viewing report: {}", key);

        S3FileDTO fileData = reportManagementService.downloadReport(key);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileData.data());
    }

    /**
     * Удалить отчет
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteReport(@RequestParam String key) {
        log.info("Deleting report: {}", key);
        reportManagementService.deleteReport(key);
        return ResponseEntity.ok().build();
    }
}