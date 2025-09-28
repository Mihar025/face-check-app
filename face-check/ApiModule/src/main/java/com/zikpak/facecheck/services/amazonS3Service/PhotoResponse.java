package com.zikpak.facecheck.services.amazonS3Service;


import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResponse {

    private Integer workerId;
    private String photoUrl;

}
