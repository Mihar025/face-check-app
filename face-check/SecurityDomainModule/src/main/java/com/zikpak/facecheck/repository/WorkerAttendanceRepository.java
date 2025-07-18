package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerAttendanceRepository extends JpaRepository<WorkerAttendance, Integer> {

    Optional<WorkerAttendance> findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(User worker);

    List<WorkerAttendance> findAllByWorkerIdAndCheckInTimeBetween(Integer workerId, LocalDateTime localDateTime, LocalDateTime localDateTime1);


    List<WorkerAttendance> findAllByWorkerIdInAndCheckInTimeBetween(
            List<Integer> workerIds,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);



    @Query("SELECT wa FROM WorkerAttendance wa " +
            "WHERE wa.worker = :worker " +
            "AND wa.checkInTime >= :startOfDay " +
            "AND wa.checkInTime <= :endOfDay " +
            "AND wa.checkOutTime IS NULL")
    Optional<WorkerAttendance> findTodayActivePunchIn(
            @Param("worker") User worker,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    Optional<WorkerAttendance> findFirstByWorkerAndCheckOutTimeIsNotNullOrderByCheckOutTimeDesc(User worker);

    @Query("SELECT wa FROM WorkerAttendance wa WHERE wa.worker.id = :workerId " +
            "ORDER BY wa.checkInTime DESC LIMIT 1")
    Optional<WorkerAttendance> findLatestAttendanceByWorkerId(@Param("workerId") Integer workerId);


    Optional<WorkerAttendance> findTopByWorkerOrderByCheckInTimeDesc(User worker);


    @Query("""
    SELECT wa 
    FROM WorkerAttendance wa 
    JOIN FETCH wa.worker w 
    JOIN FETCH w.company 
    WHERE wa.checkInTime >= :startDateTime 
    AND wa.checkInTime <= :endDateTime
""")
    List<WorkerAttendance> findAllByCheckInTimeBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
    SELECT wa 
    FROM WorkerAttendance wa 
    JOIN FETCH wa.worker w 
    WHERE w.company.id = :companyId 
    AND wa.checkInTime >= :startDateTime 
    AND wa.checkInTime <= :endDateTime
""")
    List<WorkerAttendance> findAllByCompanyIdAndCheckInTimeBetween(
            @Param("companyId") Integer companyId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );}


