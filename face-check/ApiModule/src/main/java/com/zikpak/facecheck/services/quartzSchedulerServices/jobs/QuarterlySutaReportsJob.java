package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class QuarterlySutaReportsJob extends BaseSchedulerJob {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private SutaReportService sutaReportService;
    @Autowired
    private SutaReportPdfService sutaReportPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📋 Quarterly SUTA Report Scheduler запущен: генерируем quarterly SUTA reports");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
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
            log.info("ℹ️ Ошибка в логике quarterly SUTA scheduler. Текущий месяц: {}", currentMonth);
            return JobResult.success(0);
        }

        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем SUTA Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        List<Company> nyCompanies = companyRepository.findAll().stream()
                .filter(company -> "NY".equals(company.getCompanyState()))
                .toList();

        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : nyCompanies) {
            try {
                // Проверяем, есть ли payrolls за квартал
                boolean hasPayrollsInQuarter = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), startDate, endDate);

                if (hasPayrollsInQuarter) {
                    // Генерируем квартальный SUTA отчет
                    SutaReportDTO reportData = sutaReportService.generateQuarterlySutaReport(
                            company.getId(), currentYear, completedQuarter);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = sutaReportPdfService.generateSutaReportPdf(reportData);

                    // Отправляем email
                    reportsMailSender.sendEmailQuarterSutaForm(
                            company.getCompanyEmail()
                    );

                    log.info("✅ Quarterly SUTA Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), completedQuarter, currentYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);

                }

            } catch (Exception ex) {
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Ошибка генерации quarterly SUTA report для компании ID: {} за Q{} {}",
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
