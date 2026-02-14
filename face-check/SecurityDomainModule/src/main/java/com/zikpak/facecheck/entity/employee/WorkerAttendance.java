package com.zikpak.facecheck.entity.employee;

import com.zikpak.facecheck.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;


    @Column(name = "check_in_photo_url")
    private String checkInPhotoUrl;

    @Column(name = "check_in_latitude")
    private Double checkInLatitude;

    @Column(name = "check_in_longitude")
    private Double checkInLongitude;

    @Column(name = "check_in_location")
    private String checkInLocation;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_out_photo_url")
    private String checkOutPhotoUrl;

    @Column(name = "check_out_latitude")
    private Double checkOutLatitude;

    @Column(name = "check_out_longitude")
    private Double checkOutLongitude;

    @Column(name = "check_out_location")
    private String checkOutLocation;


    @Column(name = "hours_worked")
    private Double hoursWorked;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "gross_pay_per_day")
    private BigDecimal grossPayPerDay;

    @Column(name = "net_pay")
    private BigDecimal netPay;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_time")
    private LocalDateTime verificationTime;

    @Column(name = "notes_for_punch_in", length = 3000)
    private String notesForPunchIn;

    @Column(name = "notes_for_punch_out", length = 3000)
    private String notesForPunchOut;







}
