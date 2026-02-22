package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_random_attendance_verification")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class RandomAttendanceVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    //
    @Column(name = "random_attendance_verification_url")
    private String randomAttendanceVerificationPhotoUrl;
    //
    @Column(name = "random_attendance_verification_latitude")
    private Double randomAttendanceVerificationLatitude;
    //
    @Column(name = "random_attendance_verification_longitude")
    private Double randomAttendanceVerificationLongitude;
    //
    @Column(name = "random_attendance_verification_location")
    private String randomAttendanceVerificationLocation;

    //If Person missed photo we will display it at front-end like message that its missed
    @Column(name = "is_missed_message")
    private String isMissedMessage;

    @Column(name = "message")
    private String message;


    @Column(name = "is_successful")
    private Boolean isSuccessful;

    // Flag that record current is missed
    @Column(name = "is_missed")
    private Boolean isMissed;

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDate createdAt;

    //
    @Column(name = "random_attendance_verification_time")
    private LocalDateTime randomAttendanceVerificationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;








}
