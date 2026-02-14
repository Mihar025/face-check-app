package com.zikpak.facecheck.services.notesFromPunchService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesForPunchResponse {

    private Integer attendanceId;

    private Integer workerId;
    private String workerFirstName;
    private String workerLastName;
    private String workerFullName;
    private String workerProfileImageUrl;

    private String companyName;

    private String notesForPunchIn;
    private String notesForPunchOut;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private String checkInLocation;
    private String checkOutLocation;

}