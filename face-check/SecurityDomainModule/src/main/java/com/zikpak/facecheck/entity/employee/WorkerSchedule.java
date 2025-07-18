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

    private LocalDate scheduleDate;
    private LocalTime expectedStartTime;
    private LocalTime expectedEndTime;
    private String shift;
    private Boolean isOnDuty;
    private LocalDateTime startLunch;
    private LocalDateTime endLunch;
    private Boolean isCompanyPayingLunch;
}