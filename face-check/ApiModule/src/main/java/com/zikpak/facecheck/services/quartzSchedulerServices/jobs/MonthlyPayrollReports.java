package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MonthlyPayrollReports extends BaseSchedulerJob {

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private PayrollSummaryDataService payrollSummaryDataService;

    @Autowired
    private PayrollSummaryReportService payrollSummaryReportService;

    @Autowired
    private ReportsMailSender reportsMailSender;




    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📊 Monthly PayrollReportScheduler запущен: генерируем месячные отчеты");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth()); // последний день прошлого месяца
        LocalDate startDate = endDate.withDayOfMonth(1); // первый день прошлого месяца

        log.info("📅 Генерируем отчеты за период: {} - {}", startDate, endDate);

        // ОДИН запрос вместо N+1
        List<WorkerPayroll> allMonthlyPayrolls = workerPayrollRepository
                .findAllByPeriodBetween(startDate, endDate);

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = allMonthlyPayrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();


        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();

            try {
                PayrollSummaryReportDTO reportData = payrollSummaryDataService
                        .generatePayrollSummaryData(company.getId(), startDate, endDate);

                payrollSummaryReportService.generatePayrollSummaryReport(reportData);

                reportsMailSender.sendEmailWeeklyPayrollReport(company.getCompanyEmail());

                log.info("✅ Monthly report сгенерен для компании: {} (ID: {}) за {}/{}",
                        company.getCompanyName(), company.getId(),
                        startDate.getMonthValue(), startDate.getYear());

                totalSuccess++;
            } catch (Exception ex) {
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");

                log.error("❌ Ошибка генерации monthly report для компании ID: {}",
                        company.getId(), ex);
                totalFailure++;
            }
        }
        if (totalFailure == 0) {
            return JobResult.success(totalSuccess);
        } else if (totalSuccess > 0) {
            return JobResult.partialSuccess(totalSuccess, totalFailure, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }

}
