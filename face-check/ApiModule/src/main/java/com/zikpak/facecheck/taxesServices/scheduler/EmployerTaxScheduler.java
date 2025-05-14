package com.zikpak.facecheck.taxesServices.scheduler;


import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.taxesServices.services.EmployerTaxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployerTaxScheduler {

    private final WorkerPayrollRepository workerPayrollRepository;
    private final EmployerTaxService employerTaxService;

    @Scheduled(cron = "0 0 4 * * SUN") // каждое воскресенье в 4:00 утра
    // @Scheduled(fixedDelay = 10000) // каждые 10 секунд
    public void calculateWeeklyEmployerTaxes() {
        log.info("🧮 Scheduler запущен: начинаем расчёт налогов для всех payroll'ов");

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(LocalDate.now().minusDays(1)) // вчера была суббота
                .stream()
                .filter(p -> !p.isEmployerTaxesCalculated())
                .toList();

        for (WorkerPayroll payroll : payrolls) {
            try {
                employerTaxService.calculateAndSaveEmployerTaxes(payroll);
                payroll.setEmployerTaxesCalculated(true);
                workerPayrollRepository.save(payroll);
                log.info("✅ Расчёт выполнен для payroll ID: {}", payroll.getId());
            } catch (Exception e) {
                log.error("❌ Ошибка при расчёте налогов для payroll ID: {}", payroll.getId(), e);
            }
        }

        log.info("🏁 Scheduler завершил работу");
    }

}
