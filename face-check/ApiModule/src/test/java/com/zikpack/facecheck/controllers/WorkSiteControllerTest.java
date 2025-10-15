package com.zikpack.facecheck.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zikpak.facecheck.controllers.WorkSiteController;
import com.zikpak.facecheck.controllers.WorkerAttendanceController;
import com.zikpak.facecheck.requestsResponses.attendance.PunchOutRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.data.SetNewCustomRadiusRequest;
import com.zikpak.facecheck.requestsResponses.workSite.data.SetNewCustomRadiusResponse;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = WorkSiteControllerTest.TestConfig.class   // <-- указали наш TestConfig
)
@AutoConfigureMockMvc(addFilters = false)
public class WorkSiteControllerTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })


    @Import(WorkSiteController.class)
    static class TestConfig {

    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkSiteService workSiteService;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "USER")
    void findWorkSiteById() throws Exception {

        WorkSiteResponse resp = WorkSiteResponse.builder()
                .workSiteId(1)
                .workSiteName("test")
                .address("test2")
                .latitude(1.0)
                .longitude(2.0)
                .allowedRadius(134.0)
                .workDayStart(LocalTime.of(7, 30))
                .workDayEnd(LocalTime.of(8, 30))
                .build();

        given(workSiteService.findWorkSiteById(1)).willReturn(resp);

        mockMvc.perform(get("/workSite/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

    }

    @Test
    @WithMockUser(roles = "USER")
    void selectWorkSite() throws Exception {
        SelectWorkSiteResponse resp = SelectWorkSiteResponse.builder()
                .selectedWorkSiteName("test")
                .selectedWorkSiteId(1)
                .workerId(2)
                .build();
        Authentication auth = mock(Authentication.class);
        given(workSiteService.selectWorkSite(1, auth)).willReturn(resp);

        mockMvc.perform(post("/workSite/select/1")
                    ).andExpect(status().isOk());
              //  .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createWorkSite() throws Exception {
        WorkSiteResponse resp = WorkSiteResponse.builder()
                .workSiteId(1)
                .workSiteName("test")
                .address("test2")
                .latitude(1.0)
                .longitude(2.0)
                .allowedRadius(134.0)
                .workDayStart(LocalTime.of(7, 30))
                .workDayEnd(LocalTime.of(8, 30))
                .build();

        WorkSiteRequest request = WorkSiteRequest.builder()
                .workSiteName("test")
                .address("test2")
                .latitude(1.0)
                .longitude(2.0)
                .allowedRadius(134.0)
                .workDayStart(LocalTime.of(7, 30))
                .workDayEnd(LocalTime.of(8, 30))
                .build();

        given(workSiteService.createWorkSite(any(Authentication.class), eq(request))).willReturn(resp);

        mockMvc.perform(post("/workSite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());
        //  .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<WorkSiteRequest> captor =
                ArgumentCaptor.forClass(WorkSiteRequest.class);
        then(workSiteService).should()
                .createWorkSite(isNull(), captor.capture());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setCustomRadius() throws Exception {
        SetNewCustomRadiusRequest req = new SetNewCustomRadiusRequest();
        req.setCustomRadius(1.0);

        SetNewCustomRadiusResponse resp = SetNewCustomRadiusResponse.builder()
                .workSiteId(1)
                .customRadius(1.0)
                .build();
        Authentication auth = mock(Authentication.class);
        given(workSiteService.setCustomRadiusForWorkSite(auth, 1, req)).willReturn(resp);

        mockMvc.perform(post("/workSite/1/custom-radius")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk());

        ArgumentCaptor<SetNewCustomRadiusRequest> captor =
                ArgumentCaptor.forClass(SetNewCustomRadiusRequest.class);
        then(workSiteService).should()
                .setCustomRadiusForWorkSite(isNull(),eq(1), captor.capture());
    }














}
