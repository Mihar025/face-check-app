package com.zikpak.facecheck.services.amazonS3Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportManagementService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Получить список всех отчетов для компании
     */
    public List<ReportFileDTO> getCompanyReports(Integer companyId, String companyName) {
        List<ReportFileDTO> reports = new ArrayList<>();

        // Формируем префикс для поиска файлов компании
        String companyPrefix = generateCompanyPrefix(companyName, companyId);

        try {
            // Получаем список отчетов по часам
            String hoursPrefix = companyPrefix + "/reports/hours/";
            reports.addAll(listReportsInPrefix(hoursPrefix, "Hours Report"));

            // Получаем список отчетов по зарплате
            String payrollPrefix = companyPrefix + "/reports/payroll/";
            reports.addAll(listReportsInPrefix(payrollPrefix, "Payroll Report"));

            log.info("Found {} reports for company {}", reports.size(), companyId);

        } catch (Exception e) {
            log.error("Error getting reports for company {}: {}", companyId, e.getMessage());
            throw new RuntimeException("Failed to retrieve company reports", e);
        }

        // Сортируем по дате создания (новые первые)
        reports.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));

        return reports;
    }

    /**
     * Получить отчеты определенного типа
     */
    public List<ReportFileDTO> getReportsByType(Integer companyId, String companyName, String reportType) {
        String companyPrefix = generateCompanyPrefix(companyName, companyId);
        String prefix = companyPrefix + "/reports/" + reportType.toLowerCase() + "/";

        List<ReportFileDTO> reports = listReportsInPrefix(prefix,
                reportType.equals("hours") ? "Hours Report" : "Payroll Report");

        reports.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        return reports;
    }

    /**
     * Скачать конкретный отчет
     */
    public S3FileDTO downloadReport(String reportKey) {
        try {
            log.info("Downloading report: {}", reportKey);

            S3Object s3Object = s3Client.getObject(bucketName, reportKey);
            ObjectMetadata metadata = s3Object.getObjectMetadata();

            byte[] data = s3Object.getObjectContent().readAllBytes();

            return new S3FileDTO(data, "application/pdf");

        } catch (Exception e) {
            log.error("Error downloading report {}: {}", reportKey, e.getMessage());
            throw new RuntimeException("Failed to download report", e);
        }
    }

    /**
     * Удалить отчет
     */
    public void deleteReport(String reportKey) {
        try {
            s3Client.deleteObject(bucketName, reportKey);
            log.info("Deleted report: {}", reportKey);
        } catch (Exception e) {
            log.error("Error deleting report {}: {}", reportKey, e.getMessage());
            throw new RuntimeException("Failed to delete report", e);
        }
    }

    /**
     * Получить список отчетов в указанном префиксе
     */
    private List<ReportFileDTO> listReportsInPrefix(String prefix, String reportType) {
        List<ReportFileDTO> reports = new ArrayList<>();

        try {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(prefix);

            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(request);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    // Пропускаем директории
                    if (objectSummary.getKey().endsWith("/")) {
                        continue;
                    }

                    // Только PDF файлы
                    if (objectSummary.getKey().toLowerCase().endsWith(".pdf")) {
                        ReportFileDTO reportFile = new ReportFileDTO();
                        reportFile.setKey(objectSummary.getKey());
                        reportFile.setFileName(extractFileName(objectSummary.getKey()));
                        reportFile.setReportType(reportType);
                        reportFile.setPeriodType(extractPeriodType(objectSummary.getKey()));
                        reportFile.setFileSize(objectSummary.getSize());
                        reportFile.setCreatedDate(convertToLocalDateTime(objectSummary.getLastModified()));
                        reportFile.setUrl(generatePresignedUrl(objectSummary.getKey()));

                        reports.add(reportFile);
                    }
                }

                request.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

        } catch (Exception e) {
            log.error("Error listing reports in prefix {}: {}", prefix, e.getMessage());
        }

        return reports;
    }

    /**
     * Генерировать временную ссылку для скачивания
     */
    private String generatePresignedUrl(String key) {
        try {
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60; // 1 час
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(com.amazonaws.HttpMethod.GET)
                            .withExpiration(expiration);

            return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for {}: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Извлечь имя файла из ключа
     */
    private String extractFileName(String key) {
        String[] parts = key.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Определить тип периода (weekly, monthly, quarterly, custom)
     */
    private String extractPeriodType(String key) {
        if (key.contains("/weekly/")) return "Weekly";
        if (key.contains("/monthly/")) return "Monthly";
        if (key.contains("/quarterly/") || key.contains("/Q")) return "Quarterly";
        if (key.contains("/custom/")) return "Custom";

        // Попробуем определить по имени файла
        String fileName = extractFileName(key).toLowerCase();
        if (fileName.contains("week")) return "Weekly";
        if (fileName.contains("month")) return "Monthly";
        if (fileName.contains("quarter") || fileName.contains("_q")) return "Quarterly";

        return "Custom";
    }

    /**
     * Сформировать префикс компании
     */
    private String generateCompanyPrefix(String companyName, Integer companyId) {
        String cleanName = companyName.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", "_");
        return cleanName + "_" + companyId;
    }

    /**
     * Конвертировать Date в LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}