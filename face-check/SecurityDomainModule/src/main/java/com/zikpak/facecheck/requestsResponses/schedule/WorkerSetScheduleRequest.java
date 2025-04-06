package com.zikpak.facecheck.requestsResponses.schedule;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSetScheduleRequest {

    @NotNull(message = "Start time cannot be null!")
    private LocalTime startTime;
    @NotNull(message = "End Time cannot be null")
    private LocalTime endTime;

    public void validate(){
        LocalTime minimumStartTime = LocalTime.of(6,30);

        if(startTime.isBefore(minimumStartTime)){
            throw new IllegalArgumentException("Start time cannot be before end time!");
        }
        if(startTime.isAfter(endTime)){
            throw new IllegalArgumentException("Start time cannot be after end time!");
        }

        if(endTime.isBefore(startTime)){
            throw new IllegalArgumentException("End time cannot be before start time!");
        }

    }


}
