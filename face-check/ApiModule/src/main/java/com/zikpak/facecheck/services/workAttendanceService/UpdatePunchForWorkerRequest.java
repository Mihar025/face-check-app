package com.zikpak.facecheck.services.workAttendanceService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePunchForWorkerRequest {


    @NotNull(message = "This field is required!")
    private LocalDate newPunchDate;

    @NotNull(message = "This field is required!")
    private LocalTime newPunchTime;

    @NotNull(message = "This field is required!")
    private PunchType punchType;

    @NotNull(message = "This field is required!")
    @PositiveOrZero(message = "Worked hours cannot be negative!")
    private Double workedHours;

    @NotNull(message = "This field is required!")
    private Integer workSiteId;

    @NotNull(message = "This field is required!")
    @NotBlank(message = "This field cannot be blank!")
    private String notes;

    @NotNull(message = "This field is required!")
    private Boolean skipOvertimeCalculation;

}