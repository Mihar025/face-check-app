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
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FutaYearEndReportJob extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private FutaReportService futaReportService;

    @Autowired
    private FutaReportPdfService futaReportPdfService;

    @Autowired
    private SutaReportPdfService sutaReportPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;



    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {

        log.info("📋 FUTA Year-End Preparation Scheduler запущен: подготавливаем данные к концу года");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        log.info("📅 Подготавливаем FUTA данные за {} год к концу года", currentYear);

        // Даты всего года
        LocalDate yearStart = LocalDate.of(currentYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(currentYear, 12, 31);

        // Получаем только компании с payrolls в текущем году
        List<Company> companiesWithPayrolls = companyRepository.findAll().stream()
                .filter(company -> workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd))
                .toList();
        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : companiesWithPayrolls) {
            try {
                log.info("🔍 Проверяем все квартальные FUTA отчеты для компании: {} (ID: {})",
                        company.getCompanyName(), company.getId());

                // Проверяем все 4 квартала текущего года
                BigDecimal totalAnnualLiability = BigDecimal.ZERO;
                BigDecimal totalAnnualPaid = BigDecimal.ZERO;
                List<String> missingQuarters = new ArrayList<>();
                boolean hasAnyIssues = false;



                for (int quarter = 1; quarter <= 4; quarter++) {
                    try {
                        // Проверяем есть ли данные за квартал
                        LocalDate quarterStart = LocalDate.of(currentYear, (quarter - 1) * 3 + 1, 1);
                        LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

                        if (workerPayrollRepository.existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                                company.getId(), quarterStart, quarterEnd)) {

                            FutaReportDTO quarterlyReport = futaReportService.generateQuarterlyFutaReport(
                                    company.getId(), currentYear, quarter);

                            totalAnnualLiability = totalAnnualLiability.add(quarterlyReport.getTotalFutaTaxOwed());
                            totalAnnualPaid = totalAnnualPaid.add(quarterlyReport.getTotalFutaTaxPaid());

                            if (!quarterlyReport.getComplianceStatus()) {
                                missingQuarters.add("Q" + quarter);
                                hasAnyIssues = true;
                            }

                            log.info("✅ Q{} {}: Owed=${}, Paid=${}, Compliant={}",
                                    quarter, currentYear,
                                    quarterlyReport.getTotalFutaTaxOwed(),
                                    quarterlyReport.getTotalFutaTaxPaid(),
                                    quarterlyReport.getComplianceStatus());
                        }

                    } catch (Exception ex) {
                        log.error("❌ Ошибка проверки Q{} для компании {}", quarter, company.getId(), ex);
                        missingQuarters.add("Q" + quarter + " (ERROR)");
                        hasAnyIssues = true;
                    }
                }

                BigDecimal remainingLiability = totalAnnualLiability.subtract(totalAnnualPaid);

                log.info("📊 Итоги {} года для {}: Total Owed=${}, Total Paid=${}, Remaining=${}, Issues={}",
                        currentYear, company.getCompanyName(),
                        totalAnnualLiability, totalAnnualPaid, remainingLiability,
                        missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters));

                if (!missingQuarters.isEmpty() || remainingLiability.compareTo(BigDecimal.ONE) > 0) {
                    hasAnyIssues = true;

                    log.warn("⚠️ YEAR-END ALERT: Компания {} требует внимания перед концом года. " +
                                    "Проблемные кварталы: {}, Remaining Liability: ${}",
                            company.getCompanyName(),
                            missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters),
                            remainingLiability);

                    // Отправка email уведомлений
                    reportsMailSender.sendEmailAnnualFutaReport(
                            company.getCompanyEmail());
                }

            } catch (Exception ex) {
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                errorCount++;
                log.error("❌ Ошибка year-end preparation для компании ID: {}", company.getId(), ex);
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
