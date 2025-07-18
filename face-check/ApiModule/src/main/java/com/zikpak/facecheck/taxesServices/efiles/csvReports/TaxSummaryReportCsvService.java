package com.zikpak.facecheck.taxesServices.efiles.csvReports;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.EmployeeTaxSummaryDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxBreakdownDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxSummaryReportCsvService {

    private final AmazonS3Service amazonS3Service;

    /**
     * Generate Tax Summary CSV report and upload to S3
     */
    public byte[] generateTaxSummaryReportCsv(TaxSummaryReportDTO reportData, Integer companyId) {
        try {
            StringBuilder csv = new StringBuilder();

            // Header with company info
            csv.append("Company Name,").append(reportData.getCompanyName()).append("\n");
            csv.append("Company Address,").append(escapeCsvField(reportData.getCompanyAddress())).append("\n");
            csv.append("Company City,").append(reportData.getCompanyCity()).append("\n");
            csv.append("Company State,").append(reportData.getCompanyState()).append("\n");
            csv.append("Company ZIP,").append(reportData.getCompanyZipCode()).append("\n");
            csv.append("Company Phone,").append(reportData.getCompanyPhone()).append("\n");
            csv.append("Employer EIN,").append(reportData.getEmployerEIN()).append("\n");
            csv.append("Report Period,").append(reportData.getPeriodStart()).append(" to ").append(reportData.getPeriodEnd()).append("\n");
            csv.append("Report Type,").append(reportData.getReportType()).append("\n");
            csv.append("Tax Year,").append(reportData.getTaxYear()).append("\n");
            if (reportData.getQuarter() != null) {
                csv.append("Quarter,Q").append(reportData.getQuarter()).append("\n");
            }
            csv.append("Generated Date,").append(LocalDate.now()).append("\n");
            csv.append("\n"); // Empty line

            // Tax Summary Totals
            csv.append("TAX SUMMARY TOTALS\n");
            csv.append("Total Employees,").append(reportData.getTotalEmployees()).append("\n");
            csv.append("Active Employees,").append(reportData.getActiveEmployees()).append("\n");
            csv.append("Total Gross Wages,").append("$").append(formatDecimal(reportData.getTotalGrossWages())).append("\n");
            csv.append("Total Taxable Wages,").append("$").append(formatDecimal(reportData.getTotalTaxableWages())).append("\n");
            csv.append("Total Federal Tax Withheld,").append("$").append(formatDecimal(reportData.getTotalFederalTaxWithheld())).append("\n");
            csv.append("Total Social Security Tax,").append("$").append(formatDecimal(reportData.getTotalSocialSecurityTax())).append("\n");
            csv.append("Total Medicare Tax,").append("$").append(formatDecimal(reportData.getTotalMedicareTax())).append("\n");
            csv.append("Total State Tax Withheld,").append("$").append(formatDecimal(reportData.getTotalStateTaxWithheld())).append("\n");
            csv.append("Total Local Tax Withheld,").append("$").append(formatDecimal(reportData.getTotalLocalTaxWithheld())).append("\n");
            csv.append("Total FUTA Tax,").append("$").append(formatDecimal(reportData.getTotalFUTATax())).append("\n");
            csv.append("Total SUTA Tax,").append("$").append(formatDecimal(reportData.getTotalSUTATax())).append("\n");
            csv.append("Total Employee Taxes,").append("$").append(formatDecimal(reportData.getTotalEmployeeTaxes())).append("\n");
            csv.append("Total Employer Taxes,").append("$").append(formatDecimal(reportData.getTotalEmployerTaxes())).append("\n");
            csv.append("Total Tax Liability,").append("$").append(formatDecimal(reportData.getTotalTaxLiability())).append("\n");
            csv.append("\n"); // Empty line

            // Tax Breakdown by Type
            csv.append("TAX BREAKDOWN BY TYPE\n");
            csv.append("Tax Type,Employer Portion,Employee Portion,Total Amount,Description\n");

            if (reportData.getTaxBreakdown() != null) {
                for (TaxBreakdownDTO breakdown : reportData.getTaxBreakdown()) {
                    csv.append(escapeCsvField(breakdown.getTaxType())).append(",");
                    csv.append("$").append(formatDecimal(breakdown.getEmployerPortion())).append(",");
                    csv.append("$").append(formatDecimal(breakdown.getEmployeePortion())).append(",");
                    csv.append("$").append(formatDecimal(breakdown.getTotalAmount())).append(",");
                    csv.append(escapeCsvField(breakdown.getDescription())).append("\n");
                }
            }

            csv.append("\n"); // Empty line

            // Employee Tax Summary
            csv.append("EMPLOYEE TAX SUMMARY\n");
            csv.append("Employee Name,Gross Wages,Federal Withholding,Social Security Withholding,Medicare Withholding,State Withholding,Total Withholdings,Net Pay\n");

            if (reportData.getEmployeeTaxSummary() != null) {
                for (EmployeeTaxSummaryDTO emp : reportData.getEmployeeTaxSummary()) {
                    csv.append(escapeCsvField(emp.getEmployeeName())).append(",");
                    csv.append("$").append(formatDecimal(emp.getGrossWages())).append(",");
                    csv.append("$").append(formatDecimal(emp.getFederalWithholding())).append(",");
                    csv.append("$").append(formatDecimal(emp.getSocialSecurityWithholding())).append(",");
                    csv.append("$").append(formatDecimal(emp.getMedicareWithholding())).append(",");
                    csv.append("$").append(formatDecimal(emp.getStateWithholding())).append(",");
                    csv.append("$").append(formatDecimal(emp.getTotalWithholdings())).append(",");
                    csv.append("$").append(formatDecimal(emp.getNetPay())).append("\n");
                }
            }

            csv.append("\n"); // Empty line

            // Payment Status & Compliance
            csv.append("PAYMENT STATUS & COMPLIANCE\n");
            csv.append("Total Tax Liability,").append("$").append(formatDecimal(reportData.getTotalTaxLiability())).append("\n");
            csv.append("Total Taxes Paid,").append("$").append(formatDecimal(reportData.getTotalTaxesPaid())).append("\n");
            csv.append("Remaining Tax Liability,").append("$").append(formatDecimal(reportData.getRemainingTaxLiability())).append("\n");

            String complianceStatus = reportData.getComplianceStatus() != null && reportData.getComplianceStatus()
                    ? "COMPLIANT" : "NON-COMPLIANT (Payment Required)";
            csv.append("Compliance Status,").append(complianceStatus).append("\n");
            csv.append("\n"); // Empty line

            // Required Forms
            csv.append("REQUIRED TAX FORMS\n");
            if (reportData.getFormsRequired() != null && !reportData.getFormsRequired().isEmpty()) {
                csv.append("Form Name\n");
                for (String form : reportData.getFormsRequired()) {
                    csv.append(escapeCsvField(form)).append("\n");
                }
            } else {
                csv.append("No specific forms required for this period\n");
            }

            csv.append("\n"); // Empty line

            // Tax Summary Statistics
            csv.append("TAX SUMMARY STATISTICS\n");
            if (reportData.getEmployeeTaxSummary() != null && !reportData.getEmployeeTaxSummary().isEmpty()) {
                // Calculate statistics
                BigDecimal maxGrossWages = reportData.getEmployeeTaxSummary().stream()
                        .map(EmployeeTaxSummaryDTO::getGrossWages)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal minGrossWages = reportData.getEmployeeTaxSummary().stream()
                        .map(EmployeeTaxSummaryDTO::getGrossWages)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal avgGrossWages = reportData.getEmployeeTaxSummary().stream()
                        .map(EmployeeTaxSummaryDTO::getGrossWages)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(reportData.getEmployeeTaxSummary().size()), 2, BigDecimal.ROUND_HALF_UP);

                BigDecimal totalTaxWithholdings = reportData.getEmployeeTaxSummary().stream()
                        .map(EmployeeTaxSummaryDTO::getTotalWithholdings)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal avgTaxRate = reportData.getTotalGrossWages().compareTo(BigDecimal.ZERO) > 0
                        ? totalTaxWithholdings.divide(reportData.getTotalGrossWages(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO;

                csv.append("Highest Employee Gross Wages,").append("$").append(formatDecimal(maxGrossWages)).append("\n");
                csv.append("Lowest Employee Gross Wages,").append("$").append(formatDecimal(minGrossWages)).append("\n");
                csv.append("Average Employee Gross Wages,").append("$").append(formatDecimal(avgGrossWages)).append("\n");
                csv.append("Average Tax Withholding Rate,").append(formatDecimal(avgTaxRate)).append("%\n");
            }

            csv.append("\n"); // Empty line

            // Footer
            csv.append("REPORT FOOTER\n");
            csv.append("Generated by Facecheck Tax Compliance System\n");
            csv.append("Report generated on,").append(LocalDate.now()).append("\n");
            csv.append("IMPORTANT: This report is for informational purposes only\n");
            csv.append("Please consult with a qualified tax professional before making tax payments or filing returns\n");
            csv.append("Verify all calculations before submitting to tax authorities\n");

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

            // Upload to S3 (same pattern as PDF)
            uploadCsvToS3(reportData, companyId, csvBytes);

            log.info("Successfully generated Tax Summary Report CSV, size: {} bytes", csvBytes.length);
            return csvBytes;

        } catch (Exception e) {
            log.error("Error generating Tax Summary Report CSV", e);
            throw new RuntimeException("Failed to generate Tax Summary Report CSV", e);
        }
    }

    /**
     * Upload CSV to S3 with same key pattern as PDF
     */
    private void uploadCsvToS3(TaxSummaryReportDTO reportData, Integer companyId, byte[] csvBytes) {
        try {
            String companyKeyPart = reportData.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("taxSummaryReport_%d_%d.csv",
                    companyId,
                    reportData.getTaxYear()
            );

            String key = String.format("%s/%d/taxSummaryReport/csv/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            amazonS3Service.uploadPdfToS3(csvBytes, key);
            log.info("Successfully uploaded Tax Summary Report CSV to S3 with key: {}", key);

        } catch (Exception e) {
            log.error("Failed to upload Tax Summary Report CSV to S3", e);
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