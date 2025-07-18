package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.LocationRecord;
import com.zikpak.facecheck.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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


    Optional<LocationRecord> findTopByUserOrderByTimestampDesc(User user);
}
