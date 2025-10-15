package com.zikpack.facecheck.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zikpak.facecheck.controllers.WorkerAttendanceController;
import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.worker.DailyFinanceInfo;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = WorkerAttendanceControllerTest.TestConfig.class   // <-- указали наш TestConfig
)
@AutoConfigureMockMvc(addFilters = false)
public class WorkerAttendanceControllerTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import(WorkerAttendanceController.class)
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkAttendanceService workAttendanceService;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void punchIn_thenReturnSuccess() throws Exception {
        PunchInRequest req = PunchInRequest.builder()
                .workSiteId(1)
                .latitude(2.55)
                .longitude(3.54)
                .photoBase64("fgd")
                .build();

        PunchInResponse resp = PunchInResponse.builder()
                .workerId(1)
                .workSiteId(1)
                .workSiteName("name")
                .workerFullName("flName")
                .checkInTime(LocalDateTime.of(2025,4,4,4,40))
                .formattedCheckInTime("gdrgdgr")
                .checkInPhotoUrl("gjgjie")
                .checkInLatitude(2.05)
                .checkInLongitude(3.54)
                .checkInLocation("grgre")
                .workSiteAddress("fsfgr")
                .isSuccessful(true)
                .message("fkfk")
                .build();

        given(workAttendanceService.makePunchIn(isNull(), any())).willReturn(resp);

        mockMvc.perform(post("/attendance/punch-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<PunchInRequest> captor =
                ArgumentCaptor.forClass(PunchInRequest.class);
        then(workAttendanceService).should()
                .makePunchIn(isNull(), captor.capture());

    }


    @Test
    @WithMockUser(roles = "USER")
    void punchOut_thenReturnSuccess() throws Exception {
        PunchOutRequest req = PunchOutRequest.builder()
                .workSiteId(1)
                .latitude(2.55)
                .longitude(3.54)
                .photoBase64("fgd")
                .build();

        PunchOutResponse resp = PunchOutResponse.builder()
                .workerId(1)
                .workSiteId(1)
                .workSiteName("name")
                .workerFullName("flName")
                .checkInTime(LocalDateTime.of(2025,4,4,3,40))
                .checkOutTime(LocalDateTime.of(2025,4,4,4,40))
                .formattedCheckInTime("gdrgdgr")
                .checkOutPhotoUrl("gjgjie")
                .checkOutLatitude(2.05)
                .checkOutLongitude(3.54)
                .checkOutLocation("grgre")
                .hoursWorked(1.0)
                .overtimeHours(0.0)

                .workSiteAddress("fsfgr")
                .isSuccessful(true)
                .message("fkfk")
                .build();

        given(workAttendanceService.makePunchOut(isNull(), any())).willReturn(resp);

        mockMvc.perform(post("/attendance/punch-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<PunchOutRequest> captor =
                ArgumentCaptor.forClass(PunchOutRequest.class);
        then(workAttendanceService).should()
                .makePunchOut(isNull(), captor.capture());

    }


    @Test
    @WithMockUser(roles = "USER")
    void getLastPunchTime_thenReturnSuccess() throws Exception {
        LastPunchTimeDTO resp = LastPunchTimeDTO.builder()
                .formattedDate("date")
                .formattedTime("time")
                .build();

        given(workAttendanceService.getLastPunchTime(isNull())).willReturn(resp);

        mockMvc.perform(get("/attendance/last-punch")
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getWeeklyEarnings_thenReturnSuccess() throws Exception {
        List<DailyEarningResponse> resp = new ArrayList<>();
        resp.add(DailyEarningResponse.builder()
                        .date(LocalDate.of(2025,4,4))
                        .netPay(200.0)
                .build());

        given(workAttendanceService.getCurrentWeekEarnings(isNull())).willReturn(resp);

        mockMvc.perform(get("/attendance/week")
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFinanceInfoForWeek_thenReturnSuccess() throws Exception {

        FinanceInfoForWeekInFinanceScreenResponse resp = FinanceInfoForWeekInFinanceScreenResponse.builder()
                .totalHoursWorked(20.0)
                .totalGrossPay(new BigDecimal("200.0"))
                .totalNetPay(new BigDecimal("200.0"))
                .periodStart(LocalDate.of(2025,4,4))
                .periodEnd(LocalDate.of(2025,4,4))
                .dailyInfo(
                        List.of(
                                DailyFinanceInfo.builder()
                        .date(LocalDate.of(2025,4,4))
                        .hoursWorked(20.0)
                        .grossPay(new BigDecimal("200.0"))
                        .build())
                )
                .build();

        given(workAttendanceService.getFinanceInfoForFinanceScreen(isNull(), any())).willReturn(resp);

        mockMvc.perform(get("/attendance/finance-info?weekStart=2025-04-04")
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }



}
