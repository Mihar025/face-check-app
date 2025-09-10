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


    List<LocationRecord> findByUserOrderByTimestampAsc(User user);


    List<LocationRecord> findByUserAndTimestampBetweenOrderByTimestampAsc(
            User user,
            Instant from,
            Instant to
    );

    @Query(value = "SELECT * FROM location_record WHERE user_id = :userId " +
            "AND DATE(timestamp) = :date ORDER BY timestamp ASC",
            nativeQuery = true)
    List<LocationRecord> findByUserAndDate(@Param("userId") Integer userId,
                                           @Param("date") LocalDate date);

    Optional<LocationRecord> findTopByUserOrderByTimestampDesc(User user);
}
