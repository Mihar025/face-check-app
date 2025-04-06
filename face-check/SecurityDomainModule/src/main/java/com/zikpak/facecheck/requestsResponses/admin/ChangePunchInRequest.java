package com.zikpak.facecheck.requestsResponses.admin;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChangePunchInRequest {

    private Integer workerId;

    private LocalDateTime dateWhenWorkerDidntMakePunchIn;

    private LocalDate newPunchInDate;

    private LocalTime newPunchInTime;




}
