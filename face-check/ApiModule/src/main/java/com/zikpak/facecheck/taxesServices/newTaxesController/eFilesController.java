package com.zikpak.facecheck.taxesServices.newTaxesController;


import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.ASCIIservices.EFW2GeneratorService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940ScheduleAXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940XmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941ScheduleBXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941XmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.csvReports.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("e-file")
@RequiredArgsConstructor
@Slf4j
public class eFilesController {

    private final Form941XmlGenerator form941XmlGenerator;
    private final Form941ScheduleBXmlGenerator scheduleBXmlGenerator;
    private final EFW2GeneratorService efw2GeneratorService;
    private final CompanyRepository companyRepository;
    private final AmazonS3Service amazonS3Service;

    private final HoursReportDataService hoursReportDataService;
    private final PayrollSummaryDataService payrollSummaryDataService;
    private final TaxSummaryDataService taxSummaryDataService;

    private final HoursReportCsvService hoursReportCsvService;
    private final PayrollSummaryReportCsvService payrollSummaryReportCsvService;
    private final TaxSummaryReportCsvService taxSummaryReportCsvService;

    private final Form940XmlGenerator form940XmlGenerator;
    private final Form940ScheduleAXmlGenerator generateForm940ScheduleAXml;


    @GetMapping("/{companyId}/xml")
    public ResponseEntity<String> generateXml(
            @PathVariable Integer companyId,
            @RequestParam Integer userId,
            @RequestParam int year,
            @RequestParam int quarter) {

        try {
            log.info("Generating Form 941 XML for company {}, user {}, {}-Q{}",
                    companyId, userId, year, quarter);

            // Generate XML using same logic as PDF service
            String xmlContent = form941XmlGenerator.generateForm941Xml(userId, companyId, year, quarter);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("Content-Disposition",
                    String.format("attachment; filename=Form941_Company%d_Q%d_%d.xml", companyId, quarter, year));

            log.info("Successfully generated XML, length: {} characters", xmlContent.length());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xmlContent);

        } catch (Exception e) {
            log.error("Error generating Form 941 XML for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating XML: " + e.getMessage());
        }
    }


    @GetMapping("/{companyId}/download")
    public ResponseEntity<byte[]> downloadXml(
            @PathVariable Integer companyId,
            @RequestParam Integer userId,
            @RequestParam int year,
            @RequestParam int quarter) {

        try {
            log.info("Downloading Form 941 XML for company {}, user {}, {}-Q{}",
                    companyId, userId, year, quarter);

            String xmlContent = form941XmlGenerator.generateForm941Xml(userId, companyId, year, quarter);
            byte[] xmlBytes = xmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    String.format("Form941_Company%d_Q%d_%d.xml", companyId, quarter, year));
            headers.setContentLength(xmlBytes.length);

            log.info("Successfully generated XML download, size: {} bytes", xmlBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xmlBytes);

        } catch (Exception e) {
            log.error("Error downloading Form 941 XML for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading XML: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/{companyId}/schedule-b")
    public ResponseEntity<String> generateScheduleBXml(
            @PathVariable Integer companyId,
            @RequestParam Integer userId,
            @RequestParam int year,
            @RequestParam int quarter) {

        try {
            log.info("Generating Form 941 Schedule B XML for company {}, user {}, {}-Q{}",
                    companyId, userId, year, quarter);

            // Generate Schedule B XML using same logic as PDF service
            String xmlContent = scheduleBXmlGenerator.generateForm941ScheduleBXml(userId, companyId, year, quarter);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("Content-Disposition",
                    String.format("attachment; filename=Form941ScheduleB_Company%d_Q%d_%d.xml", companyId, quarter, year));

            log.info("Successfully generated Schedule B XML, length: {} characters", xmlContent.length());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xmlContent);

        } catch (Exception e) {
            log.error("Error generating Form 941 Schedule B XML for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating Schedule B XML: " + e.getMessage());
        }
    }

    /**
     * Download Schedule B XML as file
     * GET /api/taxes/form941/{companyId}/download-schedule-b?userId=1&year=2025&quarter=1
     */
    @GetMapping("/{companyId}/download-schedule-b")
    public ResponseEntity<byte[]> downloadScheduleBXml(
            @PathVariable Integer companyId,
            @RequestParam Integer userId,
            @RequestParam int year,
            @RequestParam int quarter) {

        try {
            log.info("Downloading Form 941 Schedule B XML for company {}, user {}, {}-Q{}",
                    companyId, userId, year, quarter);

            String xmlContent = scheduleBXmlGenerator.generateForm941ScheduleBXml(userId, companyId, year, quarter);
            byte[] xmlBytes = xmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    String.format("Form941ScheduleB_Company%d_Q%d_%d.xml", companyId, quarter, year));
            headers.setContentLength(xmlBytes.length);

            log.info("Successfully generated Schedule B XML download, size: {} bytes", xmlBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xmlBytes);

        } catch (Exception e) {
            log.error("Error downloading Form 941 Schedule B XML for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading Schedule B XML: " + e.getMessage()).getBytes());
        }
    }


    /**
     * Generate EFW2 file and upload to S3
     * GET /taxes-forms/generate-efw2/{companyId}/{year}
     */
    @GetMapping("/generate-efw2/{companyId}/{year}")
    public ResponseEntity<byte[]> generateEfw2File(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            log.info("Generating EFW2 file for company {}, year {}", companyId, year);

            byte[] content = efw2GeneratorService.generateEfw2File(companyId, year);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=EFW2_" + companyId + "_" + year + ".txt");

            log.info("Successfully generated EFW2 file, size: {} bytes", content.length);

            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error generating EFW2 file for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download EFW2 file from S3
     * GET /taxes-forms/download-efw2/{companyId}/{year}
     */
    @GetMapping("/download-efw2/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadEfw2FromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("EFW2_%d_%d.txt", companyId, year);

            String key = String.format("%s/%d/efw2/%d/%s",
                    companyKeyPart,
                    companyId,
                    year,
                    fileName
            );

            log.info("Downloading EFW2 file from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading EFW2 file for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("EFW2 file not found: " + e.getMessage()).getBytes());
        }
    }





    @GetMapping("/summary/hoursReport/{companyId}/csv")
    public ResponseEntity<byte[]> generateHoursReportCsv(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            log.info("Generating Hours Report CSV for company {}, period {} to {}",
                    companyId, startDate, endDate);

            // 1. Get report data using existing service
            HoursReportDTO reportData = hoursReportDataService.generateHoursReportData(companyId, startDate, endDate);

            // 2. Generate CSV using business logic service
            byte[] csvBytes = hoursReportCsvService.generateHoursReportCsv(reportData, companyId);

            // 3. Return CSV file
            String filename = String.format("Hours_Report_%s_to_%s.csv",
                    startDate.toString(), endDate.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvBytes);

        } catch (Exception e) {
            log.error("Error generating Hours Report CSV for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating CSV: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Hours Report CSV from S3
     * GET /taxes-forms/download-hoursReport-csv/{companyId}/{year}
     */
    @GetMapping("/download-hoursReport-csv/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadHoursReportCsvFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("hoursReport_%d_%d.csv", companyId, year);

            String key = String.format("%s/%d/hoursReport/csv/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            log.info("Downloading Hours Report CSV from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Hours Report CSV for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Hours Report CSV not found: " + e.getMessage()).getBytes());
        }
    }


    @GetMapping("/summary/payrollReport/{companyId}/csv")
    public ResponseEntity<byte[]> generatePayrollSummaryReportCsv(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            log.info("Generating Payroll Summary Report CSV for company {}, period {} to {}",
                    companyId, startDate, endDate);

            // 1. Get report data using existing service
            PayrollSummaryReportDTO reportData = payrollSummaryDataService.generatePayrollSummaryData(companyId, startDate, endDate);

            // 2. Generate CSV using business logic service
            byte[] csvBytes = payrollSummaryReportCsvService.generatePayrollSummaryReportCsv(reportData, companyId);

            // 3. Return CSV file
            String filename = String.format("Payroll_Summary_Report_%s_to_%s.csv",
                    startDate.toString(), endDate.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvBytes);

        } catch (Exception e) {
            log.error("Error generating Payroll Summary Report CSV for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating CSV: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Payroll Summary Report CSV from S3
     * GET /taxes-forms/download-payrollSummaryReport-csv/{companyId}/{year}
     */
    @GetMapping("/download-payrollSummaryReport-csv/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadPayrollSummaryReportCsvFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("payrollSummaryReport_%d_%d.csv", companyId, year);

            String key = String.format("%s/%d/payrollReport/csv/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            log.info("Downloading Payroll Summary Report CSV from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Payroll Summary Report CSV for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Payroll Summary Report CSV not found: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate and download Tax Summary Report CSV
     * GET /taxes-forms/summary/taxSummaryReport/{companyId}/csv
     */
    @GetMapping("/summary/taxSummaryReport/{companyId}/csv")
    public ResponseEntity<byte[]> generateTaxSummaryReportCsv(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            log.info("Generating Tax Summary Report CSV for company {}, period {} to {}",
                    companyId, startDate, endDate);

            // 1. Get report data using existing service
            TaxSummaryReportDTO reportData = taxSummaryDataService.generateTaxSummaryReport(companyId, startDate, endDate);

            // 2. Generate CSV using business logic service
            byte[] csvBytes = taxSummaryReportCsvService.generateTaxSummaryReportCsv(reportData, companyId);

            // 3. Return CSV file
            String filename = String.format("Tax_Summary_Report_%s_to_%s.csv",
                    startDate.toString(), endDate.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvBytes);

        } catch (Exception e) {
            log.error("Error generating Tax Summary Report CSV for company {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating CSV: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Tax Summary Report CSV from S3
     * GET /taxes-forms/download-taxSummaryReport-csv/{companyId}/{year}
     */
    @GetMapping("/download-taxSummaryReport-csv/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadTaxSummaryReportCsvFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("taxSummaryReport_%d_%d.csv", companyId, year);

            String key = String.format("%s/%d/taxSummaryReport/csv/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            log.info("Downloading Tax Summary Report CSV from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Tax Summary Report CSV for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Tax Summary Report CSV not found: " + e.getMessage()).getBytes());
        }
    }

    // ========================================================================
// ДОБАВЬ ЭТИ ENDPOINTS В ТВОЙ КОНТРОЛЛЕР
// ========================================================================

// Сначала добавь dependency injection:
// private final Form940XmlGenerator form940XmlGenerator;
// private final Form940ScheduleAXmlGenerator form940ScheduleAXmlGenerator;

// ========================================================================
// FORM 940 XML ENDPOINTS
// ========================================================================

    @GetMapping("/generate-940-xml/{userId}/{companyId}/{year}")
    public ResponseEntity<byte[]> generateForm940Xml(
            @PathVariable Integer userId,
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            log.info("Generating Form 940 XML e-file for company {}, year {}", companyId, year);

            String xmlContent = form940XmlGenerator.generateForm940Xml(userId, companyId, year);

            String filename = String.format("Form940_%d_%d.xml", companyId, year);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(xmlContent.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Error generating Form 940 XML for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating Form 940 XML: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Form 940 XML from S3
     * GET /download-940-xml/{companyId}/{year}
     */
    @GetMapping("/download-940-xml/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadForm940XmlFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("f940_%d_%d.xml", companyId, year);

            String key = String.format("%s/%d/940form/940Xml/%d/%s",
                    companyKeyPart, companyId, year, fileName);

            log.info("Downloading Form 940 XML from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Form 940 XML for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Form 940 XML not found: " + e.getMessage()).getBytes());
        }
    }

// ========================================================================
// FORM 940 SCHEDULE A XML ENDPOINTS
// ========================================================================

    /**
     * Generate Form 940 Schedule A XML e-file
     * GET /generate-940sa-xml/{userId}/{companyId}/{year}
     */
    @GetMapping("/generate-940sa-xml/{userId}/{companyId}/{year}")
    public ResponseEntity<byte[]> generateForm940ScheduleAXml(
            @PathVariable Integer userId,
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            log.info("Generating Form 940 Schedule A XML e-file for company {}, year {}", companyId, year);

            String xmlContent = generateForm940ScheduleAXml.generateForm940ScheduleAXml(userId, companyId, year);

            if (xmlContent.isEmpty()) {
                return ResponseEntity.ok()
                        .body("Schedule A not required for this company".getBytes());
            }

            String filename = String.format("Form940ScheduleA_%d_%d.xml", companyId, year);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(xmlContent.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Error generating Form 940 Schedule A XML for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating Form 940 Schedule A XML: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Form 940 Schedule A XML from S3
     * GET /download-940sa-xml/{companyId}/{year}
     */
    @GetMapping("/download-940sa-xml/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadForm940ScheduleAXmlFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("f940sa_%d_%d.xml", companyId, year);

            String key = String.format("%s/%d/940saform/940saXml/%d/%s",
                    companyKeyPart, companyId, year, fileName);

            log.info("Downloading Form 940 Schedule A XML from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Form 940 Schedule A XML for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Form 940 Schedule A XML not found: " + e.getMessage()).getBytes());
        }
    }
}