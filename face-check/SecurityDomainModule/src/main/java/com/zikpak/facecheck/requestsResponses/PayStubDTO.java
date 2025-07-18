package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Builder
public class PayStubDTO {
    // —————— Существующие поля ——————
    private Integer workerId;
    private String employeeName;
    private String employeeSsn;
    private String employeeAddress;
    private String employeeCity;
    private String employeeState;
    private String employeeZipCode;
    private String employeePhoneNumber;

    private Integer companyId;
    private String companyName;
    private String employerAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyPhoneNumber;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    private BigDecimal totalGrossPay;
    private BigDecimal federalTax;
    private BigDecimal socialSecurityTax;
    private BigDecimal medicareTax;
    private BigDecimal stateTax;
    private BigDecimal localTax;
    private BigDecimal netPay;

    private Map<LocalDate, BigDecimal> hoursWorkedPerDate;
    private Map<LocalDate, BigDecimal> grossPayPerDate;
    private Map<LocalDate, DayOfWeek> dateToDayOfWeek;

    private BigDecimal baseHourlyRate;
    private double totalHours;

    private BigDecimal yearToDate;     // YTD Gross
    private BigDecimal yearToDateNet;  // YTD Net

    // —————— Новые поля для Insurance ——————
    /** true, если сотрудник участвует в страховом плане */
    private Boolean userActivatedInsurance;

    private BigDecimal healthInsuranceMonthly;


    /** Сколько удержано из gross за текущий pay period (Week/Period) */
    private BigDecimal healthInsuranceChargePeriod;

    private BigDecimal healthInsuranceWeeklyCharge;


    // —————— Новые поля для Sick Leave ——————
    /** Сколько часов начислено у сотрудника (accrued) */
    private BigDecimal sickLeaveAccrued;

    /** Сколько часов уже было использовано (used) */
    private BigDecimal sickLeaveUsed;

    /** Сколько часов остаётся (remaining = accrued − used) */
    private BigDecimal sickLeaveRemaining;

    // —————— Даты по дням для календаря ——————
    private Map<DayOfWeek, LocalDate> date;

    private Integer year;
}
