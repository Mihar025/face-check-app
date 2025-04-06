package com.zikpak.facecheck.requestsResponses.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@Builder
public class ChangePunchInForWorkerResponse {

    private Integer workerId;

    private LocalDateTime dateWhenWorkerDidntMakePunchIn;

    private LocalDate newPunchInDate;

    private LocalTime newPunchInTime;


}
