package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ScheduleInactiveDayRequest {

    @NotNull(message = "Inactive date cannot be null!")
    @Future(message = "Inactive day should be in future! Cannot make it in past!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate inactiveDate;

}
