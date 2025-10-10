package com.zikpack.facecheck.services.workerAttendanceService;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.metrics.MetricsService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.attendance.PunchInRequest;
import com.zikpak.facecheck.requestsResponses.attendance.PunchInResponse;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import com.zikpak.facecheck.taxesServices.calculators.FinanceCalculator;
import com.zikpak.facecheck.taxesServices.services.sickDayService.SickLeaveService;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class WorkAttendanceServiceTest {

    @InjectMocks
    private WorkAttendanceService workAttendanceService;

    @Mock
    private WorkerAttendanceRepository workerAttendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkSiteService workSiteService;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private WorkerSiteRepository workSiteRepository;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private WorkerScheduleRepository workerScheduleRepository;

    @Mock
    private FinanceCalculator financeCalculator;

    @Mock
    private SickLeaveService sickLeaveService;

    @Mock
    private MetricsService metricsService;

    @Mock
    private Authentication authentication;

    @Mock
    private Timer.Sample timerSample;

    private PunchInRequest punchInRequest;
    private User worker;
    private WorkSite workSite;
    private WorkerSchedule schedule;
    private Company company;

    @BeforeEach
    void setUp() {
        // Настройка компании
        company = new Company();
        company.setId(1);
        company.setCompanyName("Test Company");

        // Настройка работника
        worker = new User();
        worker.setId(1);
        worker.setFirstName("First");
        worker.setLastName("Last");
        worker.setEmail("test@example.com");
        worker.setUser(true);
        worker.setCompany(company);
        //  worker.setWorkSites(new ArrayList<>());

        // Настройка рабочего места
        workSite = new WorkSite();
        workSite.setId(1);
        workSite.setAddress("23455f");
        workSite.setSiteName("site");
        workSite.setLatitude(2.34);
        workSite.setLongitude(3.14);
        workSite.setAllowedRadius(100.0);
        workSite.setIsActive(true);
        // workSite.setUsers(new ArrayList<>());

        // Настройка расписания
        schedule = new WorkerSchedule();
        schedule.setExpectedStartTime(LocalTime.of(9, 0));
        schedule.setExpectedEndTime(LocalTime.of(17, 0));

        // Настройка запроса
        punchInRequest = new PunchInRequest();
        punchInRequest.setWorkSiteId(workSite.getId());
        punchInRequest.setLatitude(2.37);
        punchInRequest.setLongitude(3.14);
        punchInRequest.setPhotoBase64("base64photodata");

        // Настройка моков
        lenient().when(metricsService.startTimer()).thenReturn(timerSample);
    }

    @Test
    void makePunchIn_success() {
        // Arrange - настройка моков
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));

        // Мок для проверки существующего punch-in
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());

        // Мок для workSite
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        // Мок для валидации местоположения
        when(workSiteService.isWithinRadiusForPunchInOut(
                workSite.getId(),
                punchInRequest.getLatitude(),
                punchInRequest.getLongitude()))
                .thenReturn(true);

        // Мок для расписания
        when(workerScheduleRepository.findByWorkerAndScheduleDate(worker, LocalDate.now()))
                .thenReturn(Optional.of(schedule));

        // Мок для загрузки фото
        when(amazonS3Service.uploadAttendancePhoto(
                punchInRequest.getPhotoBase64(),
                worker.getEmail(),
                "punch-in"))
                .thenReturn("https://s3.amazonaws.com/photo.jpg");

        // Мок для сохранения attendance
        WorkerAttendance savedAttendance = WorkerAttendance.builder()
                .id(1)
                .worker(worker)
                .checkInTime(LocalDateTime.now())
                .checkInPhotoUrl("https://s3.amazonaws.com/photo.jpg")
                .checkInLatitude(punchInRequest.getLatitude())
                .checkInLongitude(punchInRequest.getLongitude())
                .checkInLocation(workSite.getAddress())
                .build();

        when(workerAttendanceRepository.save(any(WorkerAttendance.class)))
                .thenReturn(savedAttendance);

        // Act - выполнение тестируемого метода
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert - проверка результата
        assertThat(actual).isNotNull();
        assertThat(actual.getWorkerId()).isEqualTo(worker.getId());
        assertThat(actual.getWorkSiteId()).isEqualTo(workSite.getId());
        assertThat(actual.getWorkerFullName()).isEqualTo("First Last");
        assertThat(actual.getWorkSiteName()).isEqualTo(workSite.getSiteName());
        assertThat(actual.getIsSuccessful()).isTrue();
        assertThat(actual.getMessage()).isEqualTo("Successfully checked in!");

        // Verify - проверка вызовов моков
        verify(userRepository).findByEmail(worker.getEmail());
        verify(workerAttendanceRepository).findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker);
        verify(workSiteRepository, times(2)).findById(workSite.getId()); // Вызывается дважды: в validateAndGetWorkSite() и createAttendance()
        verify(workSiteService).isWithinRadiusForPunchInOut(
                workSite.getId(),
                punchInRequest.getLatitude(),
                punchInRequest.getLongitude());
        verify(workerScheduleRepository).findByWorkerAndScheduleDate(worker, LocalDate.now());
        verify(amazonS3Service).uploadAttendancePhoto(
                punchInRequest.getPhotoBase64(),
                worker.getEmail(),
                "punch-in");
        verify(workerAttendanceRepository).save(any(WorkerAttendance.class));

        // Проверка метрик
        verify(metricsService).startTimer();
        verify(metricsService).recordLocationValidation(
                eq(workSite.getSiteName()),
                eq("First Last"),
                eq(company.getCompanyName()),
                eq(true),
                eq(true));
        verify(metricsService).recordPhotoUploading(eq("punch_in"), eq(true), anyLong());
        verify(metricsService).recordPunchIn(workSite.getSiteName(), true);
        verify(metricsService).recordOperationTime(timerSample, "punch_in");

        // Проверка изменений в объектах
        assertThat(workSite.getIsWorkerDidPunchIn()).isTrue();
        assertThat(worker.getCurrentWorkSite()).isEqualTo(workSite);
        //    assertThat(worker.getWorkSites()).contains(workSite);
        //    assertThat(workSite.getUsers()).contains(worker);
    }

    @Test
    void makePunchIn_failureUserNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Проверяем, что метод возвращает ошибку вместо выброса исключения
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("User not found");

        // Verify error metrics
        verify(metricsService).recordPunchIn("unknown", false);
        verify(metricsService).recordError(eq("punch_in"), anyString(), any(Exception.class));
        verify(metricsService).recordOperationTime(timerSample, "punch_in_failed");
    }

    @Test
    void makePunchIn_failureExistingPunchIn() {
        // Arrange
        WorkerAttendance existingAttendance = new WorkerAttendance();
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.of(existingAttendance));

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert - метод возвращает ошибку, а не выбрасывает исключение
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("You already have an active punch-in");
    }

    @Test
    void makePunchIn_failureWorkSiteNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.empty());

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert - метод возвращает ошибку, а не выбрасывает исключение
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("Work site not found");
    }

    @Test
    void makePunchIn_failureLocationNotInRadius() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteService.isWithinRadiusForPunchInOut(
                workSite.getId(),
                punchInRequest.getLatitude(),
                punchInRequest.getLongitude()))
                .thenReturn(false);

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert - метод возвращает ошибку, а не выбрасывает исключение
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("Error! You are not in allowed radius of the work site!");
    }

    @Test
    void makePunchIn_failureTooEarlyForPunchIn() {
        // Arrange
        schedule.setExpectedStartTime(LocalTime.now().plusHours(2)); // Расписание через 2 часа

        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(workerScheduleRepository.findByWorkerAndScheduleDate(worker, LocalDate.now()))
                .thenReturn(Optional.of(schedule));

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert - метод возвращает ошибку, а не выбрасывает исключение
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("Too early for punch-in");
    }

    @Test
    void makePunchIn_failureWorkSiteNotActive() {
        // Arrange
        workSite.setIsActive(false); // Делаем рабочее место неактивным

        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("Work site is not active");
    }

    @Test
    void makePunchIn_failureNoScheduleFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(worker);
        when(userRepository.findByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        when(workerAttendanceRepository.findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(worker))
                .thenReturn(Optional.empty());
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(workerScheduleRepository.findByWorkerAndScheduleDate(worker, LocalDate.now()))
                .thenReturn(Optional.empty()); // Нет расписания

        // Act
        PunchInResponse actual = workAttendanceService.makePunchIn(authentication, punchInRequest);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getIsSuccessful()).isFalse();
        assertThat(actual.getMessage()).contains("No schedule found for worker on date");
    }
}