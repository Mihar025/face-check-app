package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Optional;

@Repository
public interface WorkerAttendanceRepository extends JpaRepository<WorkerAttendance, Integer> {

    @Query("""
       SELECT wa FROM WorkerAttendance wa
        LEFT JOIN FETCH wa.worker w
        LEFT JOIN FETCH w.company c
        WHERE wa.worker = :worker
        AND wa.checkOutTime IS NULL
        ORDER BY wa.checkInTime DESC
        LIMIT 1
""")
    Optional<WorkerAttendance> findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(User worker);



    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH w.company c
    WHERE wa.worker.id = :workerId
    AND wa.checkInTime BETWEEN :startTime AND :endTime
    ORDER BY wa.checkInTime
    """)
    List<WorkerAttendance> findAllByWorkerIdAndCheckInTimeBetween(
            @Param("workerId") Integer workerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH w.company c
    WHERE wa.worker.id IN :workerIds
    AND wa.checkInTime BETWEEN :startDateTime AND :endDateTime
    ORDER BY wa.checkInTime
    """)
    List<WorkerAttendance> findAllByWorkerIdInAndCheckInTimeBetween(
            @Param("workerIds") List<Integer> workerIds,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );


    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH w.company c
    LEFT JOIN FETCH w.wcRiskClass wcr
    WHERE wa.worker = :worker
    AND wa.checkInTime >= :startOfDay
    AND wa.checkInTime <= :endOfDay
    AND wa.checkOutTime IS NULL
    ORDER BY wa.checkInTime DESC
    """)
    Optional<WorkerAttendance> findTodayActivePunchIn(
            @Param("worker") User worker,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );


    @Query("""
SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END 
FROM WorkerAttendance w 
WHERE w.worker = :worker
AND w.checkInTime >= :startOfDay 
AND w.checkInTime < :endOfDay
""")
    boolean hasPunchInToday(
            @Param("worker") User user,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    WHERE wa.worker = :worker
    AND wa.checkOutTime IS NOT NULL
    ORDER BY wa.checkOutTime DESC
    LIMIT 1
    """)
    Optional<WorkerAttendance> findFirstByWorkerAndCheckOutTimeIsNotNullOrderByCheckOutTimeDesc(@Param("worker") User worker);





    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH w.company c
    WHERE wa.worker.id = :workerId
    ORDER BY wa.checkInTime DESC
    LIMIT 1
    """)
    Optional<WorkerAttendance> findLatestAttendanceByWorkerId(@Param("workerId") Integer workerId);


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
    );


    @Query("""
    SELECT wa.checkInPhotoUrl, wa.checkOutPhotoUrl 
    FROM WorkerAttendance wa
    WHERE wa.worker.id = :workerId
    AND (wa.checkInPhotoUrl IS NOT NULL OR wa.checkOutPhotoUrl IS NOT NULL)
    
 """)
    List<Object[]> findAllPhotosUrlRelatedToUser(@Param("workerId") Integer workerId);



    @Query("""
    SELECT wa FROM WorkerAttendance  wa 
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH wa.worker.company c
    WHERE c.id = :companyId
    
""")
    List<WorkerAttendance> findAllAttendanceByCompanyId(@Param("companyId") Integer companyId);

    @Query("""
    SELECT wa FROM WorkerAttendance wa
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH w.company c
    WHERE wa.worker.id = :workerId
    AND wa.checkInTime >= :startOfDay
    AND wa.checkInTime < :endOfDay
    ORDER BY wa.checkInTime ASC
    """)

    List<WorkerAttendance> findAllByWorkerIdAndDateRange(
                                                           @Param("workerId") Integer workerId,
                                                           @Param("startOfDay") LocalDateTime startOfDay,
                                                           @Param("endOfDay") LocalDateTime endOfDay
    );




    @Query("""
    SELECT wa FROM WorkerAttendance wa 
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH wa.worker.company c
    WHERE wa.id = :attendanceId
""")
    Optional<WorkerAttendance> findByAttendanceId(@Param("attendanceId") Integer attendanceId);


    @Query("""
    
    SELECT  DISTINCT wa FROM WorkerAttendance wa 
    LEFT JOIN FETCH wa.worker w
    LEFT JOIN FETCH wa.worker.company c
""")
    List<WorkerAttendance> findAllWithDetails();
}