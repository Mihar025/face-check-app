package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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


}
