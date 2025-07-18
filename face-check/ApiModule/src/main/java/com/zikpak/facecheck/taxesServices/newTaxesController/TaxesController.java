package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.ASCIIservices.EFW2GeneratorService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.dto.PayStubFileDTO;
import com.zikpak.facecheck.taxesServices.pdfServices.*;
import com.zikpak.facecheck.taxesServices.services.CompanyPayrollService;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("taxes-forms")
@RequiredArgsConstructor
@Slf4j
public class TaxesController {
    private final AmazonS3Service amazonS3Service;
    private final CompanyPayrollService companyPayrollService;
    private final PayStubService payStubService;
    private final WorkerPayRollService workerPayRollService;
    private final FillForm941 fillForm941;
    private final FillForm941ScheduleB fillForm941ScheduleB;
    private final FillFormW4 fillFormW4;
    private final Form940PdfGeneratorService form940PdfGeneratorService;
    private final FillForm1095B fillForm1095B;
    private final FillForm1094C fillForm1094C;
    private final FillForm1095C fillForm1095C;
    private final FillForm1040 fillForm1040;
    private final FillForm941X fillForm941X;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final PayrollSummaryDataService payrollSummaryDataService;
    private final PayrollSummaryReportService payrollSummaryReportService;

    private final HoursReportDataService hoursDataService;
    private final HoursReportPdfService hoursReportService;

    private final TaxSummaryPdfService taxSummaryPdfService;
    private final TaxSummaryDataService taxSummaryDataService;

    private final FillForm940SA fillForm940SA;
    private final FutaReportService futaReportService;
    private final FutaReportPdfService futaReportPdfService;
    private final SutaReportService sutaReportService;
    private final SutaReportPdfService sutaReportPdfService;

    private final FillFormI9 fillFormI9;


