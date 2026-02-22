package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer companyId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );


    Page<Notification> findByCompanyIdAndAdminOnlyFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer companyId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );


    long countByCompanyIdAndIsReadFalseAndCreatedAtBetween(Integer companyId,
                                                           LocalDateTime start,
                                                           LocalDateTime end);

    long  countByCompanyIdAndAdminOnlyFalseAndIsReadFalseAndCreatedAtBetween(
            Integer companyId,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<Notification> findByCompanyIdAndTargetUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer companyId, Integer targetUserId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    long countByCompanyIdAndTargetUserIdAndIsReadFalseAndCreatedAtBetween(
            Integer companyId, Integer targetUserId, LocalDateTime start, LocalDateTime end);


    @Query("SELECT n FROM Notification n WHERE n.company.id = :companyId " +
            "AND n.createdAt BETWEEN :start AND :end " +
            "AND (n.adminOnly = false AND n.targetUser IS NULL " +
            "     OR n.targetUser.id = :userId) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> findWorkerNotifications(
            @Param("companyId") Integer companyId,
            @Param("userId") Integer userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

}
