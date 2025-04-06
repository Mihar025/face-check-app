package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerScheduleRepository extends JpaRepository<WorkerSchedule, Integer> {
    List<WorkerSchedule> findByWorkerAndScheduleDateBetween(User worker, LocalDate startOfWeek, LocalDate endOfWeek);


    Optional<WorkerSchedule> findByWorkerAndScheduleDate(User worker, LocalDate scheduleDate);
    WorkerSchedule findFirstByWorkerOrderByScheduleDateDesc(User worker);}
