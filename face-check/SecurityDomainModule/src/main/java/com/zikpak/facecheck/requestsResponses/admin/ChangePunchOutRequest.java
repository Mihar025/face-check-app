package com.zikpak.facecheck.requestsResponses.admin;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePunchOutRequest {

    @NotNull(message = "This field is required!")
    private Integer workerId;

    @NotNull(message = "This field is required!")
    private LocalDateTime dateWhenWorkerDidntMakePunchOut;

    @NotNull(message = "This field is required!")
    private LocalDate newPunchOutDate;

    @NotNull(message = "This field is required!")
    private LocalTime newPunchOutTime;
}
