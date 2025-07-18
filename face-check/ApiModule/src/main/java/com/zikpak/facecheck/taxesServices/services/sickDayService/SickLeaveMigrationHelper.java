package com.zikpak.facecheck.taxesServices.services.sickDayService;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Вспомогательный сервис для миграции существующих данных
 * и инициализации sick leave для существующих пользователей
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SickLeaveMigrationHelper {

    private final UserRepository userRepository;

    /**
     * Инициализирует sick leave данные для всех существующих пользователей
     * Запустите этот метод один раз после добавления новых полей
     */
    @Transactional
    public void initializeSickLeaveForExistingUsers() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            // Если дата найма не установлена, используем дату создания аккаунта
            if (user.getHireDate() == null && user.getCreatedDate() != null) {
                user.setHireDate(user.getCreatedDate().toLocalDate());
                log.info("Установлена дата найма для пользователя {}: {}",
                        user.getId(), user.getHireDate());
            }

            // Инициализируем новые поля, если они null
            if (user.getSickLeaveAccruedThisYear() == null) {
                user.setSickLeaveAccruedThisYear(BigDecimal.ZERO);
            }

            if (user.getSickLeaveCarriedOver() == null) {
                user.setSickLeaveCarriedOver(BigDecimal.ZERO);
            }

            // Определяем, оплачиваемый ли sick leave
            if (user.getSickLeavePaid() == null && user.getCompany() != null) {
                int companySize = user.getCompany().getEmployees().size();
                user.setSickLeavePaid(companySize >= 5);
            }

            userRepository.save(user);
        }

        log.info("Инициализация sick leave завершена для {} пользователей", allUsers.size());
    }

    /**
     * Пересчитывает sick leave для пользователя на основе истории attendance
     * Полезно, если нужно исправить данные
     */
    @Transactional
    public void recalculateSickLeaveForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        // Сбрасываем счетчики
        user.setSickLeaveAccrued(BigDecimal.ZERO);
        user.setSickLeaveAccruedThisYear(BigDecimal.ZERO);

        // Пересчитываем на основе hoursWorkedYearToDate
        if (user.getHoursWorkedYearToDate() != null &&
                user.getHoursWorkedYearToDate().compareTo(BigDecimal.ZERO) > 0) {

            // 1 час sick leave за каждые 30 отработанных часов
            BigDecimal totalSickHours = user.getHoursWorkedYearToDate()
                    .divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_DOWN);

            // Применяем годовой лимит
            int companySize = user.getCompany().getEmployees().size();
            BigDecimal annualCap = companySize >= 100
                    ? BigDecimal.valueOf(56)
                    : BigDecimal.valueOf(40);

            BigDecimal actualAccrued = totalSickHours.min(annualCap);

            user.setSickLeaveAccrued(actualAccrued);
            user.setSickLeaveAccruedThisYear(actualAccrued);

            log.info("Пересчитан sick leave для пользователя {}: {} часов",
                    userId, actualAccrued);
        }

        userRepository.save(user);
    }

    /**
     * Выполняет перенос sick leave на новый год для всех пользователей
     * Запускайте в начале каждого года (например, через @Scheduled)
     */
    @Transactional
    public void performYearlyCarryover() {
        LocalDate today = LocalDate.now();
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            // Проверяем, не делали ли уже carryover в этом году
            if (user.getLastSickLeaveCarryoverDate() != null &&
                    user.getLastSickLeaveCarryoverDate().getYear() == today.getYear()) {
                continue;
            }

            // Рассчитываем неиспользованные часы
            BigDecimal unused = user.getSickLeaveAccrued().subtract(user.getSickLeaveUsed());
            BigDecimal carryOver = unused.min(BigDecimal.valueOf(40)); // Максимум 40 часов

            // Выполняем перенос
            user.setSickLeaveAccrued(carryOver);
            user.setSickLeaveUsed(BigDecimal.ZERO);
            user.setSickLeaveAccruedThisYear(BigDecimal.ZERO);
            user.setSickLeaveCarriedOver(carryOver);
            user.setHoursWorkedYearToDate(BigDecimal.ZERO);
            user.setLastSickLeaveCarryoverDate(today);

            userRepository.save(user);

            log.info("Выполнен перенос {} часов sick leave для пользователя {}",
                    carryOver, user.getId());
        }

        log.info("Годовой перенос sick leave завершен для {} пользователей",
                allUsers.size());
    }
}