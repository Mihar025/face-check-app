package com.zikpak.facecheck.services.workAttendanceService;

import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.metrics.MetricsService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.AttendanceResponse;
import com.zikpak.facecheck.requestsResponses.OvertimeResponse;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.WorkerPhotosResponse;
import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.finance.PayStubResponse;
import com.zikpak.facecheck.requestsResponses.worker.DailyFinanceInfo;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.calculators.FinanceCalculator;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryResponse;
import com.zikpak.facecheck.taxesServices.services.AsyncNotificationService;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationRequest;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
import com.zikpak.facecheck.taxesServices.services.sickDayService.SickLeaveService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import io.micrometer.core.instrument.Timer;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        private final SickLeaveService sickLeaveService;
        private final MetricsService metricsService;
        private final AsyncNotificationService notificationService;
        private final WorkAttendanceMapper workAttendanceMapper;



        private static final String typePunchIn = "PUNCH-IN";
        private static final String typePunchOut = "PUNCH-OUT";
        private static final ExecutorService PHOTO_UPLOAD_EXECUTOR = Executors.newFixedThreadPool(10);



        public CompletableFuture<String> uploadPhotoAsync(String base64, String email, String type){
                return CompletableFuture.supplyAsync(() ->
                        amazonS3Service.uploadAttendancePhoto(base64, email, type), PHOTO_UPLOAD_EXECUTOR);
        }


        @Transactional
        public PunchInResponse makePunchIn(Authentication authentication, PunchInRequest punchInRequest) {

                Timer.Sample  timer = metricsService.startTimer();
                User user = validateAndGetUserByEmail(authentication);

                CompletableFuture<String> photoUrlFuture = uploadPhotoAsync(
                        punchInRequest.getPhotoBase64(),
                        user.getEmail(),
                        "punch-in"
                );

                try {


                                checkForExistingPunchIn(user);

                                WorkSite workSite = validateAndGetWorkSite(punchInRequest.getWorkSiteId());

                                if (!user.getWorkSites().contains(workSite)) {
                                        user.getWorkSites().add(workSite);
                                        workSite.getUsers().add(user);
                                }

                                boolean isInRadius = validateLocationForPunchIn(punchInRequest, workSite);
                                metricsService.recordLocationValidation(
                                        workSite.getSiteName(),
                                        user.getFirstName() + " " + user.getLastName(),
                                        user.getCompany().getCompanyName(),
                                        isInRadius,
                                        true
                                );

                                LocalDate today = LocalDate.now();
                                WorkerSchedule schedule = getWorkerScheduleForDate(user, today);
                                validatePunchInTime(schedule);

                                long startTime = System.currentTimeMillis();

                                long endTime = System.currentTimeMillis();
                                long duration = endTime - startTime;
                                log.info("Punch IN {} ms", duration);

                                metricsService.recordPhotoUploading(
                                        "punch_in",
                                        true,
                                        duration
                                );

                               // String photoUrl = photoUrlFuture.join();
                                WorkerAttendance attendance = createAttendance(user, punchInRequest, "uploading");
                                WorkerAttendance savedAttendance = workerAttendanceRepository.save(attendance);

                        photoUrlFuture.thenAccept(url -> {
                                savedAttendance.setCheckInPhotoUrl(url);
                                workerAttendanceRepository.save(savedAttendance);
                        })
                                .exceptionally(ex -> {
                                        log.error("Failed to upload photo for attendance {}", savedAttendance.getId(), ex);
                                        savedAttendance.setCheckInPhotoUrl("upload-failed");
                                        workerAttendanceRepository.save(savedAttendance);
                                        return null;
                                });


                        LocalTime currentTime = LocalTime.now();
                        LocalTime scheduledTime = schedule.getExpectedStartTime();
                        if(currentTime.isBefore(scheduledTime)) {
                                long minutes = ChronoUnit.MINUTES.between(currentTime, scheduledTime);
                                metricsService.recordEarlyPunchIn(
                                        user.getFirstName() + " " + user.getLastName(),
                                                        minutes);
                        }
                        workSite.setIsWorkerDidPunchIn(Boolean.TRUE);
                        user.setCurrentWorkSite(workSite);

                        notificationService.buildAsyncNotificationForPunchInOut(
                                user.getFirstName(),
                                user.getLastName(),
                                workSite.getSiteName(),
                                today,
                                workSite.getAddress(),
                                user.getCompany().getId(),
                                typePunchIn
                        );

                        metricsService.recordPunchIn(workSite.getSiteName(), true);
                        metricsService.recordOperationTime(timer, "punch_in");

                        return createSuccessResponseForPunchIn(user, workSite, savedAttendance);
                } catch (Exception e) {
                        metricsService.recordPunchIn("unknown", false);
                        metricsService.recordError("punch_in", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "punch_in_failed");
                        log.error(e.getMessage());
                        return createErrorResponseForPunchIn(e.getMessage());
                }
        }



        @Transactional
        public PunchOutResponse makePunchOut(Authentication authentication, PunchOutRequest punchOutRequest) {
                Timer.Sample  timer = metricsService.startTimer();
                User user = validateAndGetUserByEmail(authentication);

                CompletableFuture<String> photoUrlAsyncOut = uploadPhotoAsync(
                        punchOutRequest.getPhotoBase64(),
                        user.getEmail(),
                        "punch-out"
                );

                try {
                        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

                        WorkerAttendance existingAttendance = workerAttendanceRepository
                                .findTodayActivePunchIn(user, startOfDay, endOfDay)
                                .orElseThrow(() -> new IllegalStateException("No active punch in found for today!"));

                        WorkSite workSite = validateAndGetWorkSite(punchOutRequest.getWorkSiteId());
                        LocalDate today = LocalDate.now();
                        WorkerSchedule schedule = getWorkerScheduleForDate(user, today);

                        LocalTime currentTime = LocalTime.now();
                        LocalTime scheduledTime = schedule.getExpectedEndTime();

                        if(currentTime.isAfter(scheduledTime)) {
                                long minutes = ChronoUnit.MINUTES.between(currentTime, scheduledTime);
                                metricsService.recordLatePunchOut(
                                        user.getFirstName() + " " + user.getLastName(),
                                                minutes
                                        );
                        }
                        boolean isInRadius = validateLocationForPunchOut(punchOutRequest, workSite);


                        //String photoUrl = photoUrlAsyncOut.join();
                        photoUrlAsyncOut.thenAccept(url -> {
                                existingAttendance.setCheckOutPhotoUrl(url);
                                workerAttendanceRepository.save(existingAttendance);
                        })
                                .exceptionally(ex -> {
                                        log.error("Failed to upload photo for attendance {}", existingAttendance.getId(), ex);
                                        existingAttendance.setCheckOutPhotoUrl("upload-failed");
                                        workerAttendanceRepository.save(existingAttendance);
                                        return null;
                                });



                        existingAttendance.setCheckOutTime(LocalDateTime.now());
                        existingAttendance.setCheckOutLatitude(punchOutRequest.getLatitude());
                        existingAttendance.setCheckOutLongitude(punchOutRequest.getLongitude());
                        existingAttendance.setCheckOutLocation(workSite.getAddress());

                        calculateWorkedHours(existingAttendance);
                        WorkerAttendance savedAttendance = workerAttendanceRepository.save(existingAttendance);

                        double workedHours = savedAttendance.getHoursWorked() != null
                                ? savedAttendance.getHoursWorked()
                                :0.00;
                        if(workedHours > 0){
                                sickLeaveService.accrueSickLeave(user.getId(), workedHours);
                        }

                        log.info("After calculating hours - hours worked: {}, overtime: {}",
                                savedAttendance.getHoursWorked(), savedAttendance.getOvertimeHours());

                        WorkerPayroll payroll = updatePayrollOnPunchOut(savedAttendance);

                        log.info("After payroll update - attendance hours: {}, payroll hours: {}",
                                savedAttendance.getHoursWorked(), payroll.getRegularHours() + payroll.getOvertimeHours());
                        workSite.setIsWorkerDidPunchIn(Boolean.FALSE);



                        notificationService.buildAsyncNotificationForPunchInOut(
                                user.getFirstName(),
                                user.getLastName(),
                                workSite.getSiteName(),
                                today,
                                workSite.getAddress(),
                                user.getCompany().getId(),
                                typePunchOut
                        );

                        metricsService.recordLocationValidation(
                                workSite.getSiteName(),
                                user.getFirstName() + " " + user.getLastName(),
                                user.getCompany().getCompanyName(),
                                isInRadius,
                                true
                        );

                        metricsService.recordPunchOut(
                                workSite.getSiteName(),
                                user.getCompany().getCompanyName(),
                                user.getFirstName() + " " + user.getLastName(),
                                true,
                                savedAttendance.getHoursWorked(),
                                savedAttendance.getOvertimeHours()
                                );

                        metricsService.recordPayrollCalculations(
                                payroll.getGrossPay(),
                                payroll.getNetPay(),
                                payroll.getTotalDeductions()
                        );


                        metricsService.recordOperationTime(timer, "punch_out");
                        return createSuccessResponseForPunchOut(user, workSite, savedAttendance);
                } catch (Exception e) {
                        metricsService.recordPunchOut(
                                "unknown",
                                "unknown",
                                "unknown",
                                false,
                                0.0,
                                0.0
                        );
                        metricsService.recordError("punch_out", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "punch_out_failed");
                        log.error("Error during punch out", e);
                        return createErrorResponseForPunchOut(e.getMessage());
                }
        }




    // ✅ Новый метод для admin - БЕЗ authentication, с userId
    @Transactional
    public void punchInForWorker(Integer userId, LocalDateTime checkInTime, Integer workSiteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found"));

        checkForExistingPunchIn(user);

        WorkSite workSite = null;
        if (workSiteId != null) {
            workSite = validateAndGetWorkSite(workSiteId);
            if (!user.getWorkSites().contains(workSite)) {
                user.getWorkSites().add(workSite);
                workSite.getUsers().add(user);
            }
        }

        WorkerAttendance attendance = WorkerAttendance.builder()
                .worker(user)
                .checkInTime(checkInTime)
                .checkInPhotoUrl("Manual entry by admin")
                .checkInLocation(workSite != null ? workSite.getAddress() : "Manual entry")
                .build();

        workerAttendanceRepository.save(attendance);

        if (workSite != null) {
            workSite.setIsWorkerDidPunchIn(Boolean.TRUE);
            user.setCurrentWorkSite(workSite);
        }

        log.info("Admin created punch in for worker: {}", userId);
    }

    // ✅ Новый метод для admin - БЕЗ authentication, с userId
    @Transactional
    public void punchOutForWorker(Integer userId, LocalDateTime checkOutTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found"));

        LocalDate targetDate = checkOutTime.toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        WorkerAttendance existingAttendance = workerAttendanceRepository
                .findTodayActivePunchIn(user, startOfDay, endOfDay)
                .orElseThrow(() -> new IllegalStateException("No active punch in found for this date!"));

        existingAttendance.setCheckOutTime(checkOutTime);
        existingAttendance.setCheckOutPhotoUrl("Manual entry by admin");
        existingAttendance.setCheckOutLocation("Manual entry");

        calculateWorkedHours(existingAttendance);
        WorkerAttendance savedAttendance = workerAttendanceRepository.save(existingAttendance);

        double workedHours = savedAttendance.getHoursWorked() != null
                ? savedAttendance.getHoursWorked() : 0.00;
        if (workedHours > 0) {
            sickLeaveService.accrueSickLeave(user.getId(), workedHours);
        }

        updatePayrollOnPunchOut(savedAttendance);

        if (user.getCurrentWorkSite() != null) {
            user.getCurrentWorkSite().setIsWorkerDidPunchIn(Boolean.FALSE);
        }

        log.info("Admin created punch out for worker: {}", userId);
    }




        public List<DailyEarningResponse> getCurrentWeekEarnings(Authentication authentication) {
                Timer.Sample timer = metricsService.startTimer();

                try {
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

                        metricsService.recordOperationTime(timer, "week_earnings");
                        log.info("Returning {} daily earnings records", result.size());
                        return result;
                }catch (Exception e){
                        metricsService.recordError("week_earnings", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "week_earnings_failed");
                        throw e;
                }
        }


        public FinanceInfoForWeekInFinanceScreenResponse getFinanceInfoForFinanceScreen(Authentication authentication, LocalDate selectedWeekStart) {
                Timer.Sample  timer = metricsService.startTimer();
                try {
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
                                        .netPay(dayAttendances.stream()
                                                .map(a -> a.getNetPay() != null ? a.getNetPay() : BigDecimal.ZERO)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                                        .build();

                                dailyInfo.add(dayInfo);
                                currentDate = currentDate.plusDays(1);

                        }

                        metricsService.recordEarningPeriod(
                                user.getFirstName() + " " + user.getLastName(),
                                totalGrossPay.doubleValue(),
                                totalNetPay.doubleValue(),
                                totalHoursWorked
                        );
                        metricsService.recordOperationTime(timer, "get_finance_info");

                        return FinanceInfoForWeekInFinanceScreenResponse.builder()
                                .totalHoursWorked(totalHoursWorked)
                                .totalGrossPay(totalGrossPay)
                                .totalNetPay(totalNetPay)
                                .periodStart(weekStart)
                                .periodEnd(weekEnd)
                                .dailyInfo(dailyInfo)
                                .build();
                }
                catch (Exception e){
                        metricsService.recordError("get_finance_info", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "get_finance_info_failed");
                        throw e;
                }
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



        public boolean isWorkerHasPunchInToday(Integer workerId, Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                if(!user.getId().equals(workerId)){
                        throw new AccessDeniedException("Access denied");
                }

                User foundedUser = userRepository.findById(workerId).orElseThrow(
                        () -> new EntityNotFoundException("Cannot find User with provided Id")
                );
                LocalDate today = LocalDate.now();
                LocalDateTime startOfTheDay = today.atStartOfDay();
                LocalDateTime endOfTheDay = today.plusDays(1).atStartOfDay();

                return workerAttendanceRepository.hasActivePunchInToday(foundedUser, startOfTheDay, endOfTheDay);

        }






        /**
         * Получает расписание для работника на конкретную дату
         * Теперь использует шаблоны по дням недели
         */
        private WorkerSchedule getWorkerScheduleForDate(User worker, LocalDate date) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                return workerScheduleRepository.findByWorkerAndDayOfWeekAndIsTemplateTrue(worker, dayOfWeek)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("No schedule template found for worker on %s", dayOfWeek)
                        ));
        }

        /**
         * Исправленный метод расчета рабочего времени
         */
        private void calculateWorkedHours(WorkerAttendance attendance) {
                User worker = attendance.getWorker();
                LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();

                WorkerSchedule schedule = getWorkerScheduleForDate(worker, attendanceDate);

                if (Boolean.TRUE.equals(schedule.getIsDayOff())) {
                        log.warn("Worker {} worked on day off: {}", worker.getId(), attendanceDate);
                        throw new IllegalStateException("Cannot calculate hours for day off");
                }

                LocalTime scheduleStartTime = schedule.getExpectedStartTime();
                LocalTime scheduleEndTime = schedule.getExpectedEndTime();

                LocalDateTime scheduledStart = attendanceDate.atTime(scheduleStartTime);
                LocalDateTime scheduledEnd = attendanceDate.atTime(scheduleEndTime);

                LocalTime lunchStartTime = schedule.getStartLunch();
                LocalTime lunchEndTime = schedule.getEndLunch();

                LocalDateTime lunchStart = attendanceDate.atTime(lunchStartTime);
                LocalDateTime lunchEnd = attendanceDate.atTime(lunchEndTime);

                boolean isPayingLunch = schedule.getIsCompanyPayingLunch();

                LocalDateTime actualCheckIn = attendance.getCheckInTime();
                LocalDateTime actualCheckOut = attendance.getCheckOutTime();

                log.info("Worker {} schedule for {} - Expected hours: {} to {}, Lunch: {} to {}, Paying lunch: {}",
                        worker.getId(),
                        attendanceDate.getDayOfWeek(),
                        scheduleStartTime,
                        scheduleEndTime,
                        lunchStartTime,
                        lunchEndTime,
                        isPayingLunch);

                // ✅ Корректируем check in (если пришёл раньше - считаем с начала графика)
                LocalDateTime effectiveCheckIn;
                if (actualCheckIn.isBefore(scheduledStart)) {
                        effectiveCheckIn = scheduledStart;
                } else {
                        effectiveCheckIn = actualCheckIn;
                }

                LocalDateTime effectiveCheckOut;
                if (actualCheckOut.isAfter(scheduledEnd)) {
                        effectiveCheckOut = scheduledEnd;
                } else {
                        effectiveCheckOut = actualCheckOut;
                }

                // Обработка обеденного перерыва
                double totalHours;

                // Если работник пришел ПОСЛЕ обеда и ушел тоже ПОСЛЕ обеда
                if (effectiveCheckIn.isAfter(lunchEnd) && effectiveCheckOut.isAfter(lunchEnd)) {
                        // Просто считаем время между check in и check out
                        totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                        log.info("Worker arrived after lunch, calculating direct hours: {}", totalHours);
                }
                // Если пришел ДО обеда и ушел ДО обеда
                else if (effectiveCheckIn.isBefore(lunchStart) && effectiveCheckOut.isBefore(lunchStart)) {
                        totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                        log.info("Worker left before lunch, total hours: {}", totalHours);
                }
                // Если пришел ДО обеда и ушел ПОСЛЕ обеда
                else if (effectiveCheckIn.isBefore(lunchStart) && effectiveCheckOut.isAfter(lunchEnd)) {
                        if (isPayingLunch) {
                                totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                                log.info("Company pays for lunch, counting full time: {}", totalHours);
                        } else {
                                double hoursBeforeLunch = java.time.Duration.between(effectiveCheckIn, lunchStart).toMinutes() / 60.0;
                                double hoursAfterLunch = java.time.Duration.between(lunchEnd, effectiveCheckOut).toMinutes() / 60.0;
                                totalHours = hoursBeforeLunch + hoursAfterLunch;
                                log.info("Excluding lunch: before={}, after={}, total={}",
                                        hoursBeforeLunch, hoursAfterLunch, totalHours);
                        }
                }
                // Если пришел ПОСЛЕ обеда начала, но ДО конца обеда
                else if (effectiveCheckIn.isAfter(lunchStart) && effectiveCheckIn.isBefore(lunchEnd)) {
                        if (effectiveCheckOut.isAfter(lunchEnd)) {
                                // Считаем только после обеда
                                totalHours = java.time.Duration.between(lunchEnd, effectiveCheckOut).toMinutes() / 60.0;
                                log.info("Worker came during lunch, counting only after lunch: {}", totalHours);
                        } else {
                                // Работал только во время обеда
                                totalHours = isPayingLunch ?
                                        java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0 : 0.0;
                                log.info("Worker only worked during lunch: {}", totalHours);
                        }
                }
                else if (effectiveCheckIn.isBefore(lunchStart) &&
                        effectiveCheckOut.isAfter(lunchStart) &&
                        effectiveCheckOut.isBefore(lunchEnd)) {
                        // Пришел до обеда, ушел во время обеда
                        double hoursBeforeLunch = java.time.Duration.between(effectiveCheckIn, lunchStart).toMinutes() / 60.0;
                        if (isPayingLunch) {
                                // Если обед оплачивается, добавляем время в обеде
                                double hoursInLunch = java.time.Duration.between(lunchStart, effectiveCheckOut).toMinutes() / 60.0;
                                totalHours = hoursBeforeLunch + hoursInLunch;
                        } else {
                                // Если обед не оплачивается, считаем только до обеда
                                totalHours = hoursBeforeLunch;
                        }
                        log.info("Worker left during lunch: hours before lunch={}, total={}",
                                hoursBeforeLunch, totalHours);
                }
                // Другие случаи
                else {
                        totalHours = java.time.Duration.between(effectiveCheckIn, effectiveCheckOut).toMinutes() / 60.0;
                        log.info("Default case, direct calculation: {}", totalHours);
                }

                attendance.setHoursWorked(totalHours);
                attendance.setOvertimeHours(0.0);

                log.info("Final hours calculated: regular={}, overtime=0.0", totalHours);
        }

        @Transactional
        public OvertimeResponse addManualOvertime(
                Integer attendanceId,
                Double overtimeHours,
                String reason,
                Integer adminId
        ) {
                Timer.Sample timer = metricsService.startTimer();

                try {
                        // 1. Получаем attendance
                        WorkerAttendance attendance = workerAttendanceRepository.findById(attendanceId)
                                .orElseThrow(() -> new EntityNotFoundException("Attendance not found: " + attendanceId));

                        // 2. Проверяем что есть check out
                        if (attendance.getCheckOutTime() == null) {
                                throw new IllegalStateException("Cannot add overtime to active attendance");
                        }

                        // 3. Валидация овертайма
                        if (overtimeHours <= 0 || overtimeHours > 8) {
                                throw new IllegalArgumentException("Overtime hours must be between 0 and 8");
                        }

                        // 4. Проверяем недельный лимит
                        User worker = attendance.getWorker();
                        LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                        LocalDate weekStart = attendanceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                        LocalDate weekEnd = weekStart.plusDays(6);

                        List<WorkerAttendance> weekAttendances = workerAttendanceRepository
                                .findAllByWorkerIdAndCheckInTimeBetween(
                                        worker.getId(),
                                        weekStart.atStartOfDay(),
                                        weekEnd.atTime(LocalTime.MAX));

                        double weeklyRegularHours = weekAttendances.stream()
                                .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                                .sum();

                        double weeklyOvertimeHours = weekAttendances.stream()
                                .mapToDouble(a -> a.getOvertimeHours() != null ? a.getOvertimeHours() : 0.0)
                                .sum();

                        double totalWeeklyHours = weeklyRegularHours + weeklyOvertimeHours;

                        log.info("Weekly hours before overtime: regular={}, overtime={}, total={}",
                                weeklyRegularHours, weeklyOvertimeHours, totalWeeklyHours);

                        // 5. Устанавливаем овертайм
                        attendance.setOvertimeHours(overtimeHours);
                        attendance.setNotes(
                                (attendance.getNotes() != null ? attendance.getNotes() + "; " : "") +
                                        String.format("Manual overtime added: %.2f hours. Reason: %s", overtimeHours, reason)
                        );

                        WorkerAttendance savedAttendance = workerAttendanceRepository.save(attendance);

                        // 6. Пересчитываем payroll
                        WorkerPayroll payroll = workerPayrollRepository
                                .findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(
                                        worker, attendanceDate, attendanceDate)
                                .orElseThrow(() -> new IllegalStateException("Payroll not found for date: " + attendanceDate));

                        updatePayrollCalculations(payroll, savedAttendance);
                        WorkerPayroll savedPayroll = workerPayrollRepository.save(payroll);
                        distributeOnlyNetPay(savedPayroll);

                        log.info("✅ Manual overtime added: {} hours for attendance {} by admin {}",
                                overtimeHours, attendanceId, adminId);

                        metricsService.recordOperationTime(timer, "add_manual_overtime");

                        return OvertimeResponse.builder()
                                .attendanceId(attendanceId)
                                .workerId(worker.getId())
                                .workerName(worker.getFirstName() + " " + worker.getLastName())
                                .date(attendanceDate)
                                .regularHours(savedAttendance.getHoursWorked())
                                .overtimeHours(overtimeHours)
                                .reason(reason)
                                .approvedBy(adminId)
                                .message("Overtime successfully added")
                                .isSuccessful(true)
                                .build();

                } catch (Exception e) {
                        metricsService.recordError("add_manual_overtime", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "add_manual_overtime_failed");
                        throw e;
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

        private boolean validateLocationForPunchIn(PunchInRequest punchInRequest, WorkSite workSite) {

                if(punchInRequest.getLatitude() == null || punchInRequest.getLongitude() == null){
                        throw new IllegalArgumentException("Coordinates cannot be null");
                }

                if(punchInRequest.getLatitude() < -90 || punchInRequest.getLatitude() > 90){
                        throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
                }

                if(punchInRequest.getLongitude() < -180 || punchInRequest.getLongitude() > 180){
                        throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
                }

                boolean isInRadius  = workSiteService.isWithinRadiusForPunchInOut(
                        workSite.getId(),
                        punchInRequest.getLatitude(),
                        punchInRequest.getLongitude()
                );

                if(!isInRadius){
                        throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
                }
                return isInRadius;
        }

        private boolean validateLocationForPunchOut(PunchOutRequest punchOutRequest, WorkSite workSite) {

                if(punchOutRequest.getLatitude() == null || punchOutRequest.getLongitude() == null){
                        throw new IllegalArgumentException("Coordinates cannot be null");
                }

                if(punchOutRequest.getLatitude() < -90 || punchOutRequest.getLatitude() > 90){
                        throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
                }

                if(punchOutRequest.getLongitude() < -180 || punchOutRequest.getLongitude() > 180){
                        throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
                }

                boolean isInRadius = workSiteService.isWithinRadiusForPunchInOut(
                        workSite.getId(),
                        punchOutRequest.getLatitude(),
                        punchOutRequest.getLongitude()
                );

                if(!isInRadius){
                        throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
                }
                return isInRadius;
        }


        private void validatePunchInTime(WorkerSchedule schedule) {

                if (Boolean.TRUE.equals(schedule.getIsDayOff())) {
                        throw new IllegalStateException(
                                String.format("%s is a day off according to schedule", schedule.getDayOfWeek())
                        );
                }

                LocalTime currentTime = LocalTime.now();
                LocalTime earliestAllowed = schedule.getExpectedStartTime().minusMinutes(30);
                LocalTime latestAllowed = schedule.getExpectedEndTime();

                if (currentTime.isBefore(earliestAllowed)) {
                        throw new IllegalStateException(
                                String.format("Too early for punch-in. Allowed from: %s", earliestAllowed)
                        );
                }

                if (currentTime.isAfter(latestAllowed)) {
                        throw new IllegalStateException(
                                String.format("Too late for punch-in! Work day ends at: %s. Current time: %s",
                                        latestAllowed, currentTime)
                        );
                }

                log.info("Punch-in time validation passed. Current: {}, Allowed window: {} to {}",
                        currentTime, earliestAllowed, latestAllowed);
        }



        private WorkerAttendance createAttendance(User user, PunchInRequest punchInRequest,String photoUrl) {
                var foundedWorkSite = workSiteRepository.findById(punchInRequest.getWorkSiteId())
                                .orElseThrow(() -> new RuntimeException("Work site not found"));
                return WorkerAttendance.builder()
                        .worker(user)
                        .checkInTime(LocalDateTime.now())
                        .checkInPhotoUrl(photoUrl)
                        .checkInLatitude(punchInRequest.getLatitude())
                        .checkInLongitude(punchInRequest.getLongitude())
                        .checkInLocation(foundedWorkSite.getAddress())
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
                        .checkInLocation(savedAttendance.getCheckInLocation())
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
                        .checkOutLocation(attendance.getCheckOutLocation())
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
                Timer.Sample timer = metricsService.startTimer();
                try {
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

                        currentPayroll.setRiskClass(worker.getWcRiskClass());
                        workerAttendance.setPeriodStart(currentPayroll.getPeriodStart());
                        workerAttendance.setPeriodEnd(currentPayroll.getPeriodEnd());

                        log.info("Before calculations - payroll ID: {}, base rate: {}",
                                currentPayroll.getId(), currentPayroll.getBaseHourlyRate());

                        BigDecimal dayGrossPay = financeCalculator.calculateGrossPay(
                                currentPayroll.getBaseHourlyRate() != null ? currentPayroll.getBaseHourlyRate() : worker.getBaseHourlyRate(),
                                currentPayroll.getOvertimeRate() != null ? currentPayroll.getOvertimeRate() : worker.getBaseHourlyRate().multiply(BigDecimal.valueOf(1.5)),
                                workerAttendance.getHoursWorked() != null ? workerAttendance.getHoursWorked() : 0.0,
                                workerAttendance.getOvertimeHours() != null ? workerAttendance.getOvertimeHours() : 0.0
                        );

                        workerAttendance.setGrossPayPerDay(dayGrossPay);
                        workerAttendanceRepository.save(workerAttendance);

                        log.info("Saved attendance with gross pay: {}", dayGrossPay);

                        // Теперь обновляем общий payroll
                        updatePayrollCalculations(currentPayroll, workerAttendance);

                        log.info("After calculations - regular hours: {}, overtime: {}, gross pay: {}",
                                currentPayroll.getRegularHours(), currentPayroll.getOvertimeHours(), currentPayroll.getGrossPay());

                        WorkerPayroll savedPayroll = workerPayrollRepository.save(currentPayroll);

                        // ВАЖНОЕ ИЗМЕНЕНИЕ: Распределяем ТОЛЬКО net pay, не трогая уже рассчитанный gross pay
                        distributeOnlyNetPay(savedPayroll);

                        metricsService.recordEarningPeriod(
                                worker.getFirstName() + " " + worker.getLastName(),
                                savedPayroll.getGrossPay().doubleValue(),
                                savedPayroll.getNetPay().doubleValue(),
                                savedPayroll.getRegularHours()
                        );
                        metricsService.recordOperationTime(timer, "update_on_punch_out");

                        return savedPayroll;
                } catch (Exception e) {
                        metricsService.recordOperationTime(timer, "update_on_punch_out_failed");
                        metricsService.recordError("update_on_punch_error", e.getMessage(), e);
                        throw e;
                }
        }

        // Новый метод для распределения ТОЛЬКО net pay без изменения gross pay
        private void distributeOnlyNetPay(WorkerPayroll payroll) {
                log.info("📊 Starting distribution of NET PAY ONLY for period {} to {}",
                        payroll.getPeriodStart(), payroll.getPeriodEnd());

                // Получаем все attendance за период с уже рассчитанным gross pay
                List<WorkerAttendance> periodAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                payroll.getWorker().getId(),
                                payroll.getPeriodStart().atStartOfDay(),
                                payroll.getPeriodEnd().atTime(LocalTime.MAX)
                        )
                        .stream()
                        .filter(a -> a.getGrossPayPerDay() != null && a.getGrossPayPerDay().compareTo(BigDecimal.ZERO) > 0)
                        .sorted(Comparator.comparing(WorkerAttendance::getCheckInTime))
                        .collect(Collectors.toList());

                if (periodAttendances.isEmpty()) {
                        log.warn("No attendances with gross pay found for distribution");
                        return;
                }

                // Считаем общую gross pay за период (сумма всех дней)
                BigDecimal totalDailyGross = periodAttendances.stream()
                        .map(WorkerAttendance::getGrossPayPerDay)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                log.info("Total daily gross pay: {}, Payroll net pay to distribute: {}",
                        totalDailyGross, payroll.getNetPay());

                // Распределяем net pay пропорционально gross pay каждого дня
                BigDecimal totalNetToDistribute = payroll.getNetPay();
                BigDecimal distributedSoFar = BigDecimal.ZERO;

                for (int i = 0; i < periodAttendances.size(); i++) {
                        WorkerAttendance attendance = periodAttendances.get(i);

                        BigDecimal dayNetPay;

                        // Для последнего дня - даём остаток (чтобы избежать ошибок округления)
                        if (i == periodAttendances.size() - 1) {
                                dayNetPay = totalNetToDistribute.subtract(distributedSoFar);
                                log.info("Last day adjustment - giving remaining: {}", dayNetPay);
                        } else {
                                // Для остальных дней - пропорционально
                                BigDecimal dayRatio = attendance.getGrossPayPerDay()
                                        .divide(totalDailyGross, 10, RoundingMode.HALF_UP);
                                dayNetPay = totalNetToDistribute.multiply(dayRatio)
                                        .setScale(2, RoundingMode.HALF_UP);
                                distributedSoFar = distributedSoFar.add(dayNetPay);
                        }

                        // ВАЖНО: Обновляем ТОЛЬКО net pay, НЕ трогая gross pay и hours!
                        attendance.setNetPay(dayNetPay);
                        // НЕ изменяем grossPayPerDay, hoursWorked, overtimeHours!

                        workerAttendanceRepository.save(attendance);

                        log.debug("Day {} - Gross: {} (UNCHANGED), Net: {}, Hours: {} (UNCHANGED)",
                                attendance.getCheckInTime().toLocalDate(),
                                attendance.getGrossPayPerDay(),
                                dayNetPay,
                                attendance.getHoursWorked());
                }

                log.info("✅ Net pay distribution complete without changing gross pay or hours!");
        }


        // ВРЕМЕННОЕ ИСПРАВЛЕНИЕ для метода updatePunchForWorker
// Добавляем вызов updatePayrollOnPunchOut вместо recalculatePayrollForPeriod
        @Transactional(rollbackOn = Exception.class)
        public UpdatePunchForWorkerResponse updatePunchForWorker(
                Integer workerId,
                UpdatePunchForWorkerRequest request) {
                Timer.Sample timer = metricsService.startTimer();
                log.info("Starting updatePunchForWorker for workerId: {}, date: {}, type: {}",
                        workerId, request.getNewPunchDate(), request.getPunchType());

                try {
                        User worker = userRepository.findById(workerId)
                                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + workerId));

                        LocalDate punchDate = request.getNewPunchDate();
                        LocalTime punchTime = request.getNewPunchTime();
                        PunchType punchType = request.getPunchType();

                        // 3. Находим или создаем attendance запись
                        WorkerAttendance attendance = findOrCreateAttendance(
                                worker, punchDate, punchTime, punchType, request.getWorkedHours());

                        // 4. Обработка ночных смен
                        handleOvernightShift(attendance);

                        // 5. Добавляем дополнительную информацию
                        if (request.getNotes() != null) {
                                String existingNotes = attendance.getNotes() != null ? attendance.getNotes() + "; " : "";
                                attendance.setNotes(existingNotes + request.getNotes());
                        }

                        // 6. Устанавливаем work site если указан
                        if (request.getWorkSiteId() != null) {
                                WorkSite workSite = workSiteRepository.findById(request.getWorkSiteId())
                                        .orElseThrow(() -> new EntityNotFoundException("Work site not found"));

                                // Добавляем связь если её нет
                                if (!worker.getWorkSites().contains(workSite)) {
                                        worker.getWorkSites().add(workSite);
                                        workSite.getUsers().add(worker);
                                }
                        }

                        WorkSite workSite = workSiteRepository.findById(request.getWorkSiteId())
                                .orElseThrow(() -> new EntityNotFoundException("Work site not found"));

                        // === ЛОГИКА РАЗДЕЛЯЕТСЯ ЗДЕСЬ ===
                        if (punchType == PunchType.PUNCH_IN) {
                                // ДЛЯ PUNCH IN - ПРОСТО СОХРАНЯЕМ БЕЗ РАСЧЕТОВ
                                log.info("Processing PUNCH IN - no calculations needed");

                                // Сохраняем attendance
                                WorkerAttendance savedAttendance = workerAttendanceRepository.save(attendance);
                                metricsService.recordPunchIn(workSite.getSiteName(), true);
                                metricsService.recordOperationTime(timer, "punch_in");

                                return UpdatePunchForWorkerResponse.builder()
                                        .workerId(workerId)
                                        .workerName(worker.fullName())
                                        .newPunchDate(punchDate)
                                        .newPunchTime(punchTime)
                                        .punchType(punchType)
                                        .hoursWorked(null) // Еще не известно
                                        .overtimeHours(null) // Еще не известно
                                        .message("Punch IN successfully updated")
                                        .isSuccessful(true)
                                        .build();

                        } else { // PUNCH_OUT
                                // ДЛЯ PUNCH OUT - ДЕЛАЕМ ВСЕ РАСЧЕТЫ
                                log.info("Processing PUNCH OUT - calculating hours and payroll");

                                // 7. Рассчитываем часы если есть и check in и check out
                                if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
                                        if (request.getWorkedHours() != null && request.getWorkedHours() > 0) {
                                                // Используем ручные часы
                                                calculateOvertimeForManualHours(attendance, worker, punchDate);
                                        } else {
                                                // Рассчитываем автоматически
                                                calculateWorkedHours(attendance);
                                        }
                                }


                                WorkerAttendance savedAttendance = workerAttendanceRepository.save(attendance);

                                // Вызываем тот же метод, что и в makePunchOut!
                                WorkerPayroll payroll = updatePayrollOnPunchOut(savedAttendance);

                                // Теперь grossPayPerDay и netPay должны быть установлены правильно!
                                log.info("After updatePayrollOnPunchOut - grossPayPerDay: {}, netPay: {}",
                                        savedAttendance.getGrossPayPerDay(),
                                        savedAttendance.getNetPay());
                                // ===== КОНЕЦ ВАЖНОГО ИЗМЕНЕНИЯ =====

                                // 12. Начисляем sick leave
                                double totalHours = (savedAttendance.getHoursWorked() != null ? savedAttendance.getHoursWorked() : 0.0) +
                                        (savedAttendance.getOvertimeHours() != null ? savedAttendance.getOvertimeHours() : 0.0);

                                if (totalHours > 0) {
                                        sickLeaveService.accrueSickLeave(worker.getId(), totalHours);
                                }

                                // 13. Логирование результата
                                log.info("Successfully updated PUNCH OUT for worker {} - regular hours: {}, overtime: {}, " +
                                                "grossPayPerDay: {}, netPay: {}, period: {} to {}",
                                        workerId,
                                        savedAttendance.getHoursWorked(),
                                        savedAttendance.getOvertimeHours(),
                                        savedAttendance.getGrossPayPerDay(),
                                        savedAttendance.getNetPay(),
                                        payroll.getPeriodStart(),
                                        payroll.getPeriodEnd());

                                metricsService.recordPunchOut(
                                        workSite.getSiteName(),
                                        worker.getCompany().getCompanyName(),
                                        worker.getFirstName() + " " + worker.getLastName(),
                                        true,
                                        savedAttendance.getHoursWorked(),
                                        savedAttendance.getOvertimeHours()
                                );

                                metricsService.recordPayrollCalculations(
                                        payroll.getGrossPay(),
                                        payroll.getNetPay(),
                                        payroll.getTotalDeductions()
                                );
                                metricsService.recordOperationTime(timer, "punch_out");

                                return UpdatePunchForWorkerResponse.builder()
                                        .workerId(workerId)
                                        .workerName(worker.fullName())
                                        .newPunchDate(punchDate)
                                        .newPunchTime(punchTime)
                                        .punchType(punchType)
                                        .hoursWorked(savedAttendance.getHoursWorked())
                                        .overtimeHours(savedAttendance.getOvertimeHours())
                                        .message("Punch OUT successfully updated with calculations")
                                        .isSuccessful(true)
                                        .periodStart(payroll.getPeriodStart())
                                        .periodEnd(payroll.getPeriodEnd())
                                        .build();
                }

                } catch (EntityNotFoundException e) {
                        metricsService.recordPunchIn("unknown", false);
                        metricsService.recordError("punch_in_out", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "punch_in_out_failed");
                        log.error("Entity not found: {}", e.getMessage());
                        throw e;
                } catch (IllegalArgumentException | IllegalStateException e) {
                        metricsService.recordPunchIn("unknown", false);
                        metricsService.recordError("punch_in_out", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "punch_in_out_failed");
                        log.error("Validation error: {}", e.getMessage());
                        throw e;
                } catch (Exception e) {
                        metricsService.recordPunchIn("unknown", false);
                        metricsService.recordError("punch_in_out", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "punch_in_out_failed");
                        log.error("Unexpected error updating punch for worker: {}", workerId, e);
                        throw new RuntimeException("Failed to update punch: " + e.getMessage(), e);
                }
        }





        private WorkerAttendance findOrCreateAttendance(
                User worker,
                LocalDate punchDate,
                LocalTime punchTime,
                PunchType punchType,
                Double manualHours) {

                LocalDateTime startOfDay = punchDate.atStartOfDay();
                LocalDateTime endOfDay = punchDate.atTime(LocalTime.MAX);

                // Ищем существующую запись на эту дату
                List<WorkerAttendance> dayAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                worker.getId(), startOfDay, endOfDay);

                WorkerAttendance attendance;

                if (dayAttendances.isEmpty()) {
                        // Нет записей на этот день
                        if (punchType == PunchType.PUNCH_IN) {
                                attendance = WorkerAttendance.builder()
                                        .worker(worker)
                                        .checkInTime(punchDate.atTime(punchTime))
                                        .checkInPhotoUrl("manual-update")
                                        .notes("Manually added punch in")
                                        .build();
                        } else { // PUNCH_OUT
                                // ОБНОВЛЕНО: Получаем шаблон расписания по дню недели
                                WorkerSchedule schedule = getWorkerScheduleForDate(worker, punchDate);

                                // Проверяем, не выходной ли
                                if (Boolean.TRUE.equals(schedule.getIsDayOff())) {
                                        throw new IllegalStateException("Cannot create punch out for day off");
                                }

                                LocalDateTime scheduledStart = punchDate.atTime(schedule.getExpectedStartTime());

                                attendance = WorkerAttendance.builder()
                                        .worker(worker)
                                        .checkInTime(scheduledStart)
                                        .checkInPhotoUrl("manual-update-assumed")
                                        .checkOutTime(punchDate.atTime(punchTime))
                                        .checkOutPhotoUrl("manual-update")
                                        .notes("Manually added punch out with assumed check in at scheduled time")
                                        .build();
                        }
                } else {
                        // Есть запись(и) на этот день
                        if (punchType == PunchType.PUNCH_OUT) {
                                attendance = dayAttendances.stream()
                                        .filter(a -> a.getCheckOutTime() == null)
                                        .findFirst()
                                        .orElse(dayAttendances.get(dayAttendances.size() - 1));

                                attendance.setCheckOutTime(punchDate.atTime(punchTime));
                                if (attendance.getCheckOutPhotoUrl() == null) {
                                        attendance.setCheckOutPhotoUrl("manual-update");
                                }
                                String notes = attendance.getNotes() != null ? attendance.getNotes() + "; " : "";
                                attendance.setNotes(notes + "Manually updated punch out");

                        } else { // PUNCH_IN
                                boolean hasOpenPunchIn = dayAttendances.stream()
                                        .anyMatch(a -> a.getCheckOutTime() == null);

                                if (hasOpenPunchIn) {
                                        log.warn("Worker {} already has an open punch in for date {}, updating existing",
                                                worker.getId(), punchDate);

                                        attendance = dayAttendances.stream()
                                                .filter(a -> a.getCheckOutTime() == null)
                                                .findFirst()
                                                .orElseThrow();

                                        attendance.setCheckInTime(punchDate.atTime(punchTime));
                                        if (attendance.getCheckInPhotoUrl() == null) {
                                                attendance.setCheckInPhotoUrl("manual-update");
                                        }
                                        String notes = attendance.getNotes() != null ? attendance.getNotes() + "; " : "";
                                        attendance.setNotes(notes + "Manually updated punch in time");
                                } else {
                                        attendance = WorkerAttendance.builder()
                                                .worker(worker)
                                                .checkInTime(punchDate.atTime(punchTime))
                                                .checkInPhotoUrl("manual-update")
                                                .notes("Manually added additional punch in")
                                                .build();
                                }
                        }
                }

                // Для PUNCH_OUT с ручными часами
                if (punchType == PunchType.PUNCH_OUT && manualHours != null && manualHours > 0) {
                        attendance.setHoursWorked(manualHours);
                        attendance.setOvertimeHours(0.0);
                }

                return attendance;
        }

        private void calculateOvertimeForManualHours(
                WorkerAttendance attendance,
                User worker,
                LocalDate punchDate) {

                // Сохраняем общее количество часов
                double totalManualHours = attendance.getHoursWorked();

                // Получаем начало недели
                LocalDate weekStart = punchDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate weekEnd = weekStart.plusDays(6);

                // Получаем все attendance за неделю кроме текущего
                List<WorkerAttendance> weekAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                worker.getId(),
                                weekStart.atStartOfDay(),
                                weekEnd.atTime(LocalTime.MAX))
                        .stream()
                        .filter(a -> attendance.getId() == null || !a.getId().equals(attendance.getId()))
                        .collect(Collectors.toList());

                // Считаем недельные часы (регулярные + овертайм)
                double weeklyRegularHours = weekAttendances.stream()
                        .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                        .sum();

                double weeklyOvertimeHours = weekAttendances.stream()
                        .mapToDouble(a -> a.getOvertimeHours() != null ? a.getOvertimeHours() : 0.0)
                        .sum();

                double totalWeeklyHoursBeforeToday = weeklyRegularHours + weeklyOvertimeHours;

                log.info("Weekly hours calculation - regular: {}, overtime: {}, total: {}",
                        weeklyRegularHours, weeklyOvertimeHours, totalWeeklyHoursBeforeToday);

                // Определяем сколько часов до 40 осталось
                double hoursUntil40 = Math.max(0, 40.0 - weeklyRegularHours);

                // Распределяем часы
                if (hoursUntil40 >= totalManualHours) {
                        // Все часы идут как регулярные
                        attendance.setHoursWorked(totalManualHours);
                        attendance.setOvertimeHours(0.0);
                } else {
                        // Часть идет как регулярные, часть как овертайм
                        attendance.setHoursWorked(hoursUntil40);
                        attendance.setOvertimeHours(totalManualHours - hoursUntil40);
                }

                log.info("Manual hours distributed - regular: {}, overtime: {}",
                        attendance.getHoursWorked(), attendance.getOvertimeHours());
        }

        private WorkerPayroll findOrCreatePayrollForDate(User worker, LocalDate date) {
                // Сначала ищем существующий payroll период
                Optional<WorkerPayroll> existingPayroll = workerPayrollRepository
                        .findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(
                                worker, date, date);

                if (existingPayroll.isPresent()) {
                        return existingPayroll.get();
                }

                // Если не нашли, создаем новый
                return createNewPayrollPeriod(worker, date);
        }

        private void recalculatePayrollForPeriod(WorkerPayroll payroll) {

                BigDecimal UI_CAP = BigDecimal.valueOf(12_300);

                BigDecimal uiRate = BigDecimal.valueOf(0.005);

                log.info("Recalculating payroll for period: {} to {}",
                        payroll.getPeriodStart(), payroll.getPeriodEnd());

                // Получаем все attendance записи за период
                List<WorkerAttendance> periodAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                payroll.getWorker().getId(),
                                payroll.getPeriodStart().atStartOfDay(),
                                payroll.getPeriodEnd().atTime(LocalTime.MAX));

                // Суммируем часы
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

                // Округляем до 2 знаков
                BigDecimal preciseRegularHours = BigDecimal.valueOf(totalRegularHours)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal preciseOvertimeHours = BigDecimal.valueOf(totalOvertimeHours)
                        .setScale(2, RoundingMode.HALF_UP);



                payroll.setRegularHours(preciseRegularHours.doubleValue());
                payroll.setOvertimeHours(preciseOvertimeHours.doubleValue());
                payroll.setTotalHours(preciseRegularHours.add(preciseOvertimeHours).doubleValue());

                // Устанавливаем ставки если их нет
                if (payroll.getBaseHourlyRate() == null ||
                        payroll.getBaseHourlyRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setBaseHourlyRate(payroll.getWorker().getBaseHourlyRate());
                }

                if (payroll.getOvertimeRate() == null ||
                        payroll.getOvertimeRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setOvertimeRate(payroll.getBaseHourlyRate().multiply(BigDecimal.valueOf(1.5)));
                }

                BigDecimal regularPay = preciseRegularHours
                        .multiply(payroll.getBaseHourlyRate())
                        .setScale(2, RoundingMode.HALF_UP);

                BigDecimal overtimePay = preciseOvertimeHours
                        .multiply(payroll.getOvertimeRate())
                        .setScale(2, RoundingMode.HALF_UP);

                // Устанавливаем рассчитанные значения
                payroll.setRegularPay(regularPay);
                payroll.setOvertimePay(overtimePay);

                int year = payroll.getPeriodStart().getYear();
                LocalDate yearStart = LocalDate.of(year, 1, 1);
                LocalDate beforePeriod = payroll.getPeriodStart().minusDays(1);
                BigDecimal ytdGrossBefore = workerPayrollRepository
                        .findAllByWorkerIdAndPeriodEndBetween(
                                payroll.getWorker().getId(),
                                yearStart,
                                beforePeriod
                        )
                        .stream()
                        .map(p -> p.getGrossPay().min(UI_CAP))
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


