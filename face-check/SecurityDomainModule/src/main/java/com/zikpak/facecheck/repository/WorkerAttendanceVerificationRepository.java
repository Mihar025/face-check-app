package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.RandomAttendanceVerification;
import com.zikpak.facecheck.entity.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkerAttendanceVerificationRepository extends JpaRepository<RandomAttendanceVerification, Integer> {


    @Modifying
    @Transactional
    @Query("UPDATE RandomAttendanceVerification r SET r.randomAttendanceVerificationPhotoUrl = :url WHERE r.id = :id")
    void updatePhotoUrl(@Param("id") Integer id, @Param("url") String url);

    boolean existsByWorkerIdAndStatusAndCreatedAt(Integer workerId, Status status, LocalDate createdAt);


    Optional<RandomAttendanceVerification> findByWorkerIdAndStatusAndCreatedAt(
            Integer workerId, Status status, LocalDate createdAt);

    /**
     * All verifications — for AppOwner (sees everything)
     */
    @Query("""
        SELECT v FROM RandomAttendanceVerification v
        JOIN FETCH v.worker w
        LEFT JOIN FETCH w.company
        ORDER BY v.createdAt DESC, v.randomAttendanceVerificationTime DESC
    """)
    Page<RandomAttendanceVerification> findAllVerificationsForAdmin(Pageable pageable);

    /**
     * Verifications for a specific company — for ADMIN role
     */
    @Query("""
        SELECT v FROM RandomAttendanceVerification v
        JOIN FETCH v.worker w
        LEFT JOIN FETCH w.company c
        WHERE c.id = :companyId
        ORDER BY v.createdAt DESC, v.randomAttendanceVerificationTime DESC
    """)
    Page<RandomAttendanceVerification> findAllVerificationsByCompanyId(
            @Param("companyId") Integer companyId,
            Pageable pageable);

    /**
     * Filter by status
     */
    @Query("""
        SELECT v FROM RandomAttendanceVerification v
        JOIN FETCH v.worker w
        LEFT JOIN FETCH w.company c
        WHERE (:companyId IS NULL OR c.id = :companyId)
        AND (:status IS NULL OR v.status = :status)
        AND (:dateFrom IS NULL OR v.createdAt >= :dateFrom)
        AND (:dateTo IS NULL OR v.createdAt <= :dateTo)
        ORDER BY v.createdAt DESC, v.randomAttendanceVerificationTime DESC
    """)
    Page<RandomAttendanceVerification> findVerificationsFiltered(
            @Param("companyId") Integer companyId,
            @Param("status") Status status,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable);


    /**
     * Count by worker and date (for minimum guarantee logic)
     */
    long countByWorkerIdAndCreatedAt(Integer workerId, LocalDate createdAt);

    /**
     * Stats: count by status for a company today
     */
    long countByWorkerCompanyIdAndStatusAndCreatedAt(Integer companyId, Status status, LocalDate createdAt);
}