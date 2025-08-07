package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j

public class CheckQuarterlyFutaComplienceJob extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private FutaReportService futaReportService;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("⚠️ FUTA Compliance Check Scheduler запущен: проверяем compliance и дедлайны");

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        // Определяем текущий квартал и следующий дедлайн
        int currentQuarter;
        LocalDate nextDeadline;

        if (currentMonth >= 1 && currentMonth <= 3) {
            currentQuarter = 1;
            nextDeadline = LocalDate.of(currentYear, 4, 30); // Q1 deadline
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            currentQuarter = 2;
            nextDeadline = LocalDate.of(currentYear, 7, 31); // Q2 deadline
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            currentQuarter = 3;
            nextDeadline = LocalDate.of(currentYear, 10, 31); // Q3 deadline
        } else {
            currentQuarter = 4;
            nextDeadline = LocalDate.of(currentYear + 1, 1, 31); // Q4 deadline
        }

        // Проверяем только если до дедлайна осталось меньше 2 недель
        long daysUntilDeadline = today.until(nextDeadline).getDays();
        int complianceIssues = 0;
        int checkErrors = 0;
        int successTotal = 0;
        StringBuilder errors = new StringBuilder();
        if (daysUntilDeadline <= 14 && daysUntilDeadline > 0) {
            log.info("⚠️ До FUTA дедлайна осталось {} дней. Проверяем compliance для Q{} {}",
                    daysUntilDeadline, currentQuarter, currentYear);

            // Даты текущего квартала
            LocalDate quarterStart = LocalDate.of(currentYear, (currentQuarter - 1) * 3 + 1, 1);
            LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

            // Получаем только компании с payrolls в текущем квартале
            List<Company> companiesWithPayrolls = companyRepository.findAll().stream()
                    .filter(company -> workerPayrollRepository
                            .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), quarterStart, quarterEnd))
                    .toList();


            for (Company company : companiesWithPayrolls) {
                try {
                    FutaReportDTO currentReport = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, currentQuarter);
                    successTotal++;
                    if (!currentReport.getComplianceStatus() || currentReport.getNeedsPayment()) {
                        complianceIssues++;

                        log.warn("⚠️ COMPLIANCE ALERT: Компания {} (ID: {}) требует внимания по FUTA за Q{} {}. " +
                                        "Compliance: {}, Needs Payment: {}, Remaining Liability: ${}",
                                company.getCompanyName(), company.getId(), currentQuarter, currentYear,
                                currentReport.getComplianceStatus(), currentReport.getNeedsPayment(),
                                currentReport.getRemainingFutaLiability());

                        reportsMailSender.sendEmailFutaCompliance(company.getCompanyEmail());
                    }

                } catch (Exception ex) {
                    errors.append("Company ID ").append(company.getId())
                            .append(": ").append(ex.getMessage()).append("\n");
                    log.error("❌ Ошибка проверки FUTA compliance для компании ID: {}", company.getId(), ex);
                    checkErrors++;
                }
            }

        } else if (daysUntilDeadline <= 0) {
            log.warn("🚨 FUTA дедлайн просрочен на {} дней!", Math.abs(daysUntilDeadline));

        } else {
            log.info("✅ До FUTA дедлайна еще {} дней. Проверка не требуется.", daysUntilDeadline);
        }
        if (checkErrors == 0 ) {
            return JobResult.success(successTotal);
        } else if (successTotal > 0) {
            return JobResult.partialSuccess(successTotal, checkErrors, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }

}



