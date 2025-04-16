package com.zikpak.facecheck.entity.employee;

import com.zikpak.facecheck.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private User worker;

    @ManyToOne
    private WorkSite workSite;

    private LocalDate scheduleDate;
    private LocalTime expectedStartTime;
    private LocalTime expectedEndTime;
    private String shift;
    private Boolean isOnDuty;
}