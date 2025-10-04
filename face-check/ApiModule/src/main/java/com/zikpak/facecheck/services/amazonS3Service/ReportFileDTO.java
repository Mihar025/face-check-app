package com.zikpak.facecheck.services.amazonS3Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFileDTO {
    private String key;           // S3 key
    private String fileName;      // Имя файла
    private String reportType;    // Hours Report / Payroll Report
    private String periodType;    // Weekly / Monthly / Quarterly / Custom
    private Long fileSize;        // Размер в байтах
    private LocalDateTime createdDate;
    private String url;          // Presigned URL для скачивания
}