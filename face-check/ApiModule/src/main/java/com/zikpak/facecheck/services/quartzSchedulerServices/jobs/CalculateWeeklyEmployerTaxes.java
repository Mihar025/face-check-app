package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.services.EmployerTaxService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class CalculateWeeklyEmployerTaxes extends BaseSchedulerJob {

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;
    @Autowired
    private EmployerTaxService employerTaxService;



    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("🧮 Scheduler запущен: начинаем расчёт налогов для всех payroll'ов");

        LocalDate today = LocalDate.now();

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(today.minusDays(1))
                .stream()
                .filter(p -> !p.isEmployerTaxesCalculated())
                .toList();

        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

        for (WorkerPayroll payroll : payrolls) {
            try {
                Company company = payroll.getCompany();

                if (!shouldCreateEmployerTaxRecord(payroll, company)) {
                    log.info("⏩ Пропускаем payroll ID {} — невалидные данные", payroll.getId());
                    continue;
                }

                employerTaxService.calculateAndSaveEmployerTaxes(payroll);
                payroll.setEmployerTaxesCalculated(true);
                totalSuccess++;
                workerPayrollRepository.save(payroll);

                log.info("✅ Расчёт выполнен для payroll ID: {}", payroll.getId());
            } catch (Exception e) {
                totalFailure++;
                errors.append("Payroll ID ").append(payroll.getId())
                        .append(": ").append(e.getMessage()).append("\n");
                log.error("❌ Ошибка при расчёте налогов для payroll ID: {}", payroll.getId(), e);
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


    /**
     * FIX: Раньше проверяли LocalDate.now().isEqual(payroll.getPeriodEnd())
     * Джоба запускается в ВОСКРЕСЕНЬЕ, а periodEnd = СУББОТА → всегда false.
     * Теперь проверяем что период уже завершился (today >= periodEnd).
     */
    private boolean shouldCreateEmployerTaxRecord(WorkerPayroll payroll, Company company) {
        if (payroll == null || company == null || company.getCompanyPaymentPosition() == null) {
            return false;
        }

        return !LocalDate.now().isBefore(payroll.getPeriodEnd());
    }

}