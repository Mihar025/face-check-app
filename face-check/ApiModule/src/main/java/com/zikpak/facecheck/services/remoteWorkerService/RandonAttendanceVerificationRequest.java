package com.zikpak.facecheck.services.remoteWorkerService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RandonAttendanceVerificationRequest {



    @Positive(message = "This field cannot be negative! And must be positive!")
    private Integer workSiteId;

    @NotNull(message = "Verification ID is required!")
    private Integer verificationId;


    @NotBlank(message = "This field cannot be blank!")
    private String photoBase64;

    @NotNull(message = "This field is required!")
    // @PositiveOrZero(message = "This field cannot be negative")
    private Double latitude;

    @NotNull(message = "This field is required!")
    // @PositiveOrZero(message = "This field cannot be negative")
    private Double longitude;
}
