package com.zikpak.facecheck.requestsResponses.company.finance;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@Builder
public class EmployeeSalaryResponse {


    private Integer employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private BigDecimal baseHourlyRate;

    public EmployeeSalaryResponse(Integer employeeId, String firstName, String lastName, String email, BigDecimal baseHourlyRate) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseHourlyRate = baseHourlyRate;
    }
}
