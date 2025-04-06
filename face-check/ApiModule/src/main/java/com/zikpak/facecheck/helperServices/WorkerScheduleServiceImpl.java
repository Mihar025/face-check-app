package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.WorkerScheduleI;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.mapper.WorkerScheduleMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerScheduleRepository;
import com.zikpak.facecheck.requestsResponses.schedule.*;
import com.zikpak.facecheck.requestsResponses.workScheduler.WorkSchedulerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;


@Service
@RequiredArgsConstructor
public class WorkerScheduleServiceImpl implements WorkerScheduleI {
    private final WorkerScheduleRepository workerScheduleRepository;
    private final UserRepository userRepository;
    private final WorkerScheduleMapper workerScheduleMapper;


    @Override
    public WeeklyScheduleResponse findWorkerAllDaysAndAllHours(Authentication authentication, LocalDate weekDate) {
        var worker = checkIsUserAuthenticatedAndFindHim(authentication);
        LocalDate date = weekDate != null ? weekDate : LocalDate.now();

        var startOfWeek =date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
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
                } )
                .toList();
        double totalHours = dailyScheduleResponses.stream()
                .mapToDouble(DailyScheduleResponse::getHoursWorked)
                .sum();

        double regularHours = Math.min(totalHours, 40.0);
        double overtimeHours = Math.max(0, totalHours - 40.0);

        return WeeklyScheduleResponse.builder()
                .dailySchedules(dailyScheduleResponses)
                .totalWeekHours(Math.round(totalHours * 100.0) / 100.0)
                .regularHours(Math.round(regularHours * 100.0) / 100.0)
                .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                .build();


    }



    @Override
    public WorkSchedulerResponse setScheduleForWorkerScenario(Integer workerId, WorkerSetScheduleRequest request) {
        request.validate();
        var worker = findWorkerById(workerId);

        // Находим ближайшее воскресенье
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        // Конец периода - через год
        LocalDate endDate = startDate.plusYears(1);

        // Проходим по всем дням в году
        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate)) {
            // Пропускаем субботу
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
                WorkerSchedule workerSchedule = WorkerSchedule.builder()
                        .worker(worker)
                        .scheduleDate(currentDate)
                        .expectedStartTime(request.getStartTime())
                        .expectedEndTime(request.getEndTime())
                        .shift("DAY")
                        .isOnDuty(false)
                        .build();

                workerScheduleRepository.save(workerSchedule);
            }
            // Переходим к следующему дню
            currentDate = currentDate.plusDays(1);
        }

        return WorkSchedulerResponse.builder()
                .workerId(worker.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    /*
@Override
public WorkSchedulerResponse setScheduleForWorkerScenario( Integer workerId, WorkerSetScheduleRequest request) {
    request.validate();
    //  checkIsPersonHasAdminAndBusinessOwnerRoleAuthenticatedAndFindHim(authentication);
    var worker = findWorkerById(workerId);

    LocalDate currentDate = LocalDate.now();
    if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
        throw new IllegalStateException("Construction work is not allowed on Saturdays!");
    }

    WorkerSchedule workerSchedule = WorkerSchedule.builder()
            .worker(worker)
            // .scheduleDate(currentDate)
            .expectedStartTime(request.getStartTime())
            .expectedEndTime(request.getEndTime())
            .shift("DAY")
            .isOnDuty(false)
            .build();

    workerScheduleRepository.save(workerSchedule);

    return WorkSchedulerResponse.builder()
            .workerId(worker.getId())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .build();
}


     */




    @Override
    public WorkerHourResponse calculateWorkerTotalHoursPerWeek(Authentication authentication) {
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

        return WorkerHourResponse.builder()
                .regularHours(Math.round(regularHours * 100.0) / 100.0)
                .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                .totalHours(Math.round(totalHoursPerWeek * 100) / 100.0)
                .build();
    }

    @Override
    public WorkerHourResponse calculateWorkerTotalHoursForSpecialWeek(Authentication authentication, LocalDate weekDate) {
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

        return WorkerHourResponse.builder()
                .regularHours(Math.round(regularHours * 100.0) / 100.0)
                .overtimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                .totalHours(Math.round(totalHoursPerWeek * 100) / 100.0)
                .build();
    }


    private void checkIsPersonHasAdminAndBusinessOwnerRoleAuthenticatedAndFindHim(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.isAdmin() && !user.isBusinessOwner()){
            throw new RuntimeException("You dont have permission to do this operation");
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
