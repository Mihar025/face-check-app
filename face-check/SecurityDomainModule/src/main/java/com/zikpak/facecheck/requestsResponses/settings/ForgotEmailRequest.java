package com.zikpak.facecheck.requestsResponses.settings;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotEmailRequest {

    private String newEmail;


}
