package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.company.CompanyService;
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
public class QuarterlyFutaReportsJob  extends BaseSchedulerJob {

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
        log.info("📋 Quarterly FUTA Report Scheduler запущен: генерируем quarterly FUTA reports");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
        int completedQuarter;
        if (currentMonth == 1) {        // 15 января - закончился Q4 прошлого года
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) { // 15 апреля - закончился Q1
            completedQuarter = 1;
        } else if (currentMonth == 7) { // 15 июля - закончился Q2
            completedQuarter = 2;
        } else if (currentMonth == 10) { // 15 октября - закончился Q3
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly FUTA scheduler. Текущий месяц: {}", currentMonth);
            return JobResult.success(0);
        }

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем FUTA Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли payrolls за квартал
                boolean hasPayrollsInQuarter = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), startDate, endDate);

                if (hasPayrollsInQuarter) {
                    // Генерируем квартальный FUTA отчет
                    FutaReportDTO reportData = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, completedQuarter);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    // Отправляем email
                    reportsMailSender.sendEmailQuarterFUTAReport(
                            company.getCompanyEmail());

                    log.info("✅ Quarterly FUTA Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), completedQuarter, currentYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации quarterly FUTA report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
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