// 4. Сколько ещё из капа осталось “налоговать” в этом периоде
                BigDecimal remainingCap = UI_CAP.subtract(ytdGrossBefore);
                if (remainingCap.compareTo(BigDecimal.ZERO) < 0) {
                        remainingCap = BigDecimal.ZERO;
                }
                // рассчитываем net и gross
                PayStubResponse taxCalculation = financeCalculator.calculateNetPayWithSeparateHours(
                        payroll.getWorker(),
                        payroll.getBaseHourlyRate(),
                        payroll.getOvertimeRate(),
                        payroll.getRegularHours(),
                        payroll.getOvertimeHours(),
                        calculateYtdPFL(payroll.getWorker(), payroll.getPeriodStart()),
                        calculateYtdSocialSecurity(payroll.getWorker(), payroll.getPeriodStart()),
                        calculateYtdMedicare(payroll.getWorker(), payroll.getPeriodStart())
                );

                BigDecimal calculatedGross = taxCalculation.getGrossPay();
                payroll.setGrossPay(calculatedGross);

                BigDecimal taxableThisPeriod = payroll.getGrossPay().min(remainingCap);

                BigDecimal uiWithholding = taxableThisPeriod
                        .multiply(uiRate)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal netAfterUI = taxCalculation.getNetPay()
                        .subtract(uiWithholding)
                        .setScale(2, RoundingMode.HALF_UP);
                payroll.setNetPay(netAfterUI);

                // Обновляем payroll
             //   payroll.setGrossPay(taxCalculation.getGrossPay());
                payroll.setNetPay(taxCalculation.getNetPay().subtract(uiWithholding).setScale(2, RoundingMode.HALF_UP));
                payroll.setMedicare(taxCalculation.getMedicare());
                payroll.setSocialSecurityEmployee(taxCalculation.getSocialSecurity());
                payroll.setFederalWithholding(taxCalculation.getFederalTax());
                payroll.setNyStateWithholding(taxCalculation.getStateTax());
                payroll.setNyLocalWithholding(taxCalculation.getNycTax());
                payroll.setNyDisabilityWithholding(taxCalculation.getDisability());
                payroll.setNyPaidFamilyLeave(taxCalculation.getPfl());
                payroll.setNyUnemploymentWithholding(uiWithholding);
                payroll.setTotalDeductions(taxCalculation.getTotalDeductions().add(uiWithholding));

                // Сохраняем
                workerPayrollRepository.save(payroll);
                distributePayrollToAttendances(payroll);
                // Обновляем дневные расчеты для каждого attendance
             //   updateDailyPayForAttendances(periodAttendances, payroll, taxCalculation);
        }


        private void validatePunchUpdate(UpdatePunchForWorkerRequest request, User worker) {
                LocalDate punchDate = request.getNewPunchDate();

                // 1. Проверка даты не в будущем
                if (punchDate.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException("Cannot create punch records for future dates");
                }

                // 2. Проверка даты не слишком старая (например, не старше 30 дней)
                if (punchDate.isBefore(LocalDate.now().minusDays(30))) {
                        throw new IllegalArgumentException("Cannot modify punch records older than 30 days");
                }

                // 3. Проверка времени
                LocalTime punchTime = request.getNewPunchTime();
                if (punchTime == null) {
                        throw new IllegalArgumentException("Punch time is required");
                }

                // 4. Валидация часов при ручном вводе
                if (request.getWorkedHours() != null) {
                        if (request.getWorkedHours() < 0) {
                                throw new IllegalArgumentException("Worked hours cannot be negative");
                        }
                        if (request.getWorkedHours() > 24) {
                                throw new IllegalArgumentException("Worked hours cannot exceed 24 hours per day");
                        }
                }

                // 5. ОБНОВЛЕНО: Проверка существования ШАБЛОНА расписания
                DayOfWeek dayOfWeek = punchDate.getDayOfWeek();
                WorkerSchedule schedule = workerScheduleRepository
                        .findByWorkerAndDayOfWeekAndIsTemplateTrue(worker, dayOfWeek)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("No schedule template found for %s", dayOfWeek)
                        ));

                // Проверяем, не выходной ли день
                if (Boolean.TRUE.equals(schedule.getIsDayOff())) {
                        // Можно разрешить или запретить punch на выходные
                        log.warn("Attempting to punch on day off: {}", dayOfWeek);
                        // throw new IllegalStateException("Cannot punch on day off");
                }
        }

        private void handleOvernightShift(WorkerAttendance attendance) {
                LocalDateTime checkIn = attendance.getCheckInTime();
                LocalDateTime checkOut = attendance.getCheckOutTime();

                if (checkIn != null && checkOut != null) {
                        // Проверяем, если punch out на следующий день
                        if (checkOut.toLocalDate().isAfter(checkIn.toLocalDate())) {
                                log.warn("Overnight shift detected for attendance ID: {}", attendance.getId());

                                // Ограничиваем до конца дня
                                LocalDateTime endOfDay = checkIn.toLocalDate().atTime(23, 59, 59);
                                attendance.setCheckOutTime(endOfDay);
                                attendance.setNotes((attendance.getNotes() != null ? attendance.getNotes() + "; " : "") +
                                        "Overnight shift adjusted to end of day");
                        }
                }
        }

        private void updateDailyPayForAttendances(
                List<WorkerAttendance> attendances,
                WorkerPayroll payroll,
                PayStubResponse periodTaxes) {

                // Просто вызываем общий метод распределения
                distributePayrollToAttendances(payroll);
        }

        private WorkerPayroll createNewPayrollPeriod(User worker, LocalDate date) {
                var company = worker.getCompany();
                var payPeriodType = company.getCompanyPaymentPosition();

                LocalDate periodStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate periodEnd;

                // 2. Границы периода по настройке компании
                switch (payPeriodType) {
                        case WEEKLY -> {
                                periodEnd = periodStart.plusDays(6);      // 7 дней
                        }
                        case BIWEEKLY -> {
                                LocalDate first = company.getFirstBiweeklyDate();
                                long weeksBetween = ChronoUnit.WEEKS.between(first, periodStart);
                                if (weeksBetween % 2 != 0) {
                                        // нечётная biweek → сдвигаем старт на неделю вперёд
                                        periodStart = periodStart.plusWeeks(1);
                                }
                                periodEnd = periodStart.plusDays(13);    // 14 дней
                        }
                        default -> throw new IllegalStateException(
                                "Unsupported pay period for company: " + payPeriodType);
                }


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
                        .riskClass(worker.getWcRiskClass())
                        .build();
        }

        @Transactional
        public void updatePayrollCalculations(WorkerPayroll payroll, WorkerAttendance currentAttendance) {
                Timer.Sample timer = metricsService.startTimer();
                log.info("🔄 Starting payroll calculations with current attendance - hours worked: {}, overtime: {}",
                        currentAttendance.getHoursWorked(), currentAttendance.getOvertimeHours());

                try {
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

                        // Округление часов до 2 знаков после запятой для точности
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

                        BigDecimal regularPay = BigDecimal.valueOf(totalRegularHours)
                                .multiply(payroll.getBaseHourlyRate())
                                .setScale(2, RoundingMode.HALF_UP);

                        BigDecimal overtimePay = BigDecimal.valueOf(totalOvertimeHours)
                                .multiply(payroll.getOvertimeRate())
                                .setScale(2, RoundingMode.HALF_UP);

                        // Устанавливаем рассчитанные значения
                        payroll.setRegularPay(regularPay);
                        payroll.setOvertimePay(overtimePay);

                        log.info("💰 Pay breakdown - Regular: ${} ({} hrs × ${}), Overtime: ${} ({} hrs × ${})",
                                regularPay, totalRegularHours, payroll.getBaseHourlyRate(),
                                overtimePay, totalOvertimeHours, payroll.getOvertimeRate());

                    // Безопасно получаем YTD значения с защитой от null
                    BigDecimal ytdPFL = calculateYtdPFL(payroll.getWorker(), payroll.getPeriodStart());
                    BigDecimal ytdSS = calculateYtdSocialSecurity(payroll.getWorker(), payroll.getPeriodStart());
                    BigDecimal ytdMedicare = calculateYtdMedicare(payroll.getWorker(), payroll.getPeriodStart());

                    PayStubResponse response = financeCalculator.calculateNetPayWithSeparateHours(
                            payroll.getWorker(),
                            payroll.getBaseHourlyRate(),
                            payroll.getOvertimeRate(),
                            payroll.getRegularHours(),
                            payroll.getOvertimeHours(),
                            ytdPFL != null ? ytdPFL : BigDecimal.ZERO,
                            ytdSS != null ? ytdSS : BigDecimal.ZERO,
                            ytdMedicare != null ? ytdMedicare : BigDecimal.ZERO
                    );



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

                        // ВАЖНОЕ ИЗМЕНЕНИЕ: Рассчитываем grossPayPerDay ТОЛЬКО для текущего attendance
                        // НЕ трогаем уже сохраненные grossPayPerDay для других attendance!
                        BigDecimal dayGrossPay = financeCalculator.calculateGrossPay(
                                payroll.getBaseHourlyRate(),
                                payroll.getOvertimeRate(),
                                currentAttendance.getHoursWorked() != null ? currentAttendance.getHoursWorked() : 0.0,
                                currentAttendance.getOvertimeHours() != null ? currentAttendance.getOvertimeHours() : 0.0
                        );

                        currentAttendance.setGrossPayPerDay(dayGrossPay);

                        // НЕ устанавливаем netPay здесь! Это будет сделано в distributePayrollToAttendances
                        // currentAttendance.setNetPay(response.getNetPay()); // УДАЛЕНО!

                        workerAttendanceRepository.save(currentAttendance);

                        log.info("✅ Daily gross pay saved for current attendance: {}", dayGrossPay);
                        log.info("✅ Total payroll updated - gross: {}, net: {}", response.getGrossPay(), response.getNetPay());

                        metricsService.recordPayrollCalculations(
                                payroll.getWorker().getFirstName() + " " + payroll.getWorker().getLastName(),
                                payroll.getWorker().getCompany().getCompanyName(),
                                payroll.getGrossPay().doubleValue(),
                                payroll.getNetPay().doubleValue(),
                                payroll.getRegularHours(),
                                payroll.getMedicare().doubleValue(),
                                payroll.getSocialSecurityEmployee().doubleValue(),
                                payroll.getFederalWithholding().doubleValue(),
                                payroll.getNyStateWithholding().doubleValue(),
                                payroll.getNyLocalWithholding().doubleValue(),
                                payroll.getNyDisabilityWithholding().doubleValue(),
                                payroll.getNyPaidFamilyLeave().doubleValue(),
                                payroll.getTotalDeductions().doubleValue()
                        );
                        metricsService.recordOperationTime(timer, "update_payroll_calculations");
                } catch(Exception e){
                        metricsService.recordError("update_payroll_calculations", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "update_payroll_calculations");
                        throw e;
                }
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


        private BigDecimal calculateYtdMedicare(User user, LocalDate untilDate) {
                return workerPayrollRepository
                        .findAllByWorkerIdAndPeriodEndLessThanEqual(user.getId(), untilDate)
                        .stream()
                        .map(p -> p.getMedicare() != null ? p.getMedicare() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        }


        public void distributePayrollToAttendances(WorkerPayroll payroll) {
                log.info("📊 Starting distribution of payroll to daily attendances for period {} to {}",
                        payroll.getPeriodStart(), payroll.getPeriodEnd());

                // Получаем все attendance за период
                List<WorkerAttendance> periodAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                payroll.getWorker().getId(),
                                payroll.getPeriodStart().atStartOfDay(),
                                payroll.getPeriodEnd().atTime(LocalTime.MAX)
                        )
                        .stream()
                        // ВАЖНО: Берем ВСЕ attendance, даже те, у которых еще нет gross pay
                        .sorted(Comparator.comparing(WorkerAttendance::getCheckInTime))
                        .collect(Collectors.toList());

                if (periodAttendances.isEmpty()) {
                        log.warn("No attendances found for distribution");
                        return;
                }

                // Сначала убеждаемся, что у всех attendance есть gross pay
                for (WorkerAttendance attendance : periodAttendances) {
                        // Если gross pay еще не рассчитан - рассчитываем
                        if (attendance.getGrossPayPerDay() == null || attendance.getGrossPayPerDay().compareTo(BigDecimal.ZERO) == 0) {
                                if (attendance.getHoursWorked() != null && attendance.getHoursWorked() > 0) {
                                        BigDecimal dayGrossPay = financeCalculator.calculateGrossPay(
                                                payroll.getBaseHourlyRate(),
                                                payroll.getOvertimeRate(),
                                                attendance.getHoursWorked(),
                                                attendance.getOvertimeHours() != null ? attendance.getOvertimeHours() : 0.0
                                        );
                                        attendance.setGrossPayPerDay(dayGrossPay);
                                        workerAttendanceRepository.save(attendance);
                                        log.info("Calculated missing gross pay for attendance on {}: {}",
                                                attendance.getCheckInTime().toLocalDate(), dayGrossPay);
                                }
                        }
                }

                // Теперь фильтруем только те, у которых есть gross pay для распределения net pay
                List<WorkerAttendance> attendancesWithGrossPay = periodAttendances.stream()
                        .filter(a -> a.getGrossPayPerDay() != null && a.getGrossPayPerDay().compareTo(BigDecimal.ZERO) > 0)
                        .collect(Collectors.toList());

                if (attendancesWithGrossPay.isEmpty()) {
                        log.warn("No attendances with gross pay found for net pay distribution");
                        return;
                }

                // Считаем общую gross pay за период (сумма всех дней)
                BigDecimal totalDailyGross = attendancesWithGrossPay.stream()
                        .map(WorkerAttendance::getGrossPayPerDay)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                log.info("Total daily gross pay: {}, Payroll net pay to distribute: {}",
                        totalDailyGross, payroll.getNetPay());

                // Распределяем net pay пропорционально gross pay каждого дня
                BigDecimal totalNetToDistribute = payroll.getNetPay();
                BigDecimal distributedSoFar = BigDecimal.ZERO;

                for (int i = 0; i < attendancesWithGrossPay.size(); i++) {
                        WorkerAttendance attendance = attendancesWithGrossPay.get(i);

                        BigDecimal dayNetPay;

                        // Для последнего дня - даём остаток (чтобы избежать ошибок округления)
                        if (i == attendancesWithGrossPay.size() - 1) {
                                dayNetPay = totalNetToDistribute.subtract(distributedSoFar);
                                log.info("Last day adjustment - giving remaining: {}", dayNetPay);
                        } else {
                                // Для остальных дней - пропорционально
                                BigDecimal dayRatio = attendance.getGrossPayPerDay()
                                        .divide(totalDailyGross, 10, RoundingMode.HALF_UP);
                                dayNetPay = totalNetToDistribute.multiply(dayRatio)
                                        .setScale(2, RoundingMode.HALF_UP);
                                distributedSoFar = distributedSoFar.add(dayNetPay);
                        }

                        // ВАЖНО: Сохраняем ТОЛЬКО net pay, не трогая существующие gross pay и hours
                        attendance.setNetPay(dayNetPay);
                        workerAttendanceRepository.save(attendance);

                        log.debug("Day {} - Gross: {} (preserved), Net: {}, Hours: {} (preserved), Overtime: {} (preserved)",
                                attendance.getCheckInTime().toLocalDate(),
                                attendance.getGrossPayPerDay(),
                                dayNetPay,
                                attendance.getHoursWorked(),
                                attendance.getOvertimeHours());
                }

                // Проверка суммы
                BigDecimal checkSum = attendancesWithGrossPay.stream()
                        .map(WorkerAttendance::getNetPay)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                log.info("✅ Distribution complete! Distributed {} to {} attendances. Check sum: {} (should equal {})",
                        totalNetToDistribute, attendancesWithGrossPay.size(), checkSum, totalNetToDistribute);

                if (checkSum.compareTo(totalNetToDistribute) != 0) {
                        log.error("❌ ERROR: Distribution sum mismatch! Expected: {}, Actual: {}",
                                totalNetToDistribute, checkSum);
                }
        }

        @Transactional
        public void useSickLeave(Integer userId,
                                 BigDecimal hoursToUse,
                                 LocalDate sickDate) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + userId));

                BigDecimal available = user.getSickLeaveAccrued().subtract(user.getSickLeaveUsed());
                if (available.compareTo(hoursToUse) < 0) {
                        throw new IllegalStateException(
                                String.format("Недостаточно sick-часов: доступно %.2f, запрошено %.2f",
                                        available.doubleValue(), hoursToUse.doubleValue()));
                }

                WorkerPayroll payroll = workerPayrollRepository
                        .findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(
                                user, sickDate, sickDate)
                        .orElseGet(() -> createNewPayrollPeriod(user, sickDate));

                LocalDateTime startOfDay = sickDate.atStartOfDay();
                LocalDateTime endOfDay   = sickDate.atTime(LocalTime.MAX);
                WorkerAttendance attendance = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(userId, startOfDay, endOfDay)
                        .stream()
                        .findFirst()
                        .orElseGet(() -> {
                                WorkerAttendance wa = new WorkerAttendance();
                                wa.setWorker(user);
                                wa.setPeriodStart(payroll.getPeriodStart());
                                wa.setPeriodEnd(payroll.getPeriodEnd());
                                return wa;
                        });

                double existed = attendance.getHoursWorked() != null
                        ? attendance.getHoursWorked()
                        : 0.0;
                if (existed + hoursToUse.doubleValue() > 8.0) {
                        throw new IllegalStateException(
                                String.format("В день нельзя больше 8 часов sick: уже %.2f, пытаемся добавить %.2f",
                                        existed, hoursToUse.doubleValue()));
                }

                // 5. Списываем часы в attendance и сохраняем
                attendance.setHoursWorked(existed + hoursToUse.doubleValue());
                attendance.setNotes(
                        Optional.ofNullable(attendance.getNotes()).orElse("") +
                                (attendance.getNotes() != null ? "; " : "") +
                                "SICK leave"
                );
                attendance.setPeriodStart(payroll.getPeriodStart());
                attendance.setPeriodEnd(payroll.getPeriodEnd());
                workerAttendanceRepository.save(attendance);

                updatePayrollCalculations(payroll, attendance);
                WorkerPayroll saved = workerPayrollRepository.save(payroll);
                distributePayrollToAttendances(saved);
        }



        public PageResponse<AttendanceResponse> findAllAttendanceAppOwner(
                Authentication authentication,
                int page,
                int size) {

                checkIsUserHasAdminRoleAndBusinessOwner(authentication);

                // ✅ ЗАГРУЖАЕМ ВСЁ С JOIN FETCH
                List<WorkerAttendance> allAttendances = workerAttendanceRepository.findAllWithDetails();

                // Сортируем и делаем пагинацию вручную
                List<WorkerAttendance> sorted = allAttendances.stream()
                        .sorted((a1, a2) -> a2.getCheckInTime().compareTo(a1.getCheckInTime()))
                        .toList();

                int start = page * size;
                int end = Math.min(start + size, sorted.size());
                List<WorkerAttendance> pagedAttendances = start < end ? sorted.subList(start, end) : List.of();

                // ✅ Теперь mapper НЕ вызовет N+1!
                List<AttendanceResponse> attendanceResponses = pagedAttendances.stream()
                        .map(workAttendanceMapper::toCompanyWorkerResponse)
                        .toList();

                int totalPages = (int) Math.ceil((double) sorted.size() / size);

                return new PageResponse<>(
                        attendanceResponses,
                        page,
                        size,
                        sorted.size(),
                        totalPages,
                        page == 0,
                        end >= sorted.size()
                );
        }

    public PageResponse<AttendanceResponse> findAllAttendanceAdmin(Authentication authentication, int page, int size) {
        User admin = checkIsUserHasAdminRoleAndBusinessOwner(authentication);

        if(!admin.isAdmin()) {
            throw new AccessDeniedException("You dont have permission!");
        }

        // ✅ ПРОВЕРКА НА NULL
        if(admin.getCompany() == null) {
            log.error("Admin has no company assigned! Admin ID: {}", admin.getId());
            return new PageResponse<>(List.of(), page, size, 0, 0, true, true);
        }

        Integer companyId = admin.getCompany().getId();
        log.info("Loading attendance for company ID: {}", companyId);

        List<WorkerAttendance> allAttendances = workerAttendanceRepository
                .findAllAttendanceByCompanyId(companyId);

        log.info("Found {} attendance records", allAttendances.size());

        // ✅ ФИЛЬТРУЙ NULL И СОРТИРУЙ
        List<WorkerAttendance> sorted = allAttendances.stream()
                .filter(a -> a.getCheckInTime() != null)  // ← ФИЛЬТР NULL
                .sorted((a1, a2) -> a2.getCheckInTime().compareTo(a1.getCheckInTime()))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, sorted.size());
        List<WorkerAttendance> pagedAttendances = start < end ? sorted.subList(start, end) : List.of();

        List<AttendanceResponse> attendanceResponses = pagedAttendances.stream()
                .map(workAttendanceMapper::toCompanyWorkerResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) sorted.size() / size);

        return new PageResponse<>(
                attendanceResponses,
                page,
                size,
                sorted.size(),
                totalPages,
                page == 0,
                end >= sorted.size()
        );
    }
        // В WorkAttendanceService
        public WorkerPhotosResponse getPhotosForWorkerByDate(
                Integer workerId,
                LocalDate date) {

                Timer.Sample timer = metricsService.startTimer();

                try {
                        User worker = userRepository.findById(workerId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Worker not found with id: " + workerId));

                        LocalDateTime startOfDay = date.atStartOfDay();
                        LocalDateTime endOfDay = date.atTime(23, 59, 59);

                        // ✅ Получаем ВСЕ записи за день
                        List<WorkerAttendance> attendances = workerAttendanceRepository
                                .findAllByWorkerIdAndDateRange(workerId, startOfDay, endOfDay);

                        if (attendances.isEmpty()) {
                                log.info("No attendance found for worker {} on date {}", workerId, date);
                                return WorkerPhotosResponse.builder()
                                        .workerId(workerId)
                                        .workerName(worker.getFirstName() + " " + worker.getLastName())
                                        .date(date.toString())
                                        .checkInPhotoUrl(null)
                                        .checkOutPhotoUrl(null)
                                        .build();
                        }

                        // ✅ Берем первую запись (самый ранний check-in)
                        WorkerAttendance firstAttendance = attendances.get(0);

                        // ✅ Берем последнюю запись (самый поздний check-out)
                        WorkerAttendance lastAttendance = attendances.get(attendances.size() - 1);

                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        metricsService.recordOperationTime(timer, "get_photos_by_date");

                        return WorkerPhotosResponse.builder()
                                .workerId(workerId)
                                .workerName(worker.getFirstName() + " " + worker.getLastName())
                                .date(date.toString())
                                .checkInPhotoUrl(firstAttendance.getCheckInPhotoUrl())
                                .checkOutPhotoUrl(lastAttendance.getCheckOutPhotoUrl())
                                .checkInTime(firstAttendance.getCheckInTime() != null ?
                                        firstAttendance.getCheckInTime().format(timeFormatter) : null)
                                .checkOutTime(lastAttendance.getCheckOutTime() != null ?
                                        lastAttendance.getCheckOutTime().format(timeFormatter) : null)
                                .build();

                } catch (Exception e) {
                        metricsService.recordError("get_photos_by_date", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "get_photos_by_date_failed");
                        log.error("Error getting photos for worker {} on date {}", workerId, date, e);
                        throw e;
                }
        }

        // ✅ НОВЫЙ метод для получения фото ПО ATTENDANCE ID
        public WorkerPhotosResponse getPhotosByAttendanceId(Integer attendanceId) {
                Timer.Sample timer = metricsService.startTimer();

                try {
                        WorkerAttendance attendance = workerAttendanceRepository
                                .findByAttendanceId(attendanceId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Attendance not found with id: " + attendanceId));

                        User worker = attendance.getWorker();
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                        metricsService.recordOperationTime(timer, "get_photos_by_attendance_id");

                        return WorkerPhotosResponse.builder()
                                .workerId(worker.getId())
                                .workerName(worker.getFirstName() + " " + worker.getLastName())
                                .date(attendance.getCheckInTime().toLocalDate().toString())
                                .checkInPhotoUrl(attendance.getCheckInPhotoUrl())
                                .checkOutPhotoUrl(attendance.getCheckOutPhotoUrl())
                                .checkInTime(attendance.getCheckInTime() != null ?
                                        attendance.getCheckInTime().format(timeFormatter) : null)
                                .checkOutTime(attendance.getCheckOutTime() != null ?
                                        attendance.getCheckOutTime().format(timeFormatter) : null)
                                .build();

                } catch (Exception e) {
                        metricsService.recordError("get_photos_by_attendance_id", e.getMessage(), e);
                        metricsService.recordOperationTime(timer, "get_photos_by_attendance_id_failed");
                        log.error("Error getting photos for attendance id {}", attendanceId, e);
                        throw e;
                }
        }

        private User checkIsUserHasAdminRoleAndBusinessOwner(Authentication authentication) {
                User user = (User) authentication.getPrincipal();

                boolean isAppOwner = user.getRoles().stream()
                        .anyMatch(role -> "AppOwner".equals(role.getName()));

                if(!user.isAdmin() && !user.isBusinessOwner() && !isAppOwner) {
                        throw new AccessDeniedException("You dont have permission for this operation!");
                }
                return user;
        }


}
