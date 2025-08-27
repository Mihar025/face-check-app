package com.zikpak.facecheck.taxesServices.efiles.csvReports;


import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.EmployeeSummaryDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollSummaryReportCsvService {

    private final AmazonS3Service amazonS3Service;

    /**
     * Generate Payroll Summary CSV report and upload to S3
     */
    public byte[] generatePayrollSummaryReportCsv(PayrollSummaryReportDTO reportData, Integer companyId) {
        try {
            StringBuilder csv = new StringBuilder();

            // Header with company info
            csv.append("Company Name,").append(reportData.getCompanyName()).append("\n");
            csv.append("Company Address,").append(escapeCsvField(reportData.getCompanyAddress())).append("\n");
            csv.append("Company City,").append(reportData.getCompanyCity()).append("\n");
            csv.append("Company State,").append(reportData.getCompanyState()).append("\n");
            csv.append("Company ZIP,").append(reportData.getCompanyZipCode()).append("\n");
            csv.append("Company Phone,").append(reportData.getCompanyPhone()).append("\n");
            csv.append("Report Period,").append(reportData.getPeriodStart()).append(" to ").append(reportData.getPeriodEnd()).append("\n");
            csv.append("Report Type,").append(reportData.getReportType()).append("\n");
            csv.append("Generated Date,").append(LocalDate.now()).append("\n");
            csv.append("\n"); // Empty line

            // Summary totals
            csv.append("PAYROLL SUMMARY TOTALS\n");
            csv.append("Total Employees,").append(reportData.getTotalEmployees()).append("\n");
            csv.append("Total Hours Worked,").append(formatDecimal(reportData.getTotalHoursWorked())).append("\n");
            csv.append("Average Hourly Rate,").append("$").append(formatDecimal(reportData.getAverageHourlyRate())).append("\n");
            csv.append("Total Gross Pay,").append("$").append(formatDecimal(reportData.getTotalGrossPay())).append("\n");
            csv.append("Total Taxes Withheld,").append("$").append(formatDecimal(reportData.getTotalTaxesWithheld())).append("\n");
            csv.append("Total Net Pay,").append("$").append(formatDecimal(reportData.getTotalNetPay())).append("\n");
            csv.append("\n"); // Empty line

            // Employee breakdown header
            csv.append("EMPLOYEE PAYROLL BREAKDOWN\n");
            csv.append("Employee Name,Regular Hours,Overtime Hours,Hourly Rate,Gross Pay,Net Pay,Total Hours\n");

            // Employee data
            if (reportData.getEmployeeBreakdown() != null) {
                for (EmployeeSummaryDTO emp : reportData.getEmployeeBreakdown()) {
                    csv.append(escapeCsvField(emp.getEmployeeName())).append(",");
                    csv.append(formatDecimal(emp.getRegularHours())).append(",");
                    csv.append(formatDecimal(emp.getOvertimeHours())).append(",");
                    csv.append("$").append(formatDecimal(emp.getHourlyRate())).append(",");
                    csv.append("$").append(formatDecimal(emp.getGrossPay())).append(",");
                    csv.append("$").append(formatDecimal(emp.getNetPay())).append(",");
                    csv.append(formatDecimal(emp.getHoursWorked())).append("\n");
                }
            }

            csv.append("\n"); // Empty line

            // Payroll summary statistics
            csv.append("PAYROLL STATISTICS\n");
            if (reportData.getEmployeeBreakdown() != null && !reportData.getEmployeeBreakdown().isEmpty()) {
                // Calculate additional statistics
                BigDecimal maxGrossPay = reportData.getEmployeeBreakdown().stream()
                        .map(EmployeeSummaryDTO::getGrossPay)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal minGrossPay = reportData.getEmployeeBreakdown().stream()
                        .map(EmployeeSummaryDTO::getGrossPay)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal avgGrossPay = reportData.getEmployeeBreakdown().stream()
                        .map(EmployeeSummaryDTO::getGrossPay)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(reportData.getEmployeeBreakdown().size()), 2, BigDecimal.ROUND_HALF_UP);

                csv.append("Highest Gross Pay,").append("$").append(formatDecimal(maxGrossPay)).append("\n");
                csv.append("Lowest Gross Pay,").append("$").append(formatDecimal(minGrossPay)).append("\n");
                csv.append("Average Gross Pay Per Employee,").append("$").append(formatDecimal(avgGrossPay)).append("\n");
            }

            csv.append("\n"); // Empty line

            // Footer
            csv.append("REPORT FOOTER\n");
            csv.append("Generated by Facecheck Payroll System\n");
            csv.append("Report generated on,").append(LocalDate.now()).append("\n");
            csv.append("This report provides a summary of payroll data for the specified period\n");
            csv.append("Please verify all information before processing payments or filing taxes\n");

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

            // Upload to S3 (same pattern as PDF)
            uploadCsvToS3(reportData, companyId, csvBytes);

            log.info("Successfully generated Payroll Summary Report CSV, size: {} bytes", csvBytes.length);
            return csvBytes;

        } catch (Exception e) {
            log.error("Error generating Payroll Summary Report CSV", e);
            throw new RuntimeException("Failed to generate Payroll Summary Report CSV", e);
        }
    }

    /**
     * Upload CSV to S3 with same key pattern as PDF
     */
    private void uploadCsvToS3(PayrollSummaryReportDTO reportData, Integer companyId, byte[] csvBytes) {
        try {
            // Генерируем правильный S3 ключ
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "_");

            // Определяем период
            String periodPart;
            String fileName;

            // Если это месячный отчет
            if (reportData.getReportType().equals("Monthly")) {
                periodPart = String.format("monthly/%02d", reportData.getPeriodStart().getMonthValue());
                fileName = String.format("payroll_summary_%d_%02d_%s.csv",
                        reportData.getPeriodStart().getYear(),
                        reportData.getPeriodStart().getMonthValue(),
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            // Если это квартальный отчет
            else if (reportData.getReportType().equals("Quarterly")) {
                int quarter = (reportData.getPeriodStart().getMonthValue() - 1) / 3 + 1;
                periodPart = String.format("Q%d", quarter);
                fileName = String.format("payroll_summary_%d_Q%d_%s.csv",
                        reportData.getPeriodStart().getYear(),
                        quarter,
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            // Если это годовой отчет
            else if (reportData.getReportType().equals("Annual")) {
                periodPart = "annual";
                fileName = String.format("payroll_summary_%d_annual_%s.csv",
                        reportData.getPeriodStart().getYear(),
                        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            // Custom период
            else {
                periodPart = "custom";
                fileName = String.format("payroll_summary_%s_to_%s.csv",
                        reportData.getPeriodStart().format(DateTimeFormatter.BASIC_ISO_DATE),
                        reportData.getPeriodEnd().format(DateTimeFormatter.BASIC_ISO_DATE));
            }

            // CSV файлы идут в подпапку csv рядом с PDF
            String key = String.format("%s_%d/reports/payroll/%d/%s/csv/%s",
                    companyKeyPart,                        // "facecheck_corp"
                    companyId,                              // "_123"
                    reportData.getPeriodStart().getYear(),  // "/2024"
                    periodPart,                             // "/Q1" или "/monthly/03"
                    fileName                                // "payroll_summary_2024_Q1_20240415.csv"
            );

            // Результаты:
            // Месячный: facecheck_corp_123/reports/payroll/2024/monthly/03/csv/payroll_summary_2024_03_20240331.csv
            // Квартальный: facecheck_corp_123/reports/payroll/2024/Q1/csv/payroll_summary_2024_Q1_20240415.csv
            // Годовой: facecheck_corp_123/reports/payroll/2024/annual/csv/payroll_summary_2024_annual_20250131.csv
            // Custom: facecheck_corp_123/reports/payroll/2024/custom/csv/payroll_summary_20240115_to_20240215.csv

            // TODO: Создать отдельный метод uploadCsvToS3 в AmazonS3Service
            amazonS3Service.uploadPdfToS3(csvBytes, key);

            log.info("Successfully uploaded Payroll Summary Report CSV to S3 with key: {}", key);

        } catch (Exception e) {
            log.error("Failed to upload Payroll Summary Report CSV to S3", e);
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
