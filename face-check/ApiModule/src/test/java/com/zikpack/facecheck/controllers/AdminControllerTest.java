package com.zikpack.facecheck.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zikpak.facecheck.controllers.AdminController;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.PunchInUpdateRequest;
import com.zikpak.facecheck.requestsResponses.admin.*;
import com.zikpak.facecheck.requestsResponses.worker.UpdatePunchInForWorkerResponse;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import com.zikpak.facecheck.services.adminService.AdminService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = AdminControllerTest.TestConfig.class   // <-- указали наш TestConfig
)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    /**
     * Мини‑конфигурация, которая регистрирует только контроллер.
     * Spring Boot найдёт её как @SpringBootConfiguration.
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import(AdminController.class)
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ForemanAndAdminService foremanAndAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void changePunchInForWorker_shouldReturnOkAndResponseBody() throws Exception {
        // arrange
        ChangePunchInRequest req = ChangePunchInRequest.builder()
                .workerId(1)
                .dateWhenWorkerDidntMakePunchIn(LocalDateTime.of(2025, 7, 7, 7, 30))
                .newPunchInDate(LocalDate.of(2025, 7, 7))
                .newPunchInTime(LocalTime.of(7, 30))
                .build();

        ChangePunchInForWorkerResponse resp = ChangePunchInForWorkerResponse.builder()
                .workerId(1)
                .dateWhenWorkerDidntMakePunchIn(req.getDateWhenWorkerDidntMakePunchIn())
                .newPunchInDate(req.getNewPunchInDate())
                .newPunchInTime(req.getNewPunchInTime())
                .build();

        given(adminService.ChangingPunchInForWorkerIfDoesntExist(eq(1), any(), any()))
                .willReturn(resp);

        // act & assert
        mockMvc.perform(post("/admin/worker/{workerId}/punch-in", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        // verify call
        ArgumentCaptor<ChangePunchInRequest> captor =
                ArgumentCaptor.forClass(ChangePunchInRequest.class);
        then(adminService).should()
                .ChangingPunchInForWorkerIfDoesntExist(eq(1), captor.capture(), any());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void changePunchOutForWorker_shouldReturnOkAndResponseBody() throws Exception {

        ChangePunchOutRequest req = ChangePunchOutRequest.builder()
                .workerId(1)
                .dateWhenWorkerDidntMakePunchOut(LocalDateTime.of(2025, 7, 7, 7, 30))
                .newPunchOutDate(LocalDate.of(2025, 7, 7))
                .newPunchOutTime(LocalTime.of(7, 30))
                .build();

        ChangePunchOutForWorkerResponse resp = ChangePunchOutForWorkerResponse.builder()
                .workerId(1)
                .dateWhenWorkerDidntMakePunchOut(LocalDateTime.of(2025, 7, 7, 7, 30))
                .newPunchOutDate(LocalDate.of(2025, 7, 7))
                .newPunchOutTime(LocalTime.of(7, 30))
                .build();

        given(adminService.ChangingPunchOutForWorkerIfDoesntExist(eq(1), any(), any())).willReturn(resp);
        mockMvc.perform(post("/admin/worker/{workerId}/punch-out", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<ChangePunchOutRequest> captor = ArgumentCaptor.forClass(ChangePunchOutRequest.class);
        then(adminService).should()
                .ChangingPunchOutForWorkerIfDoesntExist(eq(1), captor.capture(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWorkersInWorksite_shouldReturnOkAndResponseBody() throws Exception {
        // 1) подготовим один воркер
        WorksiteWorkerResponse worker = WorksiteWorkerResponse.builder()
                .workerId(1)
                .firstName("FN")
                .lastName("LN")
                .phoneNumber("pN")
                .workSiteAddress("address")
                .punchIn(LocalDateTime.of(2025,7,7,7,30))
                .build();

        // 2) завернём его в PageResponse (пример: всего 1 элемент, 1 страница)
        PageResponse<WorksiteWorkerResponse> pageResp =
                PageResponse.<WorksiteWorkerResponse>builder()
                        .content(List.of(worker))
                        .number(0)             // вместо page(0)
                        .size(10)              // size осталось без изменений
                        .totalElement(1L)      // вместо totalElements(1)
                        .totalPages(1)
                        .first(true)           // обычно true для первой страницы
                        .last(true)            // и true, раз всего одна страница
                        .build();

        // 3) мокируем foremanAndAdminService (обратите внимание на порядок параметров: page=0, size=10, worksiteId=1)
        given(foremanAndAdminService.findAllWorkersInWorkSite(eq(0), eq(10), eq(1), any()))
                .willReturn(pageResp);

        // 4) выполняем запрос — можно либо строить URL, либо лучше так
        mockMvc.perform(get("/admin/employee")
                        .param("page", "0")
                        .param("size", "10")
                        .param("worksiteId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElement").value(1))   // ← поправлено название
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].workerId").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("FN"))
                .andExpect(jsonPath("$.content[0].lastName").value("LN"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("pN"))
                .andExpect(jsonPath("$.content[0].workSiteAddress").value("address"))
                .andExpect(jsonPath("$.content[0].punchIn").value("2025-07-07T07:30:00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWorkerPunchIn_shouldReturnNoContent() throws Exception {
        // не нужно заранее ничего мокать — метод void

        mockMvc.perform(delete("/admin/workers/{workerId}/punch-in", 42))
                .andExpect(status().isNoContent());

        // убеждаемся, что контроллер вызвал сервис с правильными аргументами
        then(foremanAndAdminService).should()
                .deleteWorkerPunchIn(isNull(), eq(42));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePunchInForWorker_shouldReturnNoContent() throws Exception {

        PunchInUpdateRequest req = PunchInUpdateRequest.builder()
                .newCheckInTIme(LocalDateTime.of(2025, 7, 7, 7, 30, 0))
                .build();

        UpdatePunchInForWorkerResponse resp = UpdatePunchInForWorkerResponse.builder()
                .workerId(1)
                .current_work_site("cws")
                .workerFullName("FN")
                .newPunchInTime(LocalDateTime.of(2025, 7, 7, 7, 30, 0))
                .build();

        given(foremanAndAdminService.updateLatestPunchInForWorkerResponse(isNull(), eq(1), any())).willReturn(resp);

        mockMvc.perform(put("/admin/worker/{workerId}/punch-in", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        // verify call
        ArgumentCaptor<PunchInUpdateRequest> captor =
                ArgumentCaptor.forClass(PunchInUpdateRequest.class);
        then(foremanAndAdminService).should()
                .updateLatestPunchInForWorkerResponse(isNull(),eq(1), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalEmployeesCount() throws Exception {
        given(adminService.findAllEmployeesInCompany(isNull()))
                .willReturn(7);

        mockMvc.perform(get("/admin/company/employees/count"))
                .andExpect(status().isOk())
                // since it’s just an integer body, you can assert the raw string:
                .andExpect(content().string("7"));
        then(adminService).should()
                .findAllEmployeesInCompany(isNull());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalWorksitesCount() throws Exception {
        given(adminService.findAllWorksitesInCompany(isNull()))
                .willReturn(7);

        mockMvc.perform(get("/admin/company/worksites/count"))
                .andExpect(status().isOk())
                // since it’s just an integer body, you can assert the raw string:
                .andExpect(content().string("7"));
        then(adminService).should()
                .findAllWorksitesInCompany(isNull());
    }




}