    // Uploading ✅
    @GetMapping("/generate-w3/{companyId}")
    public ResponseEntity<byte[]> generateW3Pdf(@PathVariable Integer companyId,
                                                @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var pdfContent = companyPayrollService.generatePdfW3(companyId, year);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w3_summary_" + companyId + ".pdf")
                .body(pdfContent);
    }
    // Uploading ✅
    @GetMapping("/generate/{payrollId}")
    public ResponseEntity<byte[]> generatePayStub(@PathVariable Integer payrollId) {
        byte[] pdf = payStubService.generatePayStubPdf(payrollId);

        WorkerPayroll payroll = workerPayrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        String filename = String.format("PayStub_%s_%s_to_%s.pdf",
                payroll.getWorker().getFirstName() + "_" + payroll.getWorker().getLastName(),
                payroll.getPeriodStart().toString(),
                payroll.getPeriodEnd().toString()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }




    @GetMapping("/worker/{workerId}/year/{year}")
    public WorkerYearlySummaryDto getWorkerYearlySummary(
            @PathVariable Integer workerId,
            @PathVariable int year
    ) {
        return workerPayRollService.calculateWorkerYearlyTotals(workerId, year);
    }

    // Uploading ✅
    @GetMapping("/generate-w2/{workerId}")
    public ResponseEntity<byte[]> generateW2Pdf(@PathVariable Integer workerId,
                                                @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var pdfContent = workerPayRollService.generatePDF(workerId, year);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w2_statement_" + workerId + ".pdf")
                .body(pdfContent);
    }

    // Uploading ✅
    @GetMapping("/generate-941-pdf/{userId}/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> generateForm941Pdf(
            @PathVariable Integer userId,
            @PathVariable Integer companyId,
            @PathVariable int year,
            @PathVariable int quarter) throws IOException {

        byte[] pdfBytes = fillForm941.generateFilledPdf(userId, companyId, year, quarter);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=f941_" + companyId + "_" + year + "_" + quarter + ".pdf")
                .body(pdfBytes);
    }



    @GetMapping("/generate-940/{companyId}/{year}")
    public ResponseEntity<byte[]> generateForm940(
            @PathVariable Integer companyId,
            @PathVariable int year) throws IOException {
        byte[] pdfBytes = form940PdfGeneratorService.generate940Pdf(companyId, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Form940_" + companyId + "_" + year + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/generate-940-sa/{companyId}/{year}")
    public ResponseEntity<byte[]> generateForm940sa(
            @PathVariable Integer companyId,
            @PathVariable int year) throws IOException {
        byte[] pdfBytes = fillForm940SA.generateFilledPdf(companyId, year);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Form940SA_" + companyId + "_" + year + ".pdf")
                .body(pdfBytes);
    }

    // Uploading ✅
    @GetMapping("/generate-941sb-pdf/{userId}/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> generateForm941sbPdf(
            @PathVariable Integer userId,
            @PathVariable Integer companyId,
            @PathVariable int year,
            @PathVariable int quarter) throws IOException {

        byte[] pdfBytes = fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=f941sb_" + companyId + "_" + year + "_Q" + quarter + ".pdf")
                .body(pdfBytes);
    }


    // Uploading ✅
    @GetMapping("/generateW4pdf/{userId}/company/{companyId}")
    public ResponseEntity<byte[]> generateFormW4Pdf(@PathVariable("userId") Integer userId,
                                                    @PathVariable("companyId") Integer companyId) throws IOException {


        byte[] pdf = fillFormW4.generateW4Pdf(userId, companyId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_W4_output_" + companyId + ".pdf")
                .body(pdf);
    }


        //TODO Implement in future when i will have clients, and i will working from home! Xd hope it will be soon!
    @GetMapping("/generate1094c_pdf")
    public ResponseEntity<byte[]> generateForm1094CPdf() throws IOException {

        fillForm1094C.generateFilledPdf();

        File file = new File("filled_f1094C_output.pdf");
        byte[] pdfBytes = new FileInputStream(file).readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=filled_f1094C_output.pdf")
                .body(pdfBytes);
    }



    @GetMapping("/generate1095c_pdf/{companyId}/{userId}/{reportYear}")
    public ResponseEntity<byte[]> generateForm1095CPdf(
            @PathVariable("userId") Integer userId,
            @PathVariable("companyId") Integer companyId,
            @PathVariable("reportYear")int year
    ) throws IOException {
        fillForm1095C.generateFilledPdf(companyId, userId, year);
        File file = new File("filled_f1095C_output.pdf");
        byte[] pdfBytes = new FileInputStream(file).readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=f1095C_output.pdf")
                .body(pdfBytes);
    }



    @GetMapping("/generate1095b_pdf")
    public ResponseEntity<byte[]> generateForm1095BPdf() throws IOException {

        fillForm1095B.generateFilledPdf();
        File file = new File("filled_f1095B_output.pdf");
        byte[] pdfBytes = new FileInputStream(file).readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=f1095B_output.pdf")
                .body(pdfBytes);
    }



    @GetMapping("/generate1040_pdf")
    public ResponseEntity<byte[]> generateForm1040Pdf() throws IOException {

        fillForm1040.generateFilledPdf();
        File file = new File("filled_f1040_output.pdf");
        byte[] pdfBytes = new FileInputStream(file).readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=f1040_output.pdf")
                .body(pdfBytes);
    }



    @GetMapping("/generate941x_pdf")
    public ResponseEntity<byte[]> generateForm941xPdf() throws IOException {

        fillForm941X.generateFilledPdf();
        File file = new File("filled_f941x_output.pdf");
        byte[] pdfBytes = new FileInputStream(file).readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=filled_f941x_output.pdf")
                .body(pdfBytes);
    }



//-----------------------------------------------------------------------------------------------------------
  //Endpoints for finding all this forms!

    /** W-2 download */
    @GetMapping("/download-w2/{companyId}/{workerId}")
    public ResponseEntity<byte[]> downloadW2FromS3(
            @PathVariable Integer companyId,
            @PathVariable Integer workerId,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String key = String.format(
                "%s/%d/w2-statements/%d/w2_%d.pdf",
                companyKeyPart, companyId, year, workerId
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"w2_statement_" + workerId + "_" + year + ".pdf\"")
                .body(fileDTO.data());
    }

    /** W-3 download */
    @GetMapping("/download-w3/{companyId}")
    public ResponseEntity<byte[]> downloadW3FromS3(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String key = String.format(
                "%s/%d/w3-summary/%d/w3_%d.pdf",
                companyKeyPart, companyId, year, companyId
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"w3_summary_" + companyId + "_" + year + ".pdf\"")
                .body(fileDTO.data());
    }

    /** W-4 download */
    @GetMapping("/download-w4/{companyId}/{userId}")
    public ResponseEntity<byte[]> downloadW4Form(
            @PathVariable Integer companyId,
            @PathVariable Integer userId) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        var worker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String workerKeyPart = (worker.getFirstName() + "_" + worker.getLastName())
                .trim()
                .replaceAll("[^A-Za-z0-9_]+", "_");
        String key = String.format(
                "%s/%d/W4/%s.pdf",
                companyKeyPart, companyId, workerKeyPart
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"W4_" + workerKeyPart + ".pdf\"")
                .body(fileDTO.data());
    }



    /** Paystub download */
    @GetMapping("/download-paystub/{payrollId}")
    public ResponseEntity<byte[]> downloadPayStub(@PathVariable Integer payrollId) {
        var payroll = workerPayrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
        var worker  = payroll.getWorker();
        var company = worker.getCompany();

        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String workerKeyPart = (worker.getFirstName() + "_" + worker.getLastName() + "_" + worker.getId())
                .trim()
                .replaceAll("[^A-Za-z0-9_]+", "_");
        String periodPart = payroll.getPeriodStart().toString()
                + "_" + payroll.getPeriodEnd().toString();

        String key = String.format(
                "%s/%d/paystubs/%s/%s/%d.pdf",
                companyKeyPart, company.getId(), workerKeyPart, periodPart, payrollId
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

        String downloadFilename = String.format("PayStub_%s_%s_to_%s.pdf",
                worker.getFirstName() + "_" + worker.getLastName(),
                payroll.getPeriodStart().toString(),
                payroll.getPeriodEnd().toString()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadFilename + "\"")
                .body(fileDTO.data());
    }



    /** Form 940 download */
    @GetMapping("/download-940/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadForm940(
            @PathVariable Integer companyId,
            @PathVariable int year) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String key = String.format(
                "%s/%d/940pdf/f940_%d_%d.pdf",
                companyKeyPart, companyId, companyId, year
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"Form940_" + companyId + "_" + year + ".pdf\"")
                .body(fileDTO.data());
    }

    @GetMapping("/download-940sa/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadForm940SAFromS3(
            @PathVariable Integer companyId,
            @PathVariable int year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("f940SA_%d_%d.pdf", companyId, year);

            String key = String.format("%s/%d/940SApdf/%s",
                    companyKeyPart,
                    companyId,
                    fileName
            );

            log.info("Downloading Form 940 Schedule A from S3 with key: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("Error downloading Form 940 Schedule A for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Form 940 Schedule A not found: " + e.getMessage()).getBytes());
        }
    }



    /** Form 941 PDF download */
    @GetMapping("/download-941-pdf/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> download941Pdf(
            @PathVariable Integer companyId,
            @PathVariable int year,
            @PathVariable int quarter) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String fileName = String.format("f941_%d_%d_%d.pdf",
                companyId, year, quarter);
        String key = String.format(
                "%s/%d/e-file/941Pdf/%d/%d/%s",
                companyKeyPart, companyId, year, quarter, fileName
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(fileDTO.data());
    }

    /** Form 941-SB PDF download */
    @GetMapping("/download-941sb-pdf/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> download941SbPdf(
            @PathVariable Integer companyId,
            @PathVariable int year,
            @PathVariable int quarter) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");
        String fileName = String.format("f941sb_%d_%d_%d.pdf",
                companyId, year, quarter);
        String key = String.format(
                "%s/%d/941sbform/941Pdf/%d/%d/%s",
                companyKeyPart, companyId, year, quarter, fileName
        );
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(fileDTO.data());
    }



//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                // New report methods!


    @GetMapping("/summary/{companyId}")
    public ResponseEntity<byte[]> generatePayrollSummaryReport(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // 1. Собираем данные
            PayrollSummaryReportDTO reportData = payrollSummaryDataService
                    .generatePayrollSummaryData(companyId, startDate, endDate);

            // 2. Генерируем PDF
            byte[] pdfBytes = payrollSummaryReportService.generatePayrollSummaryReport(reportData);

            // 3. Возвращаем PDF
            String filename = String.format("Payroll_Summary_%s_to_%s.pdf",
                    startDate.toString(), endDate.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating payroll summary report", e);
        }
    }

    @GetMapping("/summary/hoursReport/{companyId}")
    public ResponseEntity<byte[]> generateHoursReport(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // 1. Собираем данные о часах
            HoursReportDTO reportData = hoursDataService
                    .generateHoursReportData(companyId, startDate, endDate);

            // 2. Генерируем PDF с S3 сохранением
            byte[] pdfBytes = hoursReportService.generateHoursReport(reportData, companyId);

            // 3. Возвращаем PDF
            String filename = String.format("Hours_Report_%s_to_%s.pdf",
                    startDate.toString(), endDate.toString());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating hours report", e);
        }
    }


