package com.zikpak.facecheck.taxesServices.efiles.csvReports;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.DailyHoursDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.EmployeeHoursDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HoursReportCsvService {

    private final AmazonS3Service amazonS3Service;

    /**
     * Generate CSV report and upload to S3
     */
    public byte[] generateHoursReportCsv(HoursReportDTO reportData, Integer companyId) {
        try {
            StringBuilder csv = new StringBuilder();

            // Header with company info
            csv.append("Company Name,").append(reportData.getCompanyName()).append("\n");
            csv.append("Report Period,").append(reportData.getPeriodStart()).append(" to ").append(reportData.getPeriodEnd()).append("\n");
            csv.append("Report Type,").append(reportData.getReportType()).append("\n");
            csv.append("Generated Date,").append(LocalDate.now()).append("\n");
            csv.append("\n"); // Empty line

            // Summary totals
            csv.append("SUMMARY TOTALS\n");
            csv.append("Total Employees,").append(reportData.getTotalEmployees()).append("\n");
            csv.append("Total Hours,").append(formatDecimal(reportData.getTotalHours())).append("\n");
            csv.append("Total Regular Hours,").append(formatDecimal(reportData.getTotalRegularHours())).append("\n");
            csv.append("Total Overtime Hours,").append(formatDecimal(reportData.getTotalOvertimeHours())).append("\n");
            csv.append("Average Hours Per Employee,").append(formatDecimal(reportData.getAverageHoursPerEmployee())).append("\n");
            csv.append("Overtime Percentage,").append(formatDecimal(reportData.getOvertimePercentage())).append("%\n");
            csv.append("\n"); // Empty line

            // Employee breakdown header
            csv.append("EMPLOYEE HOURS BREAKDOWN\n");
            csv.append("Employee Name,Regular Hours,Overtime Hours,Total Hours,Daily Average,Overtime Rate %,Hourly Rate,Total Earnings\n");

            // Employee data
            if (reportData.getEmployeeHours() != null) {
                for (EmployeeHoursDTO emp : reportData.getEmployeeHours()) {
                    csv.append(escapeCsvField(emp.getEmployeeName())).append(",");
                    csv.append(formatDecimal(emp.getRegularHours())).append(",");
                    csv.append(formatDecimal(emp.getOvertimeHours())).append(",");
                    csv.append(formatDecimal(emp.getTotalHours())).append(",");
                    csv.append(formatDecimal(emp.getDailyAverage())).append(",");
                    csv.append(formatDecimal(emp.getOvertimeRate())).append(",");
                    csv.append(formatDecimal(emp.getHourlyRate())).append(",");
                    csv.append(formatDecimal(emp.getTotalEarnings())).append("\n");
                }
            }

            csv.append("\n"); // Empty line

            // Daily breakdown header
            csv.append("DAILY HOURS BREAKDOWN\n");
            csv.append("Date,Total Hours,Employees Worked,Average Hours Per Employee,Total Overtime Hours\n");

            // Daily data
            if (reportData.getDailyHours() != null) {
                for (Map.Entry<LocalDate, DailyHoursDTO> entry : reportData.getDailyHours().entrySet()) {
                    DailyHoursDTO daily = entry.getValue();
                    csv.append(daily.getDate()).append(",");
                    csv.append(formatDecimal(daily.getTotalHours())).append(",");
                    csv.append(daily.getEmployeesWorked()).append(",");
                    csv.append(formatDecimal(daily.getAverageHoursPerEmployee())).append(",");
                    csv.append(formatDecimal(daily.getTotalOvertimeHours())).append("\n");
                }
            }

            csv.append("\n"); // Empty line

            // Top performers header
            csv.append("TOP PERFORMERS\n");
            csv.append("Employee Name,Total Hours,Daily Average,Total Earnings\n");

            // Top performers data
            if (reportData.getTopPerformers() != null) {
                for (EmployeeHoursDTO performer : reportData.getTopPerformers()) {
                    csv.append(escapeCsvField(performer.getEmployeeName())).append(",");
                    csv.append(formatDecimal(performer.getTotalHours())).append(",");
                    csv.append(formatDecimal(performer.getDailyAverage())).append(",");
                    csv.append(formatDecimal(performer.getTotalEarnings())).append("\n");
                }
            }

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

            // Upload to S3 (same pattern as PDF)
            uploadCsvToS3(reportData, companyId, csvBytes);

            log.info("Successfully generated Hours Report CSV, size: {} bytes", csvBytes.length);
            return csvBytes;

        } catch (Exception e) {
            log.error("Error generating Hours Report CSV", e);
            throw new RuntimeException("Failed to generate Hours Report CSV", e);
        }
    }

    /**
     * Upload CSV to S3 with same key pattern as PDF
     */
    private void uploadCsvToS3(HoursReportDTO reportData, Integer companyId, byte[] csvBytes) {
        try {
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("hoursReport_%d_%d.csv",
                    companyId,
                    reportData.getPeriodStart().getYear()
            );

            String key = String.format("%s/%d/hoursReport/csv/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            amazonS3Service.uploadPdfToS3(csvBytes, key);
            log.info("Successfully uploaded Hours Report CSV to S3 with key: {}", key);

        } catch (Exception e) {
            log.error("Failed to upload Hours Report CSV to S3", e);
            // Don't throw exception, just log the error since CSV generation succeeded
        }
    }

    /**
     * Escape CSV field if it contains commas, quotes, or newlines
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        // If field contains comma, quote, or newline, wrap in quotes and escape internal quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    /**
     * Format decimal values consistently
     */
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return String.format("%.2f", value);
    }
}