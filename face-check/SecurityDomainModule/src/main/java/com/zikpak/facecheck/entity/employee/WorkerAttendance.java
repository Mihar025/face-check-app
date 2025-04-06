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
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private User worker;

    private LocalDateTime checkInTime;
    private String checkInPhotoUrl;
    private Double checkInLatitude;
    private Double checkInLongitude;
    private String checkInLocation;

    // Информация об уходе
    private LocalDateTime checkOutTime;
    private String checkOutPhotoUrl;
    private Double checkOutLatitude;
    private Double checkOutLongitude;
    private String checkOutLocation;



    private Double hoursWorked;
    private Double overtimeHours;
    private BigDecimal grossPayPerDay;
    private BigDecimal netPay;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private String notes;


    private boolean isVerified;
    private String verifiedBy;
    private LocalDateTime verificationTime;




}
