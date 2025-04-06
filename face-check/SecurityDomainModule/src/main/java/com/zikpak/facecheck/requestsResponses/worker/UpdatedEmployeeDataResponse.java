package com.zikpak.facecheck.requestsResponses.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatedEmployeeDataResponse {

    private Integer workerId;

    private String firstName;

    private String lastName;

    private String email;

}
