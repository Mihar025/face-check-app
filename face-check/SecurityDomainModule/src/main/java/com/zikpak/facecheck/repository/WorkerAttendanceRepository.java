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

    Optional<WorkerAttendance> findFirstByWorkerOrderByCheckInTimeDesc(User worker);
    Optional<WorkerAttendance> findFirstByWorkerOrderByCheckOutTimeDesc(User worker);

    Optional<WorkerAttendance> findFirstByWorkerAndCheckOutTimeIsNotNullOrderByCheckOutTimeDesc(User worker);

    @Query("SELECT wa FROM WorkerAttendance wa WHERE wa.worker.id = :workerId " +
            "ORDER BY wa.checkInTime DESC LIMIT 1")
    Optional<WorkerAttendance> findLatestAttendanceByWorkerId(@Param("workerId") Integer workerId);

}


