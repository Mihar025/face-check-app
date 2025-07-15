package com.zikpack.facecheck.services.adminService;

import com.zikpak.facecheck.entity.Role;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.ChangePunchInForWorkerResponse;
import com.zikpak.facecheck.requestsResponses.admin.ChangePunchInRequest;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import com.zikpak.facecheck.services.adminService.AdminService;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  WorkerAttendanceRepository workerAttendanceRepository;
    @Mock
    private  ForemanAndAdminService foremanAndAdminService;
    @Mock
    private  CompanyRepository companyRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Authentication auth;
    private User admin;
    private User worker;


    @BeforeEach
    void setUp() {
        // Мокаем Authentication
        User admin = new User();
        admin.setId(101);

        // Создаём настоящий Role и ставим имя
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        admin.setRoles(Collections.singletonList(adminRole));

        when(auth.getPrincipal()).thenReturn(admin);

        // Пустой "worker"
        worker = new User();
        worker.setId(100);
        worker.setAttendances(Collections.emptyList());

        // Мокаем userRepository, когда нужно
        when(userRepository.findById(100)).thenReturn(Optional.of(worker));
    }



    @Test
    void findAllWorkersInWorkSite_delegatesToForemanService() {
        int page = 1, size = 20;
        Integer workSiteId = 42;
        Authentication auth = mock(Authentication.class);

        @SuppressWarnings("unchecked")
        PageResponse<WorksiteWorkerResponse> expected = mock(PageResponse.class);
        when(foremanAndAdminService.findAllWorkersInWorkSite(page, size, workSiteId, auth))
                .thenReturn(expected);

        PageResponse<WorksiteWorkerResponse> actual =
                adminService.findAllWorkersInWorkSite(page, size, workSiteId, auth);

        assertSame(expected, actual);

        verify(foremanAndAdminService)
                .findAllWorkersInWorkSite(page, size, workSiteId, auth);

        verifyNoMoreInteractions(foremanAndAdminService);
    }

    @Test
    void changingPunchIn_successful() {
        // --- Arrange ---
        ChangePunchInRequest req = new ChangePunchInRequest();
        LocalDateTime missed = LocalDateTime.of(2025,1,1,8,0);
        req.setDateWhenWorkerDidntMakePunchIn(missed);
        req.setNewPunchInDate(missed.toLocalDate());
        req.setNewPunchInTime(LocalTime.of(9,15));

        when(userRepository.findById(100)).thenReturn(Optional.of(worker));

        // --- Act ---
        ChangePunchInForWorkerResponse resp =
                adminService.ChangingPunchInForWorkerIfDoesntExist(100, req, auth);

        // --- Assert ---
        assertThat(resp.getWorkerId()).isEqualTo(100);
        assertThat(resp.getNewPunchInDate()).isEqualTo(req.getNewPunchInDate());
        assertThat(resp.getNewPunchInTime()).isEqualTo(req.getNewPunchInTime());

        ArgumentCaptor<WorkerAttendance> captor =
                ArgumentCaptor.forClass(WorkerAttendance.class);
        verify(workerAttendanceRepository).save(captor.capture());
        assertThat(captor.getValue().getCheckInTime())
                .isEqualTo(LocalDateTime.of(2025,1,1,9,15));

        verifyNoMoreInteractions(userRepository, workerAttendanceRepository);
    }



}