    @GetMapping("/paystubs/company/{companyId}")
    public ResponseEntity<List<PayStubFileDTO>> getCompanyPayStubFiles(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PayStubFileDTO> payStubFiles = payStubService.getPayStubFilesList(companyId, startDate, endDate);
        return ResponseEntity.ok(payStubFiles);
    }

    /**
     * Получить список paystub файлов для конкретного сотрудника
     */
    @GetMapping("/paystubs/company/{companyId}/worker/{workerId}")
    public ResponseEntity<List<PayStubFileDTO>> getWorkerPayStubFiles(
            @PathVariable Integer companyId,
            @PathVariable Integer workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PayStubFileDTO> payStubFiles = payStubService.getWorkerPayStubFilesList(companyId, workerId, startDate, endDate);
        return ResponseEntity.ok(payStubFiles);
    }

    /**
     * Получить группированные по сотрудникам paystub файлы (для папочной структуры)
     */
    @GetMapping("/paystubs/company/{companyId}/grouped")
    public ResponseEntity<Map<String, List<PayStubFileDTO>>> getGroupedPayStubFiles(
            @PathVariable Integer companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, List<PayStubFileDTO>> groupedFiles = payStubService.getGroupedPayStubFilesList(companyId, startDate, endDate);
        return ResponseEntity.ok(groupedFiles);
    }





