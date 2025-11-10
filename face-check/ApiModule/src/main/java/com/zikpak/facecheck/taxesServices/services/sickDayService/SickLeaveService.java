package com.zikpak.facecheck.taxesServices.services.sickDayService;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SickLeaveService {

    private final UserRepository userRepository;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;

    /**
     * Начисление sick leave по правилам NYC
     * - Начисляется с первого часа работы
     * - 1 час sick leave за каждые 30 часов работы
     * - Лимиты зависят от размера компании
     */
    public void accrueSickLeave(Integer userId, double workedHours) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        // 1) Обновляем общее количество отработанных часов за год (null-safe)
        BigDecimal oldYtd = nz(user.getHoursWorkedYearToDate());
        BigDecimal worked = BigDecimal.valueOf(workedHours);
        BigDecimal newYtd = oldYtd.add(worked);
        user.setHoursWorkedYearToDate(newYtd);

        // 2) Начисляем sick leave с ПЕРВОГО часа работы (не с 80-го!)
        // 1 час sick leave за каждые 30 отработанных часов
        BigDecimal sickHoursThisPeriod = worked
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.DOWN);

        // 3) Определяем лимиты в зависимости от размера компании (null-safe)
        int companySize = Optional.ofNullable(user.getCompany())
                .map(c -> c.getEmployees() == null ? 0 : c.getEmployees().size())
                .orElse(0);
        BigDecimal annualCap;
        boolean isPaid;

        if (companySize >= 100) {
            annualCap = BigDecimal.valueOf(56);  // 56 часов оплачиваемого
            isPaid = true;
        } else if (companySize >= 5) {
            annualCap = BigDecimal.valueOf(40);  // 40 часов оплачиваемого
            isPaid = true;
        } else {
            annualCap = BigDecimal.valueOf(40);  // 40 часов НЕоплачиваемого
            isPaid = false;
        }

        // 4) Проверяем, не превысили ли мы годовой лимит (null-safe)
        BigDecimal currentYearAccrued = nz(user.getSickLeaveAccruedThisYear());
        BigDecimal potentialNewAccrued = currentYearAccrued.add(sickHoursThisPeriod);

        if (potentialNewAccrued.compareTo(annualCap) > 0) {
            // Начисляем только до достижения лимита
            sickHoursThisPeriod = annualCap.subtract(currentYearAccrued).max(BigDecimal.ZERO);
        }

        // 5) Обновляем балансы (null-safe)
        BigDecimal newAccrued = nz(user.getSickLeaveAccrued()).add(sickHoursThisPeriod);
        user.setSickLeaveAccrued(newAccrued);

        // Обновляем счетчик начисленного в текущем году
        user.setSickLeaveAccruedThisYear(currentYearAccrued.add(sickHoursThisPeriod));

        // Устанавливаем флаг оплачиваемости
        user.setSickLeavePaid(isPaid);

        log.info("Начислено {} часов sick leave для пользователя {}. " +
                        "Общий баланс: {}, начислено в этом году: {}, оплачиваемый: {}",
                sickHoursThisPeriod, userId, newAccrued,
                user.getSickLeaveAccruedThisYear(), isPaid);

        // 6) Сохраняем обновлённого пользователя
        userRepository.save(user);
    }



    /**
     * Получение доступного баланса sick leave
     */
    public BigDecimal getAvailableSickHours(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        // null-safe разница
        BigDecimal available = nz(user.getSickLeaveAccrued()).subtract(nz(user.getSickLeaveUsed()));

        // Проверяем, может ли сотрудник использовать sick leave
        LocalDate hireDate = user.getHireDate();
        if (hireDate != null) {
            long daysSinceHire = ChronoUnit.DAYS.between(hireDate, LocalDate.now());
            if (daysSinceHire < 120) {
                log.info("Сотрудник {} не может использовать sick leave. " +
                                "До разблокировки осталось {} дней",
                        userId, 120 - daysSinceHire);
                return BigDecimal.ZERO;
            }
        }

        return available;
    }

    /**
     * Перенос неиспользованных часов на следующий год
     * Вызывать в начале года или при годовом пересчете
     */
    public void carryOverSickLeave(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        // Максимум 40 часов можно перенести на следующий год
        BigDecimal maxCarryOver = BigDecimal.valueOf(40);
        BigDecimal unused = nz(user.getSickLeaveAccrued()).subtract(nz(user.getSickLeaveUsed()));
        BigDecimal carryOver = unused.min(maxCarryOver);

        // Сбрасываем счетчики для нового года
        user.setSickLeaveAccrued(carryOver);
        user.setSickLeaveUsed(BigDecimal.ZERO);
        user.setSickLeaveAccruedThisYear(BigDecimal.ZERO);
        user.setHoursWorkedYearToDate(BigDecimal.ZERO);

        log.info("Перенесено {} часов sick leave на новый год для пользователя {}",
                carryOver, userId);

        userRepository.save(user);
    }

    /**
     * Получение детальной информации о sick leave
     */
    public SickLeaveInfo getSickLeaveInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        BigDecimal available = getAvailableSickHours(userId);
        boolean canUse = true;
        int daysUntilCanUse = 0;

        LocalDate hireDate = user.getHireDate();
        if (hireDate != null) {
            long daysSinceHire = ChronoUnit.DAYS.between(hireDate, LocalDate.now());
            if (daysSinceHire < 120) {
                canUse = false;
                daysUntilCanUse = (int)(120 - daysSinceHire);
            }
        }



        return SickLeaveInfo.builder()
                .totalAccrued(nz(user.getSickLeaveAccrued()))
                .totalUsed(nz(user.getSickLeaveUsed()))
                .available(available)
                .accruedThisYear(nz(user.getSickLeaveAccruedThisYear()))
                .isPaid(user.getSickLeavePaid())
                .canUse(canUse)
                .daysUntilCanUse(daysUntilCanUse)
                .build();
    }

    private static BigDecimal nz(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }


    @lombok.Builder
    @lombok.Data
    public static class SickLeaveInfo {
        private BigDecimal totalAccrued;
        private BigDecimal totalUsed;
        private BigDecimal available;
        private BigDecimal accruedThisYear;
        private Boolean isPaid;
        private boolean canUse;
        private int daysUntilCanUse;
    }
}
