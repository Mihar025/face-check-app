package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.LocationRecord;
import com.zikpak.facecheck.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRecordRepository extends JpaRepository<LocationRecord, Integer> {

    @Query(value = """
    SELECT lr.* FROM location_record lr
    WHERE lr.user_id = :userId
    AND DATE(lr."timestamp" AT TIME ZONE 'UTC' AT TIME ZONE 'America/New_York') = :date
    ORDER BY lr."timestamp" ASC
    """, nativeQuery = true)
    List<LocationRecord> findByUserAndDate(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date
    );

    @Query("""
    SELECT lr FROM LocationRecord lr
    LEFT JOIN FETCH lr.user u
    WHERE lr.user = :user
    ORDER BY lr.timestamp DESC
    LIMIT 1
    """)
    Optional<LocationRecord> findTopByUserOrderByTimestampDesc(User user);
}
