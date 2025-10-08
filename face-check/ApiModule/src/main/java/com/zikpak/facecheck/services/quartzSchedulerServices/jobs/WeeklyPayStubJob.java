package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j

public class WeeklyPayStubJob extends BaseSchedulerJob {


    @Autowired
    private WorkerPayrollRepository payrollRepository;

    @Autowired
    private PayStubService payStubService;

    @Autowired
    private ReportsMailSender reportsMailSender;


    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📄 Scheduler (WEEKLY): запуск генерации Paystubs");

        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        // Получаем payrolls с фильтрацией по WEEKLY компаниям
        List<WorkerPayroll> payrolls = payrollRepository
                .findAllByPeriodEnd(today)
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getPayStubGenerated()))
                .filter(p -> p.getCompany().getCompanyPaymentPosition() == CompanyPaymentPosition.WEEKLY)
                .toList();

        log.info("Found {} WEEKLY payrolls to process", payrolls.size());

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = payrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerPayroll> companyPayrolls = entry.getValue();
            int companySuccessCount = 0;

            for (WorkerPayroll payroll : companyPayrolls) {
                try {
                    payStubService.generatePayStubPdf(payroll.getId());
                    payroll.setPayStubGenerated(true);
                    payrollRepository.save(payroll);
                    companySuccessCount++;
                    totalSuccess++;
                    log.info("✅ WEEKLY paystub создан для payroll ID: {}", payroll.getId());
                } catch (Exception e) {
                    totalFailure++;
                    errors.append("Payroll ID ").append(payroll.getId())
                            .append(": ").append(e.getMessage()).append("\n");
                    log.error("❌ Ошибка WEEKLY для payroll ID: {}", payroll.getId(), e);
                }
            }

            // Отправляем email если есть успешно сгенерированные paystubs
            if (companySuccessCount > 0) {
                try {
                    reportsMailSender.sendEmailPaystubs(company.getCompanyEmail());
                    log.info("📧 Email отправлен компании: {} (paystubs: {})",
                            company.getCompanyEmail(), companySuccessCount);
                } catch (Exception e) {
                    errors.append("Email failed for company: ")
                            .append(company.getCompanyEmail())
                            .append(": ").append(e.getMessage()).append("\n");
                    log.error("❌ Ошибка отправки email для компании: {}",
                            company.getCompanyEmail(), e);
                }
            }
        }

        log.info("🏁 Scheduler (WEEKLY): завершено, всего: {}", payrolls.size());

        if (totalFailure == 0 && errors.length() == 0) {
            return JobResult.success(totalSuccess);
        } else if (totalSuccess > 0) {
            return JobResult.partialSuccess(totalSuccess, totalFailure, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }
}