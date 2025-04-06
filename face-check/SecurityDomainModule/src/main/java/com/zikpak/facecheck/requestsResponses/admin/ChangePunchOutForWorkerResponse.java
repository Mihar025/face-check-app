package com.zikpak.facecheck.requestsResponses.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class ChangePunchOutForWorkerResponse {

    private Integer workerId;

    private LocalDateTime dateWhenWorkerDidntMakePunchOut;

    private LocalDate newPunchOutDate;

    private LocalTime newPunchOutTime;
}
