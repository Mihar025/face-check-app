package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TermsOfUseAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String event;

    private Integer userId;

    @CreatedDate
    private LocalDate timeStamp;

    private String termsVersion;

    private String privacyVersion;

    private String ip;

    private String device;

    private String osVersion;




}
