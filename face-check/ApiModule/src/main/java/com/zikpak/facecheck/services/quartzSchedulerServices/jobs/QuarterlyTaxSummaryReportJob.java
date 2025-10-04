package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class QuarterlyTaxSummaryReportJob extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TaxSummaryDataService taxSummaryDataService;

    @Autowired
    private TaxSummaryPdfService taxSummaryPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;





    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📋 Quarterly TaxSummaryScheduler запущен: генерируем quarterly tax summary reports");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        int completedQuarter;
        if (currentMonth == 1) {
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) {
            completedQuarter = 1;
        } else if (currentMonth == 7) {
            completedQuarter = 2;
        } else if (currentMonth == 10) {
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly scheduler. Текущий месяц: {}", currentMonth);
            return JobResult.success(0);
        }

        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем Tax Summary Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : allCompanies) {
            try {
                TaxSummaryReportDTO reportData = taxSummaryDataService
                        .generateTaxSummaryReport(company.getId(), startDate, endDate);

                taxSummaryPdfService.generateTaxSummaryReport(reportData);

                reportsMailSender.sendEmailTaxSummaryReport(company.getCompanyEmail());
                log.info("✅ Quarterly Tax Summary Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Tax Summary Report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
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
