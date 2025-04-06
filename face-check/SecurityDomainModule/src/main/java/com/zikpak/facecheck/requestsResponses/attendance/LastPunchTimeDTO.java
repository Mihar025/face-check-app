package com.zikpak.facecheck.requestsResponses.attendance;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LastPunchTimeDTO {
    private String formattedDate;
    private String formattedTime;
}