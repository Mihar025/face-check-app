package com.zikpak.facecheck.requestsResponses.attendance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PunchOutRequest {

    @Positive(message = "This field cannot be negative! And must be positive!")
    private Integer workSiteId;

    @NotBlank(message = "This field cannot be blank!")
    private String photoBase64;

    @NotNull(message = "This field is required!")
  //  @PositiveOrZero(message = "This field cannot be negative")
    private Double latitude;

    @NotNull(message = "This field is required!")
 //   @PositiveOrZero(message = "This field cannot be negative")
    private Double longitude;

    private String notesForPunchOut;


}
