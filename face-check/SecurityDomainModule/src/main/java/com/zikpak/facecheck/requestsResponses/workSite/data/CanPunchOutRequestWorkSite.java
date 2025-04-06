package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
public class CanPunchOutRequestWorkSite {

    @NotNull(message = "PunchOut cannot be null!")
    @Future(message = "PunchOut should be in future! Cannot make it in past!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
   private LocalTime canPunchOut;

}
