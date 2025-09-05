package com.zikpack.facecheck.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zikpak.facecheck.controllers.AdminController;
import com.zikpak.facecheck.controllers.CompanyController;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingRequest;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.*;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import com.zikpak.facecheck.services.adminService.AdminService;
import com.zikpak.facecheck.services.company.CompanyService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(
        classes = CompanyControllerTest.TestConfig.class   // <-- указали наш TestConfig
)
@AutoConfigureMockMvc(addFilters = false)
public class CompanyControllerTest {


    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import(CompanyController.class)
    static class TestConfig {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCompany_thenReturnSuccess() throws Exception {
        CompanyUpdatingRequest req = CompanyUpdatingRequest.builder()
                .companyName("d")
                .companyAddress("a")
                .companyPhone("3")
                .companyEmail("3@gmail.com")
                .workersQuantity(3)
                .build();

        CompanyUpdatingResponse response = CompanyUpdatingResponse.builder()
                .companyId(1)
                .companyName("d")
                .companyAddress("a")
                .companyPhone("3")
                .companyEmail("3@gmail.com")
                .workersQuantity(3)
                .build();
        given(companyService.updateCompany(any(), eq(1), isNull())).willReturn(response);
        mockMvc.perform(put("/company/{companyId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        ArgumentCaptor<CompanyUpdatingRequest> captor =
                ArgumentCaptor.forClass(CompanyUpdatingRequest.class);
        then(companyService).should()
                .updateCompany(captor.capture(), eq(1), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setCompanyIncomePerMonth_thenReturnSuccess() throws Exception {
        CompanyIncomePerMonthRequest req = CompanyIncomePerMonthRequest.builder()
                .companyIncomePerMonth(new BigDecimal("14.05"))
                .build();

        CompanyIncomePerMonthResponse resp = CompanyIncomePerMonthResponse.builder()
                .companyId(1)
                .companyIncomePerMonth(new BigDecimal("14.05"))
                .build();

        given(companyService.setCompanyIncomePerMonth(any(), eq(1), isNull())).willReturn(resp);

        mockMvc.perform(put("/company/{companyId}/income", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<CompanyIncomePerMonthRequest> captor =
                ArgumentCaptor.forClass(CompanyIncomePerMonthRequest.class);
        then(companyService).should()
                .setCompanyIncomePerMonth(captor.capture(), eq(1), isNull());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findCompanyIncomePerMonth_thenReturnSuccess() throws Exception {

        CompanyIncomePerMonthResponse resp = CompanyIncomePerMonthResponse.builder()
                .companyId(1)
                .companyIncomePerMonth(new BigDecimal("14.05"))
                .build();

        given(companyService.findCompanyIncomePerMonth(eq(1), isNull())).willReturn(resp);

        mockMvc.perform(get("/company/{companyId}/income", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void calculateTaxes_thenReturnSuccess() throws Exception {
        CompanyTaxCalculationRequest req = CompanyTaxCalculationRequest.builder()
                .year(2025)
                .month(3)
                .build();

        CompanyTaxCalculationResponse resp = CompanyTaxCalculationResponse.builder()
                .companyId(1)
                .companyName("d")
                .monthlyIncome(new BigDecimal("14.05"))
                .totalTaxes(new BigDecimal("14.06"))
                .socialSecurityTax(new BigDecimal("14.07"))
                .medicareTax(new BigDecimal("14.08"))
                .federalUnemploymentTax(new BigDecimal("14.09"))
                .nyUnemploymentTax(new BigDecimal("14.10"))
                .nyDisabilityInsurance(new BigDecimal("14.11"))
                .workersCompensation(new BigDecimal("14.12"))
                .employeeCount(5)
                .totalPayroll(new BigDecimal("14.13"))
                .calculationDate(LocalDate.of(2025, 3, 3))
                .build();

        given(companyService.countAllTaxesByCompanyIncome(any(), isNull())).willReturn(resp);

        mockMvc.perform(post("/company/calculate-taxes", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<CompanyTaxCalculationRequest> captor =
                ArgumentCaptor.forClass(CompanyTaxCalculationRequest.class);
        then(companyService).should()
                .countAllTaxesByCompanyIncome(captor.capture(), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalSalaries() throws Exception {
        given(companyService.countSalariesInTotalForAllEmployeePerMonth(isNull())).willReturn(new BigDecimal("14.05"));

        mockMvc.perform(get("/company/total-salaries", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new BigDecimal("14.05"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployeeRates_thenReturnSuccess() throws Exception {
        List<EmployeeSalaryResponse> resp = new ArrayList<>();
        resp.add(EmployeeSalaryResponse.builder()
                        .employeeId(1)
                        .firstName("f")
                        .lastName("l")
                        .email("s")
                        .baseHourlyRate(new BigDecimal("14.05"))
                .build());

        given(companyService.findAllEmployeesBaseHourRate(isNull())).willReturn(resp);

        mockMvc.perform(get("/company/employee-rates", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getEmployeeRate_thenReturnSuccess() throws Exception {
        EmployeeSalaryResponse resp = EmployeeSalaryResponse.builder()
                .employeeId(1)
                .firstName("f")
                .lastName("l")
                .email("s")
                .baseHourlyRate(new BigDecimal("14.05"))
                .build();

        given(companyService.findTheLatestEmployeeBaseHourRate(eq(1), eq(1), isNull())).willReturn(resp);

        mockMvc.perform(get("/company/{companyId}/employees/{employeeId}/rate", 1,1)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployeeRate_thenReturnSuccess() throws Exception {
        EmployeeRaiseHourRateRequest req = EmployeeRaiseHourRateRequest.builder()
                .baseHourlyRate(new BigDecimal("14.05"))
                .build();

        EmployeeSalaryResponse resp = EmployeeSalaryResponse.builder()
                .baseHourlyRate(new BigDecimal("14.05"))
                .build();

        given(companyService.changeEmployeeBaseHourRate(eq(1), eq(1), isNull(), any())).willReturn(resp);

        mockMvc.perform(put("/company/{companyId}/employees/{employeeId}/rate", 1,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        ArgumentCaptor<EmployeeRaiseHourRateRequest> captor =
                ArgumentCaptor.forClass(EmployeeRaiseHourRateRequest.class);
        then(companyService).should()
                .changeEmployeeBaseHourRate( eq(1), eq(1),isNull(),captor.capture());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToForeman_thenReturnSuccess() throws Exception {

        mockMvc.perform(patch("/company/employees/{employeeId}/promote/foreman", 1))
                .andExpect(status().isOk());
        then(companyService).should()
                .raiseToForemanRoleInCompany(eq(1), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_thenReturnSuccess() throws Exception {

        mockMvc.perform(patch("/company/employees/{employeeId}/promote/admin", 1))
                .andExpect(status().isOk());
        then(companyService).should()
                .raiseToAdminRoleInCompany(eq(1), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fireEmployee_thenReturnSuccess() throws Exception {

        mockMvc.perform(delete("/company/employees/{employeeId}/fire", 1))
                .andExpect(status().isOk());
        then(companyService).should()
                .fireEmployee(eq(1), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany_thenReturnSuccess() throws Exception {

        mockMvc.perform(delete("/company/{companyId}", 1))
                .andExpect(status().isOk());
        then(companyService).should()
                .deleteCompany(eq(1), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void demoteFromForemanToUser_thenReturnSuccess() throws Exception {

        mockMvc.perform(put("/company/demote/{workerId}/foreman-to-user", 1))
                .andExpect(status().isOk());
        then(companyService).should()
                .demoteFromForemanToUser(eq(1), isNull());
    }









}
