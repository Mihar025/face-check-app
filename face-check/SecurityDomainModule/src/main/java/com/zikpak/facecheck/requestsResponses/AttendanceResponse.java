package com.zikpak.facecheck.requestsResponses;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponse {

    private Integer workerId;

    private Integer companyId;

    private Integer attendanceId;

    private Double overtimeHours;


    private String companyName;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDateTime checkInTime;

    private String checkInPhotoUrl;

    private Double checkInLatitude;

    private Double checkInLongitude;

    private String checkInLocation;

    private LocalDateTime checkOutTime;

    private String checkOutPhotoUrl;

    private Double checkOutLatitude;

    private Double checkOutLongitude;

    private String checkOutLocation;

    private Double hoursWorked;

    private BigDecimal grossPayPerDay;

    private BigDecimal netPay;

    private LocalDate periodStart;

    private LocalDate periodEnd;








}
