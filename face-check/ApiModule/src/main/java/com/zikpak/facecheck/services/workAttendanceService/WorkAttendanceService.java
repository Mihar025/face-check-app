package com.zikpak.facecheck.services.workAttendanceService;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.worker.DailyFinanceInfo;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.security.AuthenticationServiceImpl;
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
        private final AuthenticationServiceImpl authenticationService;

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
                        //  workSite.setWorkerDidPunchIn(true);
                        workSite.setIsWorkerDidPunchIn(Boolean.TRUE);
                        return createSuccessResponseForPunchIn(user, workSite, savedAttendance);

                }catch (Exception e){
                        log.error(e.getMessage());
                        return createErrorResponseForPunchIn(e.getMessage());
                }

        }

        @Transactional
        public PunchOutResponse makePunchOut(Authentication authentication, PunchOutRequest punchOutRequest) {
                try {
                        User user = validateAndGetUserByEmail(authentication);
                        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

                        WorkerAttendance existingAttendance = workerAttendanceRepository
                                .findTodayActivePunchIn(user, startOfDay, endOfDay)
                                .orElseThrow(() -> new IllegalStateException("No active punch in found for today!"));

                        WorkSite workSite = validateAndGetWorkSite(punchOutRequest.getWorkSiteId());
                        // Получаем расписание на сегодня
                        LocalDate today = LocalDate.now();
                        WorkerSchedule schedule = getWorkerScheduleForDate(user, today);

                        // Проверяем время для punch-out
                        validatePunchOutTime(schedule);
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

                        log.info("Before updatePayrollOnPunchOut - attendance hours: {}, overtime: {}",
                                savedAttendance.getHoursWorked(), savedAttendance.getOvertimeHours());


                        WorkerPayroll payroll = updatePayrollOnPunchOut(savedAttendance);
                        log.info("After updatePayrollOnPunchOut - payroll ID: {}, regular hours: {}, overtime: {}, gross pay: {}",
                                payroll.getId(), payroll.getRegularHours(), payroll.getOvertimeHours(), payroll.getGrossPay());

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

                // Проверяем каждую запись подробно
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

                // Считаем тотальные значения
                double totalHoursWorked = weeklyAttendances.stream()
                        .mapToDouble(a -> a.getHoursWorked() != null ? a.getHoursWorked() : 0.0)
                        .sum();

                BigDecimal totalGrossPay = weeklyAttendances.stream()
                        .map(a -> a.getGrossPayPerDay() != null ? a.getGrossPayPerDay() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalNetPay = weeklyAttendances.stream()
                        .map(a -> a.getNetPay() != null ? a.getNetPay() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Группируем по дням
                Map<LocalDate, List<WorkerAttendance>> attendanceByDay = weeklyAttendances.stream()
                        .collect(Collectors.groupingBy(a -> a.getCheckInTime().toLocalDate()));

                // Создаем список с информацией по каждому дню
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


        private void calculateWorkedHours(WorkerAttendance attendance) {
                User worker = attendance.getWorker();
                LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();

                // Получаем расписание работника на этот день
                WorkerSchedule schedule = getWorkerScheduleForDate(worker, attendanceDate);

                LocalDateTime checkInTime = attendance.getCheckInTime();
                LocalDateTime checkOutTime = attendance.getCheckOutTime();

                log.info("Calculating hours for worker {} - Schedule: {} to {}, Actual: {} to {}",
                        worker.getId(),
                        schedule.getExpectedStartTime(),
                        schedule.getExpectedEndTime(),
                        checkInTime.toLocalTime(),
                        checkOutTime.toLocalTime());

                // Получаем время начала и конца смены для этого дня
                LocalDateTime scheduleStart = checkInTime.with(schedule.getExpectedStartTime());
                LocalDateTime scheduleEnd = checkInTime.with(schedule.getExpectedEndTime());

                // Если пришел раньше начала смены, считаем с начала смены
                LocalDateTime effectiveStartTime;
                if (checkInTime.isBefore(scheduleStart)) {
                        effectiveStartTime = scheduleStart;
                } else {
                        effectiveStartTime = checkInTime;
                }

                // Если ушел позже конца смены, считаем по конец смены
                LocalDateTime effectiveEndTime;
                if (checkOutTime.isAfter(scheduleEnd)) {
                        effectiveEndTime = scheduleEnd;
                } else {
                        effectiveEndTime = checkOutTime;
                }

                log.info("Effective times - Start: {}, End: {}", effectiveStartTime, effectiveEndTime);


                double totalHours = java.time.Duration.between(effectiveStartTime, effectiveEndTime)
                        .toMinutes() / 60.0;


                if (totalHours > 6.0) {
                        totalHours -= 0.75;
                        log.info("Deducted 1 hour for lunch break");
                }

                log.info("Regular hours calculated: {}", totalHours);
                attendance.setHoursWorked(totalHours);

                // Считаем сверхурочные, если человек реально работал после окончания смены
                if (checkOutTime.isAfter(scheduleEnd)) {
                        double overtimeHours = java.time.Duration.between(scheduleEnd, checkOutTime)
                                .toMinutes() / 60.0;
                        attendance.setOvertimeHours(overtimeHours);
                        log.info("Overtime hours: {}", overtimeHours);
                } else {
                        attendance.setOvertimeHours(0.0);
                }

        }


        private User validateAndGetUser(Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                if(user == null || user.getId() == null){
                        throw new RuntimeException("User not found");
                }
                return userRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
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
                LocalTime earliestAllowed = schedule.getExpectedStartTime().minusMinutes(30); // За 30 минут до начала
                LocalTime latestAllowed = schedule.getExpectedStartTime().plusHours(1); // До часа после начала

                if (currentTime.isBefore(earliestAllowed)) {
                        throw new IllegalStateException(
                                "Too early for punch-in. Allowed from: " + earliestAllowed
                        );
                }

                if (currentTime.isAfter(latestAllowed)) {
                        throw new IllegalStateException(
                                "Too late for punch-in. Last allowed time was: " + latestAllowed
                        );
                }
        }

        private void validatePunchOutTime(WorkerSchedule schedule) {
                LocalTime currentTime = LocalTime.now();
                LocalTime earliestAllowed = schedule.getExpectedEndTime().minusHours(1); // За час до конца
                LocalTime latestAllowed = schedule.getExpectedEndTime().plusHours(1); // До часа после конца

                if (currentTime.isBefore(earliestAllowed)) {
                        throw new IllegalStateException(
                                "Too early for punch-out. Allowed from: " + earliestAllowed
                        );
                }

                if (currentTime.isAfter(latestAllowed)) {
                        throw new IllegalStateException(
                                "Too late for punch-out. Last allowed time was: " + latestAllowed
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

        private WorkerAttendance createAttendanceForPunchOut(User user, PunchOutRequest punchOutRequest, String photoUrl) {
                return WorkerAttendance.builder()
                        .worker(user)

                        .checkOutTime(LocalDateTime.now())
                        .checkOutPhotoUrl(photoUrl)
                        .checkOutLatitude(punchOutRequest.getLatitude())
                        .checkOutLongitude(punchOutRequest.getLongitude())
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


        @Transactional
        public WorkerPayroll updatePayrollOnPunchOut(WorkerAttendance workerAttendance) {
                User worker = workerAttendance.getWorker();
                LocalDate now = workerAttendance.getCheckOutTime().toLocalDate();

                log.info("Starting updatePayrollOnPunchOut - worker: {}, date: {}", worker.getId(), now);

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

                updatePayrollCalculations(currentPayroll);

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


                BigDecimal defaultHourlyRate = BigDecimal.valueOf(0.0);  // или другое значение по умолчанию
                BigDecimal defaultOvertimeRate = BigDecimal.valueOf(0.0); // или другое значение по умолчанию

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

        @Transactional
        public void updatePayrollCalculations(WorkerPayroll payroll) {
                log.info("Starting payroll calculations");

                // Получаем все attendance за период
                List<WorkerAttendance> periodAttendances = workerAttendanceRepository
                        .findAllByWorkerIdAndCheckInTimeBetween(
                                payroll.getWorker().getId(),
                                payroll.getPeriodStart().atStartOfDay(),
                                payroll.getPeriodEnd().atTime(LocalTime.MAX));

                log.info("Found {} attendances for period", periodAttendances.size());

                // Добавляем текущий attendance в список, если он еще не включен
                WorkerAttendance currentAttendance = workerAttendanceRepository
                        .findFirstByWorkerAndCheckOutTimeIsNotNullOrderByCheckOutTimeDesc(payroll.getWorker())
                        .orElseThrow(() -> new RuntimeException("Current attendance not found"));

                if (!periodAttendances.contains(currentAttendance)) {
                        periodAttendances.add(currentAttendance);
                        log.info("Added current attendance to calculations");
                }

                double totalRegularHours = 0;
                double totalOvertimeHours = 0;

                for (WorkerAttendance attendance : periodAttendances) {
                        if (attendance.getHoursWorked() != null) {
                                double dailyHours = attendance.getHoursWorked();
                                log.info("Processing attendance ID: {} - hours worked: {}", attendance.getId(), dailyHours);

                                if (dailyHours > 8) {
                                        totalRegularHours += 8;
                                        totalOvertimeHours += dailyHours - 8;
                                } else {
                                        totalRegularHours += dailyHours;
                                }
                        }
                }

                log.info("Calculated total hours - regular: {}, overtime: {}", totalRegularHours, totalOvertimeHours);

                // Устанавливаем часы
                payroll.setRegularHours(totalRegularHours);
                payroll.setOvertimeHours(totalOvertimeHours);
                payroll.setTotalHours(totalRegularHours + totalOvertimeHours);



                // Проверяем ставки
                if (payroll.getBaseHourlyRate() == null || payroll.getBaseHourlyRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setBaseHourlyRate(BigDecimal.valueOf(15.0));
                        log.info("Set default base rate: {}", payroll.getBaseHourlyRate());
                }

                if (payroll.getOvertimeRate() == null || payroll.getOvertimeRate().compareTo(BigDecimal.ZERO) == 0) {
                        payroll.setOvertimeRate(BigDecimal.valueOf(22.5));
                        log.info("Set default overtime rate: {}", payroll.getOvertimeRate());
                }

                // Рассчитываем зарплату
                BigDecimal regularPay = payroll.getBaseHourlyRate().multiply(BigDecimal.valueOf(totalRegularHours));
                BigDecimal overtimePay = payroll.getOvertimeRate().multiply(BigDecimal.valueOf(totalOvertimeHours));

                payroll.setRegularPay(regularPay);
                payroll.setOvertimePay(overtimePay);
                payroll.setGrossPay(regularPay.add(overtimePay));


                log.info("Set payroll values - regular pay: {}, overtime pay: {}, gross pay: {}",
                        regularPay, overtimePay, payroll.getGrossPay());

                calculateDeductions(payroll);
                var x = payroll.getGrossPay().subtract(payroll.getTotalDeductions());
                payroll.setNetPay(payroll.getGrossPay().subtract(payroll.getTotalDeductions()));

                double currentDayHours = currentAttendance.getHoursWorked() != null ? currentAttendance.getHoursWorked() : 0.0;

                double regularHours = Math.min(currentDayHours, 8.0);
                double overtimeHours = currentDayHours > 8.0 ? currentDayHours - 8.0 : 0.0;


                BigDecimal currentDayRegularPay = payroll.getBaseHourlyRate()
                        .multiply(BigDecimal.valueOf(regularHours));
                BigDecimal currentDayOvertimePay = payroll.getOvertimeRate()
                        .multiply(BigDecimal.valueOf(overtimeHours));


                BigDecimal currentDayGrossPay = currentDayRegularPay.add(currentDayOvertimePay);

                log.info("Daily pay calculation - hours: {}, regular hours: {}, overtime: {}, total pay: {}",
                        currentDayHours, regularHours, overtimeHours, currentDayGrossPay);


                BigDecimal dailyDeductions = payroll.getTotalDeductions()
                        .multiply(currentDayGrossPay)
                        .divide(payroll.getGrossPay(), 2, RoundingMode.HALF_UP);
                currentAttendance.setNetPay(currentDayGrossPay.subtract(dailyDeductions));
                currentAttendance.setGrossPayPerDay(currentDayGrossPay);

        }

        private void calculateDeductions(WorkerPayroll payroll) {
                BigDecimal grossPay = payroll.getGrossPay();

                // Пример расчета удержаний (процентные ставки нужно настроить согласно вашим требованиям)
                payroll.setMedicare(grossPay.multiply(BigDecimal.valueOf(0.0145)));
                payroll.setSocialSecurityEmployee(grossPay.multiply(BigDecimal.valueOf(0.062)));
                payroll.setFederalWithholding(grossPay.multiply(BigDecimal.valueOf(0.15)));
                payroll.setNyStateWithholding(grossPay.multiply(BigDecimal.valueOf(0.04)));
                payroll.setNyLocalWithholding(grossPay.multiply(BigDecimal.valueOf(0.03)));
                payroll.setNyDisabilityWithholding(grossPay.multiply(BigDecimal.valueOf(0.005)));
                payroll.setNyPaidFamilyLeave(grossPay.multiply(BigDecimal.valueOf(0.00511)));

                BigDecimal totalDeductions = Arrays.asList(
                        payroll.getMedicare(),
                        payroll.getSocialSecurityEmployee(),
                        payroll.getFederalWithholding(),
                        payroll.getNyStateWithholding(),
                        payroll.getNyLocalWithholding(),
                        payroll.getNyDisabilityWithholding(),
                        payroll.getNyPaidFamilyLeave()
                ).stream().reduce(BigDecimal.ZERO, BigDecimal::add);

                payroll.setTotalDeductions(totalDeductions);
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
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 'a' добавляет AM/PM

                String formattedDate = dateTime.format(dateFormatter);
                String formattedTime = dateTime.format(timeFormatter);

                return new LastPunchTimeDTO(formattedDate, formattedTime);
        }
}
