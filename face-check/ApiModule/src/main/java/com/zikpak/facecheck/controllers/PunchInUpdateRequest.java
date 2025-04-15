package com.zikpak.facecheck.controllers;


import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PunchInUpdateRequest {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]", shape = JsonFormat.Shape.STRING)
    private LocalDateTime newCheckInTIme;

}