    @GetMapping("/generate-tax-summary-quarterly/{companyId}/{year}/{quarter}")
    @Operation(summary = "Generate Quarterly Tax Summary Report PDF",
            description = "Generate and download quarterly tax summary report PDF")
    public ResponseEntity<byte[]> generateQuarterlyTaxSummaryPdf(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter) throws Exception {

        if (quarter < 1 || quarter > 4) {
            return ResponseEntity.badRequest().build();
        }

        // Calculate quarter dates
        LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("Generating Quarterly Tax Summary PDF for company: {}, Q{} {}",
                companyId, quarter, year);

        // Generate report data
        TaxSummaryReportDTO reportData = taxSummaryDataService
                .generateTaxSummaryReport(companyId, startDate, endDate);

        // Generate PDF
        byte[] pdfBytes = taxSummaryPdfService.generateTaxSummaryReport(reportData);

        String fileName = String.format("TaxSummaryReport_Q%d_%d_%s.pdf",
                quarter,
                year,
                reportData.getCompanyName().replaceAll("[^A-Za-z0-9]+", "_")
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(pdfBytes);
    }

    @GetMapping("/download-tax-summary-quarterly/{companyId}/{year}/{quarter}")
    @Operation(summary = "Download Quarterly Tax Summary PDF from S3",
            description = "Download existing quarterly tax summary report PDF from S3")
    public ResponseEntity<byte[]> downloadQuarterlyTaxSummaryPdf(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter) {

        if (quarter < 1 || quarter > 4) {
            return ResponseEntity.badRequest().build();
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]+", "_");

        String fileName = String.format("taxSummaryReport_%d_%d.pdf", companyId, year);

        String key = String.format("%s/%d/taxSummaryReport/%s",
                companyKeyPart, companyId, fileName);

        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

        String downloadFileName = String.format("TaxSummaryReport_Q%d_%d_%s.pdf",
                quarter, year, companyKeyPart);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .body(fileDTO.data());
    }



    @GetMapping("/generate-quarterly/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> generateQuarterlyFutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter) {

        try {
            log.info("🔄 Generating quarterly FUTA report for company {} Q{} {}", companyId, quarter, year);

            // Генерируем данные отчета
            FutaReportDTO reportData = futaReportService.generateQuarterlyFutaReport(companyId, year, quarter);

            // Генерируем PDF (автоматически сохраняется в S3)
            byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

            String fileName = String.format("FUTA_Report_Q%d_%d_%s.pdf",
                    quarter, year, reportData.getCompanyName().replaceAll("[^A-Za-z0-9]+", "_"));

            log.info("✅ Quarterly FUTA report generated successfully: {} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("❌ Error generating quarterly FUTA report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating quarterly FUTA report: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 2. ГЕНЕРИРУЕТ годовой FUTA отчет PDF
     */
    @GetMapping("/generate-annual/{companyId}/{year}")
    public ResponseEntity<byte[]> generateAnnualFutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year) {

        try {
            log.info("🔄 Generating annual FUTA report for company {} year {}", companyId, year);

            // Генерируем данные отчета
            FutaReportDTO reportData = futaReportService.generateAnnualFutaReport(companyId, year);

            // Генерируем PDF (автоматически сохраняется в S3)
            byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

            String fileName = String.format("FUTA_Report_Annual_%d_%s.pdf",
                    year, reportData.getCompanyName().replaceAll("[^A-Za-z0-9]+", "_"));

            log.info("✅ Annual FUTA report generated successfully: {} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("❌ Error generating annual FUTA report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating annual FUTA report: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 3. СКАЧИВАЕТ квартальный FUTA отчет из S3
     */
    @GetMapping("/download-quarterly/{companyId}/{year}/{quarter}")
    public ResponseEntity<byte[]> downloadQuarterlyFutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("futaReport_Q%d_%d_%d.pdf", quarter, year, companyId);

            String key = String.format("%s/%d/futaReports/%s",
                    companyKeyPart, companyId, fileName);

            log.info("📥 Downloading quarterly FUTA report from S3: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            String downloadFileName = String.format("FUTA_Report_Q%d_%d_%s.pdf",
                    quarter, year, companyKeyPart);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("❌ Error downloading quarterly FUTA report for company {}, Q{} {}", companyId, quarter, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Quarterly FUTA report not found: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 4. СКАЧИВАЕТ годовой FUTA отчет из S3
     */
    @GetMapping("/download-annual/{companyId}/{year}")
    public ResponseEntity<byte[]> downloadAnnualFutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year) {

        try {
            var company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("futaReport_Annual_%d_%d.pdf", year, companyId);

            String key = String.format("%s/%d/futaReports/%s",
                    companyKeyPart, companyId, fileName);

            log.info("📥 Downloading annual FUTA report from S3: {}", key);

            S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(key);

            String downloadFileName = String.format("FUTA_Report_Annual_%d_%s.pdf",
                    year, companyKeyPart);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                    .body(fileDTO.data());

        } catch (Exception e) {
            log.error("❌ Error downloading annual FUTA report for company {}, year {}", companyId, year, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Annual FUTA report not found: " + e.getMessage()).getBytes());
        }
    }


    @GetMapping("/quarterly/{companyId}/{year}/{quarter}")
    public ResponseEntity<SutaReportDTO> generateQuarterlySutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter,
            Authentication authentication) {

        log.info("📊 Generating quarterly SUTA report for company {} Q{} {}", companyId, quarter, year);

        SutaReportDTO report = sutaReportService.generateQuarterlySutaReport(companyId, year, quarter);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/annual/{companyId}/{year}")
    public ResponseEntity<SutaReportDTO> generateAnnualSutaReport(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            Authentication authentication) {

        log.info("📊 Generating annual SUTA report for company {} year {}", companyId, year);

        SutaReportDTO report = sutaReportService.generateAnnualSutaReport(companyId, year);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/quarterly/{companyId}/{year}/{quarter}/pdf")
    public ResponseEntity<byte[]> downloadQuarterlySutaReportPdf(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            @PathVariable Integer quarter,
            Authentication authentication) {

        log.info("📄 Downloading quarterly SUTA PDF for company {} Q{} {}", companyId, quarter, year);

        SutaReportDTO report = sutaReportService.generateQuarterlySutaReport(companyId, year, quarter);
        byte[] pdfBytes = sutaReportPdfService.generateSutaReportPdf(report);

        String filename = String.format("SUTA_Report_Q%d_%d_Company_%d.pdf", quarter, year, companyId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/annual/{companyId}/{year}/pdf")
    public ResponseEntity<byte[]> downloadAnnualSutaReportPdf(
            @PathVariable Integer companyId,
            @PathVariable Integer year,
            Authentication authentication) {

        log.info("📄 Downloading annual SUTA PDF for company {} year {}", companyId, year);

        SutaReportDTO report = sutaReportService.generateAnnualSutaReport(companyId, year);
        byte[] pdfBytes = sutaReportPdfService.generateSutaReportPdf(report);

        String filename = String.format("SUTA_Report_Annual_%d_Company_%d.pdf", year, companyId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @GetMapping(value = "/i9/{userId}/{companyId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadI9(@PathVariable("userId") Integer userId, @PathVariable("companyId") Integer companyId) throws IOException {
        byte[] pdfBytes = fillFormI9.generateFilledPdf(userId, companyId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""  + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }







}
