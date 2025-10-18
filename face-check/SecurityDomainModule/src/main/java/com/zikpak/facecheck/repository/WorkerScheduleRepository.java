package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerScheduleRepository extends JpaRepository<WorkerSchedule, Integer> {

    @Query("""
    SELECT ws FROM WorkerSchedule ws
    LEFT JOIN FETCH ws.workSite
    WHERE ws.worker = :worker 
    AND ws.scheduleDate BETWEEN :startDate AND :endDate
    ORDER BY ws.scheduleDate
    """)
    List<WorkerSchedule> findByWorkerAndScheduleDateBetween(User worker, LocalDate startOfWeek, LocalDate endOfWeek);




    @Query("""
    SELECT ws FROM WorkerSchedule ws
    LEFT JOIN FETCH ws.workSite
    LEFT JOIN FETCH ws.worker w
    WHERE ws.worker = :worker
    AND ws.scheduleDate = :scheduleDate
    """)
    Optional<WorkerSchedule> findByWorkerAndScheduleDate(
            @Param("worker") User worker,
            @Param("scheduleDate") LocalDate scheduleDate
    );




    @Query("""
    SELECT ws FROM WorkerSchedule ws
    LEFT JOIN FETCH ws.workSite
    LEFT JOIN FETCH ws.worker w
    WHERE ws.worker = :worker
    AND ws.isTemplate = true
    """)
    List<WorkerSchedule> findByWorkerAndIsTemplateTrue(@Param("worker") User worker);



    @Query("""
    SELECT ws FROM WorkerSchedule ws
    LEFT JOIN FETCH ws.workSite
    LEFT JOIN FETCH ws.worker w
    WHERE ws.worker = :worker
    AND ws.dayOfWeek = :dayOfWeek
    AND ws.isTemplate = true
    """)
    Optional<WorkerSchedule> findByWorkerAndDayOfWeekAndIsTemplateTrue(
            @Param("worker") User worker,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );



    void deleteByWorkerAndIsTemplateTrue(User worker);

}
