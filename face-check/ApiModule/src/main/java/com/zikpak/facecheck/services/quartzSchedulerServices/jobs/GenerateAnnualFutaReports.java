package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class GenerateAnnualFutaReports extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private FutaReportService futaReportService;

    @Autowired
    private FutaReportPdfService futaReportPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📄 Annual FUTA Report Scheduler запущен: генерируем annual FUTA reports за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Годовой отчет генерируем за прошлый год

        log.info("📅 Генерируем Annual FUTA Reports за {} год", previousYear);

        // Даты за весь прошлый год
        LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();


        for (Company company : allCompanies) {
            try {
                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (hasPayrollsInYear) {
                    FutaReportDTO reportData = futaReportService.generateAnnualFutaReport(
                            company.getId(), previousYear);

                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    reportsMailSender.sendEmailAnnualFutaReport(
                            company.getCompanyEmail());

                    log.info("✅ Annual FUTA Report сгенерен и отправлен для компании: {} (ID: {}) за {} год, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), previousYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за {} год",
                            company.getCompanyName(), previousYear);
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации annual FUTA report для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                errorCount++;
            }
        }

        if (errorCount == 0 ) {
            return JobResult.success(successCount);
        } else if (successCount > 0) {
            return JobResult.partialSuccess(successCount, errorCount, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }
}
