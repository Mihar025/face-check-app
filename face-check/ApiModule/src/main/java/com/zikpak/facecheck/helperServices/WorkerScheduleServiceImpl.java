package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.WorkerScheduleI;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.metrics.MetricServiceWorkerSchedule;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerScheduleRepository;
import com.zikpak.facecheck.requestsResponses.schedule.*;
import com.zikpak.facecheck.requestsResponses.workScheduler.ScheduleDto;
import com.zikpak.facecheck.requestsResponses.workScheduler.WorkSchedulerResponse;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerScheduleServiceImpl implements WorkerScheduleI {
    private final WorkerScheduleRepository workerScheduleRepository;
    private final UserRepository userRepository;
    private final MetricServiceWorkerSchedule metric;

    @Override
    public WeeklyScheduleResponse findWorkerAllDaysAndAllHours(Authentication authentication, LocalDate weekDate) {
        Timer.Sample timer = metric.startTimer();
        try {
            var worker = checkIsUserAuthenticatedAndFindHim(authentication);
            LocalDate date = weekDate != null ? weekDate : LocalDate.now();

            var startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            var endOfWeek = startOfWeek.plusDays(6);

            List<WorkerSchedule> schedules = workerScheduleRepository.findByWorkerAndScheduleDateBetween(worker, startOfWeek, endOfWeek);

            List<DailyScheduleResponse> dailyScheduleResponses = schedules.stream()
                    .map(schedule -> {
                        Duration duration =
                                Duration.between(
                                        schedule.getExpectedStartTime(),
                                        schedule.getExpectedEndTime()
                                );
                        double hoursWorked = duration.toMinutes() / 60.0;
                        return DailyScheduleResponse.builder()
                                .dayOfWeek(schedule.getScheduleDate().getDayOfWeek().toString())
                                .date(schedule.getScheduleDate())
                                .hoursWorked(Math.round(hoursWorked * 100.0) / 100.0)
                                .startTime(schedule.getExpectedStartTime())
                                .endTime(schedule.getExpectedEndTime())
                                .workSiteName(schedule.getWorkSite().getSiteName())
                                .isOnDuty(schedule.getIsOnDuty())
                                .build();
                    })
                    .toList();
            double totalHours = dailyScheduleResponses.stream()
                    .mapToDouble(DailyScheduleResponse::getHoursWorked)
                    .sum();

            double regularHours = Math.min(totalHours, 40.0);
            double overtimeHours = Math.max(0, totalHours - 40.0);
            metric.recordWorkersDaysHours(worker.getFirstName() + " " + worker.getLastName() ,  totalHours, regularHours, overtimeHours, true);
            metric.recordOperationTime(timer, "find_hours");
            return WeeklyScheduleResponse.builder()
                    .dailySchedules(dailyScheduleResponses)
                    .totalWeekHours(Math.round(totalHours * 100.0) / 100.0)
                    .regularHours(Math.round(regularHours * 100.0) / 100.0)
                    .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                    .build();
        }catch (Exception e){
            metric.recordOperationTime(timer, "find_hours_failed");
            metric.recordScheduleError("find_hours_failed", e.getMessage(), e);
            metric.recordWorkersDaysHours("unknown", 0.0, 0.0, 0.0, false);
            throw e;
        }

    }



/*
    @Override
    public WorkSchedulerResponse setScheduleForWorkerScenario(Integer workerId, WorkerSetScheduleRequest request) {
        Timer.Sample timer = metric.startTimer();
        try {
            request.validate();
            var worker = findWorkerById(workerId);

            LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate endDate = startDate.plusYears(1);

            LocalDate currentDate = startDate;
            while (currentDate.isBefore(endDate)) {
                    WorkerSchedule workerSchedule = WorkerSchedule.builder()
                            .worker(worker)
                            .scheduleDate(currentDate)
                            .expectedStartTime(request.getStartTime())
                            .expectedEndTime(request.getEndTime())
                            .startLunch(request.getStartLunch())
                            .endLunch(request.getEndLunch())
                            .isCompanyPayingLunch(request.getIsCompanyPayingLunch())
                            .shift("DAY")
                            .isOnDuty(false)
                            .build();

                if (!workerScheduleRepository.existsByWorkerAndScheduleDate(worker, currentDate)) {
                    workerScheduleRepository.save(workerSchedule);
                }

                    workerScheduleRepository.save(workerSchedule);
                }
                currentDate = currentDate.plusDays(1);
            metric.recordNewWorkerSchedule(true);
            metric.recordOperationTime(timer, "set_schedule_for_worker_scenario");
            return WorkSchedulerResponse.builder()
                    .workerId(worker.getId())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .isCompanyPayingLunch(request.getIsCompanyPayingLunch())
                    .startLunch(request.getStartLunch())
                    .endLunch(request.getEndLunch())
                    .build();
        }catch (Exception e){
            metric.recordNewWorkerSchedule(false);
            metric.recordOperationTime(timer, "set_schedule_for_worker_scenario_failed");
            metric.recordScheduleError("schedule_for_worker_scenario_failed", e.getMessage(), e);
            throw e;
        }
    }

 */

    @Transactional
    public WorkSchedulerResponse setScheduleForWorkerScenario2(
            Integer workerId,
            WorkerSetScheduleRequest2 request
    ) {
        // 1. Валидация запроса
        request.validate();

        // 2. Получаем работника
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + workerId));

        List<WorkerSchedule> existingTemplates = workerScheduleRepository.findByWorkerAndIsTemplateTrue(worker);
        if (!existingTemplates.isEmpty()) {
            // Если шаблон уже есть, сначала удаляем старый
            workerScheduleRepository.deleteAll(existingTemplates);
            log.info("Deleted {} existing schedule templates for worker {}", existingTemplates.size(), workerId);
        }

        // 4. Создаём новые шаблоны для каждого дня недели
        List<WorkerSchedule> schedules = new ArrayList<>();

        for (Map.Entry<DayOfWeek, WorkerSetScheduleRequest2.DaySchedule> entry :
                request.getWeeklySchedule().entrySet()) {

            DayOfWeek dayOfWeek = entry.getKey();
            WorkerSetScheduleRequest2.DaySchedule daySchedule = entry.getValue();

            WorkerSchedule schedule = WorkerSchedule.builder()
                    .worker(worker)
                    .dayOfWeek(dayOfWeek)
                    .isDayOff(daySchedule.getIsDayOff())
                    .isTemplate(true) // ЭТО ШАБЛОН!
                    .build();

            // Если НЕ выходной - заполняем время
            if (!Boolean.TRUE.equals(daySchedule.getIsDayOff())) {
                schedule.setExpectedStartTime(daySchedule.getStartTime());
                schedule.setExpectedEndTime(daySchedule.getEndTime());
                schedule.setStartLunch(daySchedule.getLunchStart());
                schedule.setEndLunch(daySchedule.getLunchEnd());
                schedule.setIsCompanyPayingLunch(daySchedule.getIsCompanyPayingLunch());
            }

            schedules.add(schedule);
        }

        // 5. Сохраняем все шаблоны
        List<WorkerSchedule> savedSchedules = workerScheduleRepository.saveAll(schedules);

        // 6. Формируем ответ
        return WorkSchedulerResponse.builder()
                .workerId(workerId)
                .workerName(worker.getFirstName() + " " + worker.getLastName())
                .schedules(savedSchedules.stream()
                        .map(this::convertToScheduleDto)
                        .collect(Collectors.toList()))
                .message("Weekly schedule template set successfully for " +
                        savedSchedules.stream()
                                .filter(s -> !Boolean.TRUE.equals(s.getIsDayOff()))
                                .count() + " working days")
                .build();
    }

    private ScheduleDto convertToScheduleDto(WorkerSchedule entity) {
        return ScheduleDto.builder()
                .scheduleId(entity.getId())
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getExpectedStartTime())
                .endTime(entity.getExpectedEndTime())
                .lunchStart(entity.getStartLunch())
                .lunchEnd(entity.getEndLunch())
                .isCompanyPayingLunch(entity.getIsCompanyPayingLunch())
                .isDayOff(entity.getIsDayOff())
                .build();
    }

    @Transactional
    public void deleteWorkerScheduleTemplate(Integer workerId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found"));

        workerScheduleRepository.deleteByWorkerAndIsTemplateTrue(worker);
    }

    // НОВЫЙ МЕТОД - Получить шаблон расписания работника
    public WorkSchedulerResponse getWorkerScheduleTemplate(Integer workerId) {
        log.info("=== START getWorkerScheduleTemplate for worker ID: {} ===", workerId);

        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + workerId));

        log.info("Found worker: ID={}, FirstName={}, LastName={}",
                worker.getId(), worker.getFirstName(), worker.getLastName());

        // Проверим, что передаем в запрос
        log.info("Calling findByWorkerAndIsTemplateTrue with worker.getId()={}", worker.getId());

        List<WorkerSchedule> templates = workerScheduleRepository.findByWorkerAndIsTemplateTrue(worker);

        log.info("Query returned {} templates", templates.size());

        if (templates.isEmpty()) {
            // Дополнительная проверка - попробуем простой запрос
            log.warn("No templates found! Trying alternative query...");

            // Попробуем нативный запрос для диагностики
            List<WorkerSchedule> allSchedules = workerScheduleRepository.findAll();
            long templatesCount = allSchedules.stream()
                    .filter(s -> s.getWorker() != null &&
                            s.getWorker().getId().equals(workerId) &&
                            Boolean.TRUE.equals(s.getIsTemplate()))
                    .count();

            log.info("Alternative check: found {} templates for worker {} in all schedules",
                    templatesCount, workerId);
        }

        templates.forEach(t -> {
            log.info("Template: id={}, day={}, isTemplate={}, isDayOff={}, start={}, end={}",
                    t.getId(), t.getDayOfWeek(), t.getIsTemplate(), t.getIsDayOff(),
                    t.getExpectedStartTime(), t.getExpectedEndTime());
        });

        List<ScheduleDto> scheduleDtos = templates.stream()
                .map(this::convertToScheduleDto)
                .collect(Collectors.toList());

        log.info("=== END getWorkerScheduleTemplate: returning {} schedules ===", scheduleDtos.size());

        return WorkSchedulerResponse.builder()
                .workerId(workerId)
                .workerName(worker.getFirstName() + " " + worker.getLastName())
                .schedules(scheduleDtos)
                .message(templates.isEmpty() ? "No schedule template found" : "Schedule template retrieved successfully")
                .build();
    }


    @Override
    public WorkerHourResponse calculateWorkerTotalHoursPerWeek(Authentication authentication) {
        Timer.Sample timer = metric.startTimer();

        try {
            var worker = checkIsUserAuthenticatedAndFindHim(authentication);

            LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate saturday = sunday.plusDays(6);

            List<WorkerSchedule> weekSchedules = workerScheduleRepository.findByWorkerAndScheduleDateBetween(
                    worker,
                    sunday,
                    saturday
            );

            double totalHoursPerWeek = weekSchedules.stream()
                    .mapToDouble(schedule -> {
                        Duration duration = Duration.between(
                                schedule.getExpectedStartTime(),
                                schedule.getExpectedEndTime()
                        );
                        return duration.toMinutes() / 60.0;
                    })
                    .sum();

            double regularHours = Math.min(totalHoursPerWeek, 40.0);
            double overtimeHours = Math.max(0, totalHoursPerWeek - 40.0);

            metric.recordWorkersDaysHours(worker.getFirstName() + " " + worker.getLastName() ,totalHoursPerWeek, regularHours, overtimeHours, true);
            metric.recordOperationTime(timer, "calculate_hours_per_week");


            return WorkerHourResponse.builder()
                    .regularHours(Math.round(regularHours * 100.0) / 100.0)
                    .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                    .totalHours(Math.round(totalHoursPerWeek * 100) / 100.0)
                    .build();
        } catch (Exception e){
            metric.recordOperationTime(timer, "calculate_hours_per_week_failed");
            metric.recordScheduleError("calculate_hours_per_week_failed", e.getMessage(), e);
            metric.recordWorkersDaysHours("unknown",0.0, 0.0, 0.0, false);
            throw e;
        }
    }

    @Override
    public WorkerHourResponse calculateWorkerTotalHoursForSpecialWeek(Authentication authentication, LocalDate weekDate) {
        Timer.Sample timer = metric.startTimer();
        try {
            var worker = checkIsUserAuthenticatedAndFindHim(authentication);

            LocalDate sunday = weekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate saturday = sunday.plusDays(6);

            List<WorkerSchedule> weekSchedules = workerScheduleRepository.findByWorkerAndScheduleDateBetween(
                    worker,
                    sunday,
                    saturday
            );

            double totalHoursPerWeek = weekSchedules.stream()
                    .mapToDouble(schedule -> {
                        Duration duration = Duration.between(
                                schedule.getExpectedStartTime(),
                                schedule.getExpectedEndTime()
                        );
                        return duration.toMinutes() / 60.0;
                    })
                    .sum();

            double regularHours = Math.min(totalHoursPerWeek, 40.0);
            double overtimeHours = Math.max(0, totalHoursPerWeek - 40.0);


            metric.recordWorkersDaysHours(worker.getFirstName() + " " + worker.getLastName() ,totalHoursPerWeek, regularHours, overtimeHours, true);
            metric.recordOperationTime(timer, "calculate_hours_per_special_week");
            return WorkerHourResponse.builder()
                    .regularHours(Math.round(regularHours * 100.0) / 100.0)
                    .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                    .totalHours(Math.round(totalHoursPerWeek * 100) / 100.0)
                    .build();
        } catch(Exception e) {
            metric.recordOperationTime(timer, "calculate_hours_per_special_week_failed");
            metric.recordScheduleError("calculate_hours_per_special_week_failed", e.getMessage(), e);
            metric.recordWorkersDaysHours("unknown",0.0, 0.0, 0.0, false);
            throw e;
        }
    }


    private User checkIsUserAuthenticatedAndFindHim(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getId() == null) {
            throw new RuntimeException("You dont have permission to do this operation");
        }
        return  findWorkerById(user.getId());
    }

    private User findWorkerById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }
}
