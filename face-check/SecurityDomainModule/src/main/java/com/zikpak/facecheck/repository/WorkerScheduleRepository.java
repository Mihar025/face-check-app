package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerScheduleRepository extends JpaRepository<WorkerSchedule, Integer> {
    List<WorkerSchedule> findByWorkerAndScheduleDateBetween(User worker, LocalDate startOfWeek, LocalDate endOfWeek);


    Optional<WorkerSchedule> findByWorkerAndScheduleDate(User worker, LocalDate scheduleDate);
    WorkerSchedule findFirstByWorkerOrderByScheduleDateDesc(User worker);

    boolean existsByWorkerAndScheduleDate(User worker, LocalDate currentDate);

    List<WorkerSchedule> findByWorkerAndIsTemplateTrue(User worker);

    Optional<WorkerSchedule> findByWorkerAndDayOfWeekAndIsTemplateTrue(
            User worker,
            DayOfWeek dayOfWeek
    );

    void deleteByWorkerAndIsTemplateTrue(User worker);

    @Query("SELECT ws FROM WorkerSchedule ws WHERE ws.worker = :worker " +
            "AND ws.isTemplate = true ORDER BY ws.dayOfWeek")
    List<WorkerSchedule> findTemplateScheduleByWorker(User worker);
}
