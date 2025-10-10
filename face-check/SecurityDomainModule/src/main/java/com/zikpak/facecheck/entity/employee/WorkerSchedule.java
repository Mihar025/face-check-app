package com.zikpak.facecheck.entity.employee;

import com.zikpak.facecheck.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "worker_schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    @ManyToOne
    @JoinColumn(name = "work_site_id")
    private WorkSite workSite;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    @Column(name = "expected_start_time")
    private LocalTime expectedStartTime;

    @Column(name = "expected_end_time")
    private LocalTime expectedEndTime;

    @Column(name = "shift")
    private String shift;

    @Column(name = "is_on_duty")
    private Boolean isOnDuty;

    @Column(name = "start_lunch")
    private LocalDateTime startLunch;

    @Column(name = "end_lunch")
    private LocalDateTime endLunch;

    @Column(name = "is_company_paying_lunch")
    private Boolean isCompanyPayingLunch;
}