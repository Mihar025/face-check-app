package com.zikpak.facecheck.taxesServices.services.wcRiskService;

import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportDto;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportLine;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;  // ← импорт
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WcRiskCsvService {

    private final WcRiskServiceForPDF reportService;
    private final AmazonS3Service amazonS3Service;            // ← внедрили S3-сервис
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_DATE;

    public byte[] generateCsv(Integer companyId,
                              LocalDate periodStart,
                              LocalDate periodEnd) {
        // 1) Получаем DTO
        WcReportDto dto = reportService.generateReport(companyId, periodStart, periodEnd);

        // 2) Строим CSV
        StringBuilder sb = new StringBuilder();
        sb.append("Company,").append(escape(dto.getCompanyName())).append("\n");
        sb.append("Period Start,").append(dto.getPeriodStart().format(DATE_FMT)).append("\n");
        sb.append("Period End,").append(dto.getPeriodEnd().format(DATE_FMT)).append("\n\n");
        sb.append("Risk Code,Description,Total Wages,Rate,EMR,Total Contribution\n");
        for (WcReportLine line : dto.getLines()) {
            sb.append(escape(line.getCode())).append(',')
                    .append(escape(line.getDescription())).append(',')
                    .append(line.getTotalWages()).append(',')
                    .append(line.getRate()).append(',')
                    .append(line.getEmr()).append(',')
                    .append(line.getTotalContribution()).append('\n');
        }
        sb.append("\n,,,,Grand Total,").append(dto.getGrandTotal()).append("\n");

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        // 3) Собираем ключ для S3 (аналогично PDF)
        String companyKeyPart = dto.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]", "_");
        String periodPart = periodStart + "_to_" + periodEnd;
        String key = String.format("%s/%d/wcReports/%s.csv",
                companyKeyPart,
                companyId,
                periodPart);

        // 4) Заливаем в S3
        amazonS3Service.uploadPdfToS3(csvBytes, key);  // или, если есть uploadCsvToS3:
        // amazonS3Service.uploadCsvToS3(csvBytes, key);

        return csvBytes;
    }

    private String escape(String value) {
        if (value == null) return "";
        String esc = value.replace("\"", "\"\"");
        if (esc.contains(",") || esc.contains("\"") || esc.contains("\n")) {
            return "\"" + esc + "\"";
        }
        return esc;
    }
}
