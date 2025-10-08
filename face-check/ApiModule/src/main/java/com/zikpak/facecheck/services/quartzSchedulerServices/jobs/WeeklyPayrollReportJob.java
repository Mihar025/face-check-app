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
public class WeeklyPayrollReportJob extends BaseSchedulerJob {

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private PayrollSummaryReportService payrollSummaryReportService;

    @Autowired
    private PayrollSummaryDataService payrollSummaryDataService;

    @Autowired
    private ReportsMailSender reportsMailSender;


    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📊 Weekly PayrollReportScheduler запущен: генерируем еженедельные отчеты");

        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(6);

        // ОДИН запрос вместо N+1
        List<WorkerPayroll> allWeeklyPayrolls = workerPayrollRepository
                .findAllByPeriodBetween(startDate, endDate);

        // Группируем по компаниям в памяти
        Map<Company, List<WorkerPayroll>> payrollsByCompany = allWeeklyPayrolls.stream()
                .collect(Collectors.groupingBy(wp -> wp.getCompany()));
        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();
        // Теперь обрабатываем только компании с payrolls
        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerPayroll> companyPayrolls = entry.getValue();

            try {
                // Генерируем данные отчета
                PayrollSummaryReportDTO reportData = payrollSummaryDataService
                        .generatePayrollSummaryData(company.getId(), startDate, endDate);

                // Генерируем PDF и сохраняем в S3
                payrollSummaryReportService.generatePayrollSummaryReport(reportData);

                log.info("✅ Weekly report сгенерен для компании: {} (ID: {})",
                        company.getCompanyName(), company.getId());

                reportsMailSender.sendEmailWeeklyPayrollReport(company.getCompanyEmail());
                totalSuccess++;

            } catch (Exception ex) {
                totalFailure++;
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Ошибка генерации weekly report для компании ID: {}",
                        company.getId(), ex);
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
