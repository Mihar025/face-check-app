package com.zikpak.facecheck.services.workAttendanceService;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.finance.PayStubResponse;
import com.zikpak.facecheck.requestsResponses.worker.DailyFinanceInfo;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.security.AuthenticationServiceImpl;
import com.zikpak.facecheck.services.finance.FinanceCalculator;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkAttendanceService {

        private final WorkerAttendanceRepository workerAttendanceRepository;
        private final UserRepository userRepository;
        private final WorkSiteService workSiteService;
        private final AmazonS3Service amazonS3Service;
        private final WorkerSiteRepository workSiteRepository;
        private final WorkerPayrollRepository workerPayrollRepository;
        private final WorkerScheduleRepository workerScheduleRepository;
        private final FinanceCalculator financeCalculator;


        @Transactional
        public PunchInResponse makePunchIn(Authentication authentication, PunchInRequest punchInRequest) {
                try {
                        User user = validateAndGetUserByEmail(authentication);

                        checkForExistingPunchIn(user);

                        WorkSite workSite = validateAndGetWorkSite(punchInRequest.getWorkSiteId());

                        if (!user.getWorkSites().contains(workSite)) {
                                user.getWorkSites().add(workSite);
                                workSite.getUsers().add(user);
                        }

                        validateLocationForPunchIn(punchInRequest, workSite);

                        LocalDate today = LocalDate.now();
                        WorkerSchedule schedule = getWorkerScheduleForDate(user, today);
                        validatePunchInTime(schedule);

                        String photoUrl = amazonS3Service.uploadAttendancePhoto(
                                punchInRequest.getPhotoBase64(),
                                user.getEmail(),
                                "punch-in"
                        );

                        WorkerAttendance attendance = createAttendance(user, punchInRequest, photoUrl);
                        WorkerAttendance savedAttendance = workerAttendanceRepository.save(attendance);
                        workSite.setIsWorkerDidPunchIn(Boolean.TRUE);
                        user.setCurrentWorkSite(workSite);
                        return createSuccessResponseForPunchIn(user, workSite, savedAttendance);

                } catch (Exception e) {
                        log.error(e.getMessage());
                        return createErrorResponseForPunchIn(e.getMessage());
                }

        }


        public PunchOutResponse makePunchOut(Authentication authentication, PunchOutRequest punchOutRequest) {
                try {
                        User user = validateAndGetUserByEmail(authentication);
                        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

                        WorkerAttendance existingAttendance = workerAttendanceRepository
                                .findTodayActivePunchIn(user, startOfDay, endOfDay)
                                .orElseThrow(() -> new IllegalStateException("No active punch in found for today!"));

                        WorkSite workSite = validateAndGetWorkSite(punchOutRequest.getWorkSiteId());
                        LocalDate today = LocalDate.now();
                        WorkerSchedule schedule = getWorkerScheduleForDate(user, today);

                        validateLocationForPunchOut(punchOutRequest, workSite);

                        String photoUrl = amazonS3Service.uploadAttendancePhoto(
                                punchOutRequest.getPhotoBase64(),
                                user.getEmail(),
                                "punch-out"
                        );

                        existingAttendance.setCheckOutTime(LocalDateTime.now());
                        existingAttendance.setCheckOutPhotoUrl(photoUrl);
                        existingAttendance.setCheckOutLatitude(punchOutRequest.getLatitude());
                        existingAttendance.setCheckOutLongitude(punchOutRequest.getLongitude());

                        calculateWorkedHours(existingAttendance);
                        WorkerAttendance savedAttendance = workerAttendanceRepository.save(existingAttendance);

                        log.info("After calculating hours - hours worked: {}, overtime: {}",
                                savedAttendance.getHoursWorked(), savedAttendance.getOvertimeHours());

                        WorkerPayroll payroll = updatePayrollOnPunchOut(savedAttendance);

                        log.info("After payroll update - attendance hours: {}, payroll hours: {}",
                                savedAttendance.getHoursWorked(), payroll.getRegularHours() + payroll.getOvertimeHours());

                        workSite.setIsWorkerDidPunchIn(Boolean.FALSE);
                        return createSuccessResponseForPunchOut(user, workSite, savedAttendance);
                } catch (Exception e) {
                        log.error("Error during punch out", e);
                        return createErrorResponseForPunchOut(e.getMessage());
                }
        }

        public List<DailyEarningResponse> getCurrentWeekEarnings(Authentication authentication) {
                User user = validateAndGetUserByEmail(authentication);
                LocalDate now = LocalDate.now();

                LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate weekEnd = weekStart.plusDays(6);

                log.info("Getting earnings for period: {} to {} for user: {}",
                        weekStart, weekEnd, user.getEmail());

                List<WorkerAttendance> weekAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                user.getId(),
                                weekStart.atStartOfDay(),
                                weekEnd.atTime(LocalTime.MAX));

                log.info("Found {} total attendance records", weekAttendances.size());

                weekAttendances.forEach(attendance -> {
                        log.info("Attendance record - ID: {}, Date: {}, CheckOut: {}, NetPayPerDay: {}",
                                attendance.getId(),
                                attendance.getCheckInTime().toLocalDate(),
                                attendance.getCheckOutTime(),
                                attendance.getGrossPayPerDay());
                });

                List<DailyEarningResponse> result = weekAttendances.stream()
                        .filter(a -> a.getCheckOutTime() != null && a.getGrossPayPerDay() != null)
                        .map(attendance -> new DailyEarningResponse(
                                attendance.getCheckInTime().toLocalDate(),
                                attendance.getGrossPayPerDay().doubleValue()
                        ))
                        .sorted(Comparator.comparing(DailyEarningResponse::getDate))
                        .collect(Collectors.toList());

                log.info("Returning {} daily earnings records", result.size());
                return result;
        }

        public FinanceInfoForWeekInFinanceScreenResponse getFinanceInfoForFinanceScreen(Authentication authentication, LocalDate selectedWeekStart) {
                User user = validateAndGetUserByEmail(authentication);

                LocalDate weekStart = selectedWeekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate weekEnd = weekStart.plusDays(6);

                log.info("Getting weekly attendance for period: {} to {} for user: {}",
                        weekStart, weekEnd, user.getEmail());

                List<WorkerAttendance> weeklyAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                user.getId(),
                                weekStart.atStartOfDay(),
                                weekEnd.atTime(LocalTime.MAX));

                if (weeklyAttendances.isEmpty()) {
                        return createEmptyResponse(weekStart, weekEnd);
                }

                double totalHoursWorked = weeklyAttendances.stream()
                        .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                        .sum();

                BigDecimal totalGrossPay = weeklyAttendances.stream()
                        .map(a -> a.getGrossPayPerDay() != null ? a.getGrossPayPerDay() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalNetPay = weeklyAttendances.stream()
                        .map(a -> a.getNetPay() != null ? a.getNetPay() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Map<LocalDate, List<WorkerAttendance>> attendanceByDay = weeklyAttendances.stream()
                        .collect(Collectors.groupingBy(a -> a.getCheckInTime().toLocalDate()));

                List<DailyFinanceInfo> dailyInfo = new ArrayList<>();
                LocalDate currentDate = weekStart;
                while (!currentDate.isAfter(weekEnd)) {
                        List<WorkerAttendance> dayAttendances = attendanceByDay.getOrDefault(currentDate, new ArrayList<>());

                        DailyFinanceInfo dayInfo = DailyFinanceInfo.builder()
                                .date(currentDate)
                                .hoursWorked(dayAttendances.stream()
                                        .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                                        .sum())
                                .grossPay(dayAttendances.stream()
                                        .map(a -> a.getGrossPayPerDay() != null ? a.getGrossPayPerDay() : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                .build();

                        dailyInfo.add(dayInfo);
                        currentDate = currentDate.plusDays(1);
                }

                return FinanceInfoForWeekInFinanceScreenResponse.builder()
                        .totalHoursWorked(totalHoursWorked)
                        .totalGrossPay(totalGrossPay)
                        .totalNetPay(totalNetPay)
                        .periodStart(weekStart)
                        .periodEnd(weekEnd)
                        .dailyInfo(dailyInfo)
                        .build();
        }

        private FinanceInfoForWeekInFinanceScreenResponse createEmptyResponse(LocalDate weekStart, LocalDate weekEnd) {
                List<DailyFinanceInfo> emptyDailyInfo = new ArrayList<>();
                LocalDate currentDate = weekStart;
                while (!currentDate.isAfter(weekEnd)) {
                        emptyDailyInfo.add(DailyFinanceInfo.builder()
                                .date(currentDate)
                                .hoursWorked(0.0)
                                .grossPay(BigDecimal.ZERO)
                                .build());
                        currentDate = currentDate.plusDays(1);
                }

                return FinanceInfoForWeekInFinanceScreenResponse.builder()
                        .totalHoursWorked(0.0)
                        .totalGrossPay(BigDecimal.ZERO)
                        .totalNetPay(BigDecimal.ZERO)
                        .periodStart(weekStart)
                        .periodEnd(weekEnd)
                        .dailyInfo(emptyDailyInfo)
                        .build();
        }


        private WorkerSchedule getWorkerScheduleForDate(User worker, LocalDate date) {
                return workerScheduleRepository.findByWorkerAndScheduleDate(worker, date)
                        .orElseThrow(() -> new IllegalStateException("No schedule found for worker on date: " + date));
        }


        /**
         * Исправленный метод расчета рабочего времени
         */
        private void calculateWorkedHours(WorkerAttendance attendance) {
                User worker = attendance.getWorker();
                LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();

                WorkerSchedule schedule = getWorkerScheduleForDate(worker, attendanceDate);
                if (schedule == null) {
                        log.error("No schedule found for worker ID {} on date {}", worker.getId(), attendanceDate);
                        throw new IllegalStateException("No schedule found for worker on date: " + attendanceDate);
                }

                // Получаем запланированное время начала и окончания смены
                LocalTime scheduleStartTime = schedule.getExpectedStartTime();
                LocalTime scheduleEndTime = schedule.getExpectedEndTime();

                // Преобразуем в LocalDateTime для конкретной даты
                LocalDateTime scheduledStart = attendance.getCheckInTime().toLocalDate().atTime(scheduleStartTime);
                LocalDateTime scheduledEnd = attendance.getCheckInTime().toLocalDate().atTime(scheduleEndTime);

                // Получаем время обеда
                LocalDateTime lunchStartTime = attendance.getCheckInTime().toLocalDate().atTime(schedule.getStartLunch().toLocalTime());
                LocalDateTime lunchEndTime = attendance.getCheckInTime().toLocalDate().atTime(schedule.getEndLunch().toLocalTime());
                boolean isPayingLunch = schedule.getIsCompanyPayingLunch();
                double lunchDuration = java.time.Duration.between(lunchStartTime, lunchEndTime).toMinutes() / 60.0;

                // Получаем фактическое время входа/выхода
                LocalDateTime actualCheckIn = attendance.getCheckInTime();
                LocalDateTime actualCheckOut = attendance.getCheckOutTime();

                log.info("Worker {} schedule - Expected hours: {} to {}, Lunch: {} to {}, Paying lunch: {}",
                        worker.getId(),
                        scheduleStartTime,
                        scheduleEndTime,
                        schedule.getStartLunch().toLocalTime(),
                        schedule.getEndLunch().toLocalTime(),
                        isPayingLunch);

                log.info("Worker actual attendance - Check in: {}, Check out: {}",
                        actualCheckIn, actualCheckOut);

                // ИСПРАВЛЕНИЕ: Всегда используем график как базовое время для расчета
                // Для начала смены - если пришел раньше, считаем по графику
                LocalDateTime effectiveCheckIn;
                if (actualCheckIn.isBefore(scheduledStart)) {
                        effectiveCheckIn = scheduledStart;
                        log.info("Worker checked in early at {}, adjusted to scheduled start: {}", actualCheckIn, effectiveCheckIn);
                } else {
                        effectiveCheckIn = actualCheckIn;
                }

                // Для окончания смены - если ушел позже, считаем по графику
                // ИСПРАВЛЕНИЕ: Не включаем дополнительное время после графика в регулярные часы
                LocalDateTime effectiveCheckOut;
                if (actualCheckOut.isAfter(scheduledEnd)) {
                        effectiveCheckOut = scheduledEnd;
                        log.info("Worker checked out late at {}, regular hours capped at scheduled end: {}", actualCheckOut, effectiveCheckOut);
                } else {
                        effectiveCheckOut = actualCheckOut;
                }

                // Обработка обеденного перерыва
                double totalHours;

                // Случай 1: Работник ушел до начала обеда
                if (effectiveCheckOut.isBefore(lunchStartTime)) {
                        // Просто считаем время между входом и выходом
                        totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                        log.info("Worker left before lunch break, total hours: {}", totalHours);
                }
                // Случай 2: Работник ушел во время обеда и компания не платит за обед
                else if (effectiveCheckOut.isAfter(lunchStartTime) && effectiveCheckOut.isBefore(lunchEndTime) && !isPayingLunch) {
                        // Считаем только до начала обеда
                        totalHours = java.time.Duration.between(effectiveCheckIn, lunchStartTime).toMinutes() / 60.0;
                        log.info("Worker left during lunch and company doesn't pay lunch, hours counted to lunch start: {}", totalHours);
                }
                // Случай 3: Работник работал через обед
                else {
                        if (isPayingLunch) {
                                // Компания платит за обед, считаем всё время
                                totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                                log.info("Company pays for lunch, counting full time: {}", totalHours);
                        } else {
                                // Компания не платит за обед, вычитаем время обеда
                                double hoursBeforeLunch = 0;
                                if (effectiveCheckIn.isBefore(lunchStartTime)) {
                                        hoursBeforeLunch = java.time.Duration.between(effectiveCheckIn, lunchStartTime).toMinutes() / 60.0;
                                }

                                double hoursAfterLunch = 0;
                                if (effectiveCheckOut.isAfter(lunchEndTime)) {
                                        hoursAfterLunch = java.time.Duration.between(lunchEndTime, effectiveCheckOut).toMinutes() / 60.0;
                                }

                                totalHours = hoursBeforeLunch + hoursAfterLunch;
                                log.info("Company doesn't pay for lunch, hours calculated: before lunch {}, after lunch {}, total {}",
                                        hoursBeforeLunch, hoursAfterLunch, totalHours);
                        }
                }

                // Устанавливаем рассчитанные часы
                attendance.setHoursWorked(totalHours);
                log.info("Total regular hours calculated: {}", totalHours);

                // Расчет овертайма, если работник работал дольше запланированного времени
                calculateOvertime(attendance, scheduledEnd, actualCheckOut);
        }

        /**
         * Исправленный метод расчета сверхурочных часов (овертайма)
         */
        private void calculateOvertime(WorkerAttendance attendance, LocalDateTime scheduledEnd, LocalDateTime actualCheckOut) {
                if (actualCheckOut.isAfter(scheduledEnd)) {
                        // Рассчитываем потенциальные сверхурочные часы
                        double overtimeHours = java.time.Duration.between(scheduledEnd, actualCheckOut).toMinutes() / 60.0;

                        // ИСПРАВЛЕНИЕ: Проверяем, достигнут ли недельный порог в 40 часов для учета овертайма
                        User worker = attendance.getWorker();
                        LocalDate today = attendance.getCheckInTime().toLocalDate();
                        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                        LocalDate weekEnd = weekStart.plusDays(6); // Суббота

                        // Получаем все посещения за текущую неделю (исключая текущее)
                        List<WorkerAttendance> weekAttendances = workerAttendanceRepository
                                .findAllByWorkerIdAndCheckInTimeBetween(
                                        worker.getId(),
                                        weekStart.atStartOfDay(),
                                        weekEnd.atTime(LocalTime.MAX))
                                .stream()
                                .filter(a -> !a.getId().equals(attendance.getId())) // Исключаем текущее посещение
                                .collect(Collectors.toList());

                        // Считаем суммарные часы за неделю (без текущего дня)
                        double weeklyHoursWithoutToday = weekAttendances.stream()
                                .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                                .sum();

                        // Суммарные часы включая текущий день (регулярные, без овертайма)
                        double totalWeeklyRegularHours = weeklyHoursWithoutToday + attendance.getHoursWorked();

                        log.info("Weekly hours before today: {}, with today: {}, weekly threshold: 40.0",
                                weeklyHoursWithoutToday, totalWeeklyRegularHours);

                        // Учитываем овертайм только если превышен недельный порог в 40 часов
                        if (totalWeeklyRegularHours > 40.0) {
                                // Если общее количество часов за неделю превышает 40,
                                // то овертайм сегодня - это либо все сверхурочные за день,
                                // либо общее превышение недельного лимита, в зависимости от того, что меньше

                                double weeklyOvertime = totalWeeklyRegularHours - 40.0;
                                double dailyOvertime = overtimeHours;

                                // Выбираем наименьшее из двух значений
                                double effectiveOvertimeHours = Math.min(weeklyOvertime, dailyOvertime);

                                attendance.setOvertimeHours(effectiveOvertimeHours);
                                log.info("Overtime hours (40+ weekly threshold): {}", effectiveOvertimeHours);
                        } else {
                                // Если недельный порог не превышен, овертайм не начисляется
                                attendance.setOvertimeHours(0.0);
                                log.info("No overtime hours assigned (below 40 hours weekly threshold)");
                        }
                } else {
                        attendance.setOvertimeHours(0.0);
                        log.info("No overtime (checkout within scheduled time)");
                }
        }



        private User validateAndGetUserByEmail(Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                if(user == null || user.getId() == null){
                        throw new RuntimeException("User not found");
                }
                return userRepository.findByEmail(user.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
        }


        private void checkForExistingPunchIn(User user) {
                var existingAttendance = workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(user);
                if(existingAttendance.isPresent()){
                        throw new IllegalStateException("You already have an active punch-in");
                }
        }




        private WorkSite validateAndGetWorkSite(Integer workSiteId) {
                var workSite = workSiteRepository.findById(workSiteId)
                        .orElseThrow(() -> new RuntimeException("Work site not found"));
                if(!workSite.getIsActive()){
                        throw new IllegalStateException("Work site is not active");
                }
                return workSite;
        }

        private void validateLocationForPunchIn(PunchInRequest punchInRequest, WorkSite workSite) {

                boolean isInRadius = workSiteService.isWithinRadiusForPunchInOut(
                        workSite.getId(),
                        punchInRequest.getLatitude(),
                        punchInRequest.getLongitude()
                );

                if(!isInRadius){
                        throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
                }
        }

        private void validateLocationForPunchOut(PunchOutRequest punchOutRequest, WorkSite workSite) {

                boolean isInRadius = workSiteService.isWithinRadiusForPunchInOut(
                        workSite.getId(),
                        punchOutRequest.getLatitude(),
                        punchOutRequest.getLongitude()
                );

                if(!isInRadius){
                        throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
                }
        }


        private void validatePunchInTime(WorkerSchedule schedule) {
                LocalTime currentTime = LocalTime.now();
                LocalTime earliestAllowed = schedule.getExpectedStartTime().minusMinutes(30);

                if (currentTime.isBefore(earliestAllowed)) {
                        throw new IllegalStateException(
                                "Too early for punch-in. Allowed from: " + earliestAllowed
                        );
                }
        }




        private WorkerAttendance createAttendance(User user, PunchInRequest punchInRequest, String photoUrl) {
                return WorkerAttendance.builder()
                        .worker(user)
                        .checkInTime(LocalDateTime.now())
                        .checkInPhotoUrl(photoUrl)
                        .checkInLatitude(punchInRequest.getLatitude())
                        .checkInLongitude(punchInRequest.getLongitude())
                        .build();
        }


        private PunchInResponse createSuccessResponseForPunchIn(User user, WorkSite workSite, WorkerAttendance savedAttendance) {
                return PunchInResponse.builder()
                        .workerId(user.getId())
                        .workSiteId(workSite.getId())
                        .workSiteName(workSite.getSiteName())
                        .workerFullName(user.getFirstName() + " " + user.getLastName())
                        .checkInTime(savedAttendance.getCheckInTime())
                        .formattedCheckInTime(formatDateTime(savedAttendance.getCheckInTime()))
                        .checkInPhotoUrl(savedAttendance.getCheckInPhotoUrl())
                        .checkInLatitude(savedAttendance.getCheckInLatitude())
                        .checkInLongitude(savedAttendance.getCheckInLongitude())
                        .workSiteAddress(workSite.getAddress())
                        .isSuccessful(true)
                        .message("Successfully checked in!")
                        .build();
        }


        private PunchOutResponse createSuccessResponseForPunchOut(User user, WorkSite workSite, WorkerAttendance attendance) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                return PunchOutResponse.builder()
                        .workerId(user.getId())
                        .workSiteId(workSite.getId())
                        .workSiteName(workSite.getSiteName())
                        .workerFullName(user.getFirstName() + " " + user.getLastName())
                        .checkInTime(attendance.getCheckInTime())
                        .formattedCheckInTime(attendance.getCheckInTime() != null ?
                                attendance.getCheckInTime().format(formatter) : null)
                        .checkOutTime(attendance.getCheckOutTime())
                        .formattedCheckOutTime(attendance.getCheckOutTime() != null ?
                                attendance.getCheckOutTime().format(formatter) : null)
                        .checkOutPhotoUrl(attendance.getCheckOutPhotoUrl())
                        .checkOutLatitude(attendance.getCheckOutLatitude())
                        .checkOutLongitude(attendance.getCheckOutLongitude())
                        .hoursWorked(attendance.getHoursWorked())
                        .overtimeHours(attendance.getOvertimeHours())
                        .workSiteAddress(workSite.getAddress())
                        .isSuccessful(true)
                        .message("Punch out successful")
                        .build();
        }

        private PunchInResponse createErrorResponseForPunchIn(String message) {
                return PunchInResponse.builder()
                        .isSuccessful(false)
                        .message("Error during punch in: " + message)
                        .build();
        }

        private PunchOutResponse createErrorResponseForPunchOut(String message) {
                return PunchOutResponse.builder()
                        .isSuccessful(false)
                        .message("Error during punch out: " + message)
                        .build();
        }

        public String formatDateTime(LocalDateTime dateTime) {
                return dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        }


        public WorkerPayroll updatePayrollOnPunchOut(WorkerAttendance workerAttendance) {
                User worker = workerAttendance.getWorker();
                LocalDate now = workerAttendance.getCheckOutTime().toLocalDate();

                log.info("Starting updatePayrollOnPunchOut - worker: {}, date: {}, hours: {}",
                        worker.getId(), now, workerAttendance.getHoursWorked());

                WorkerPayroll currentPayroll = workerPayrollRepository
                        .findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(worker, now, now)
                        .orElseGet(() -> {
                                log.info("Creating new payroll period");
                                return createNewPayrollPeriod(worker, now);
                        });

                workerAttendance.setPeriodStart(currentPayroll.getPeriodStart());
                workerAttendance.setPeriodEnd(currentPayroll.getPeriodEnd());

                log.info("Before calculations - payroll ID: {}, base rate: {}",
                        currentPayroll.getId(), currentPayroll.getBaseHourlyRate());

                updatePayrollCalculations(currentPayroll, workerAttendance);

                log.info("After calculations - regular hours: {}, overtime: {}, gross pay: {}",
                        currentPayroll.getRegularHours(), currentPayroll.getOvertimeHours(), currentPayroll.getGrossPay());

                WorkerPayroll savedPayroll = workerPayrollRepository.save(currentPayroll);

                log.info("After save - payroll ID: {}, saved regular hours: {}, saved overtime: {}, saved gross pay: {}",
                        savedPayroll.getId(), savedPayroll.getRegularHours(), savedPayroll.getOvertimeHours(), savedPayroll.getGrossPay());

                return savedPayroll;
        }



        private WorkerPayroll createNewPayrollPeriod(User worker, LocalDate date) {
                LocalDate periodStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate periodEnd = periodStart.plusDays(6);


                BigDecimal defaultHourlyRate = BigDecimal.valueOf(0.0);
                BigDecimal defaultOvertimeRate = BigDecimal.valueOf(0.0);

                if (worker.getPayrolls() != null && !worker.getPayrolls().isEmpty()) {
                        WorkerPayroll lastPayroll = worker.getPayrolls().getLast();
                        defaultHourlyRate = lastPayroll.getBaseHourlyRate();
                        defaultOvertimeRate = lastPayroll.getOvertimeRate();
                }

                return WorkerPayroll.builder()
                        .worker(worker)
                        .company(worker.getCompany())
                        .periodStart(periodStart)
                        .periodEnd(periodEnd)
                        .baseHourlyRate(defaultHourlyRate)
                        .overtimeRate(defaultOvertimeRate)
                        .build();
        }

        public void updatePayrollCalculations(WorkerPayroll payroll, WorkerAttendance currentAttendance) {
                log.info("🔄 Starting payroll calculations with current attendance - hours worked: {}, overtime: {}",
                        currentAttendance.getHoursWorked(), currentAttendance.getOvertimeHours());

                // Получаем все посещения за период, кроме текущего
                List<WorkerAttendance> periodAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                payroll.getWorker().getId(),
                                payroll.getPeriodStart().atStartOfDay(),
                                payroll.getPeriodEnd().atTime(LocalTime.MAX))
                        .stream()
                        .filter(a -> !a.getId().equals(currentAttendance.getId()))
                        .collect(Collectors.toList());

                log.info("✅ Found {} previous attendances for payroll period", periodAttendances.size());

                // Добавляем текущее посещение
                periodAttendances.add(currentAttendance);

                // Суммируем часы по всем посещениям с точностью до 2 знаков
                double totalRegularHours = 0;
                double totalOvertimeHours = 0;

                for (WorkerAttendance attendance : periodAttendances) {
                        if (attendance.getHoursWorked() != null) {
                                totalRegularHours += attendance.getHoursWorked();
                        }
                        if (attendance.getOvertimeHours() != null) {
                                totalOvertimeHours += attendance.getOvertimeHours();
                        }
                }

                // ИСПРАВЛЕНИЕ: Округление часов до 2 знаков после запятой для точности
                BigDecimal preciseRegularHours = BigDecimal.valueOf(totalRegularHours)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal preciseOvertimeHours = BigDecimal.valueOf(totalOvertimeHours)
                        .setScale(2, RoundingMode.HALF_UP);

                totalRegularHours = preciseRegularHours.doubleValue();
                totalOvertimeHours = preciseOvertimeHours.doubleValue();

                log.info("Total hours calculated - regular: {}, overtime: {}", totalRegularHours, totalOvertimeHours);

                payroll.setRegularHours(totalRegularHours);
                payroll.setOvertimeHours(totalOvertimeHours);
                payroll.setTotalHours(totalRegularHours + totalOvertimeHours);
                payroll.setBaseHourlyRate(payroll.getWorker().getBaseHourlyRate());
                log.info("BASE HOURLY RATE: {}", payroll.getBaseHourlyRate());

                if (payroll.getBaseHourlyRate() == null || payroll.getBaseHourlyRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setBaseHourlyRate(BigDecimal.valueOf(15.0));
                        log.info("📌 Set default base hourly rate: {}", payroll.getBaseHourlyRate());
                }

                if (payroll.getOvertimeRate() == null || payroll.getOvertimeRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setOvertimeRate(payroll.getBaseHourlyRate().multiply(BigDecimal.valueOf(1.5)));
                        log.info("📌 Set default overtime rate: {}", payroll.getOvertimeRate());
                }

                // Расчет заработной платы
                PayStubResponse response = financeCalculator.calculateNetPay(
                        payroll.getWorker(),
                        payroll.getBaseHourlyRate(),
                        payroll.getOvertimeRate(),
                        payroll.getTotalHours(),
                        calculateYtdPFL(payroll.getWorker(), payroll.getPeriodStart()),
                        calculateYtdSocialSecurity(payroll.getWorker(), payroll.getPeriodStart())
                );

                // Обновляем зарплатные данные
                payroll.setGrossPay(response.getGrossPay());
                payroll.setNetPay(response.getNetPay());
                payroll.setMedicare(response.getMedicare());
                payroll.setSocialSecurityEmployee(response.getSocialSecurity());
                payroll.setFederalWithholding(response.getFederalTax());
                payroll.setNyStateWithholding(response.getStateTax());
                payroll.setNyLocalWithholding(response.getNycTax());
                payroll.setNyDisabilityWithholding(response.getDisability());
                payroll.setNyPaidFamilyLeave(response.getPfl());
                payroll.setTotalDeductions(response.getTotalDeductions());

                // Расчет зарплаты за текущий день
                BigDecimal baseRate = payroll.getWorker().getBaseHourlyRate();
                BigDecimal overtimeRate = payroll.getOvertimeRate();
                double dayRegularHours = currentAttendance.getHoursWorked() != null ? currentAttendance.getHoursWorked() : 0.0;
                double dayOvertimeHours = currentAttendance.getOvertimeHours() != null ? currentAttendance.getOvertimeHours() : 0.0;

                // Рассчитываем дневную зарплату
                BigDecimal dayRegularPay = baseRate.multiply(BigDecimal.valueOf(dayRegularHours));
                BigDecimal dayOvertimePay = overtimeRate.multiply(BigDecimal.valueOf(dayOvertimeHours));
                BigDecimal dayGrossPay = dayRegularPay.add(dayOvertimePay);

                // Расчет удержаний за день (пропорционально дневной зарплате)
                BigDecimal dayDeductionsRatio = BigDecimal.ZERO;
                if (dayGrossPay.compareTo(BigDecimal.ZERO) > 0 && response.getGrossPay().compareTo(BigDecimal.ZERO) > 0) {
                        dayDeductionsRatio = dayGrossPay.divide(response.getGrossPay(), 4, RoundingMode.HALF_UP);
                }

                BigDecimal dayDeductions = response.getTotalDeductions().multiply(dayDeductionsRatio);
                BigDecimal dayNetPay = dayGrossPay.subtract(dayDeductions);

                // Обновляем данные о дневной зарплате в записи о посещении
                currentAttendance.setGrossPayPerDay(dayGrossPay);
                currentAttendance.setNetPay(dayNetPay);

                // Сохраняем обновленную запись о посещении
                workerAttendanceRepository.save(currentAttendance);

                log.info("✅ Daily pay calculated - gross: {}, net: {}", dayGrossPay, dayNetPay);
                log.info("✅ Total payroll updated - gross: {}, net: {}", response.getGrossPay(), response.getNetPay());
        }




        private BigDecimal calculateYtdPFL(User user, LocalDate untilDate) {
                return workerPayrollRepository
                        .findAllByWorkerIdAndPeriodEndLessThanEqual(user.getId(), untilDate)
                        .stream()
                        .map(p -> p.getNyPaidFamilyLeave() != null ? p.getNyPaidFamilyLeave() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal calculateYtdSocialSecurity(User user, LocalDate untilDate) {
                return workerPayrollRepository
                        .findAllByWorkerIdAndPeriodEndLessThanEqual(user.getId(), untilDate)
                        .stream()
                        .map(p -> p.getGrossPay() != null ? p.getGrossPay() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        }


        public LastPunchTimeDTO getLastPunchTime(Authentication authentication) {
                User user = validateAndGetUserByEmail(authentication);

                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

                Optional<WorkerAttendance> todayAttendance = workerAttendanceRepository
                        .findTodayActivePunchIn(user, startOfDay, endOfDay);

                if (todayAttendance.isPresent()) {
                        LocalDateTime time = todayAttendance.get().getCheckInTime();
                        return formatDateTimeToDTO(time);
                }

                return workerAttendanceRepository
                        .findFirstByWorkerAndCheckOutTimeIsNotNullOrderByCheckOutTimeDesc(user)
                        .map(attendance -> formatDateTimeToDTO(attendance.getCheckOutTime()))
                        .orElse(new LastPunchTimeDTO("DD/MM/YYYY", "--:--"));
        }

        private LastPunchTimeDTO formatDateTimeToDTO(LocalDateTime dateTime) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

                String formattedDate = dateTime.format(dateFormatter);
                String formattedTime = dateTime.format(timeFormatter);

                return new LastPunchTimeDTO(formattedDate, formattedTime);
        }

}
