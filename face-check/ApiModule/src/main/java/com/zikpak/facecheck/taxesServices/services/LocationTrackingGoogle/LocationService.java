package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import com.zikpak.facecheck.entity.LocationRecord;
import com.zikpak.facecheck.repository.LocationRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRecordRepository locationRecordRepository;
    private final UserRepository userRepository;


    @Transactional
    public LocationRecord saveLocation(Integer userId, LocationUpdateDto dto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocationRecord record = LocationRecord.builder()
                .user(user)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .timestamp(dto.getTimestamp())
                .accuracy(dto.getAccuracy())
                .speed(dto.getSpeed())
                .bearing(dto.getBearing())
                .altitude(dto.getAltitude())
                .batteryLevel(dto.getBatteryLevel())
                .build();


        // Вычисляем расстояние от предыдущей точки
        Optional<LocationRecord> lastRecord = locationRecordRepository
                .findTopByUserOrderByTimestampDesc(user);

        if (lastRecord.isPresent()) {
            double distance = calculateDistance(
                    lastRecord.get().getLatitude(),
                    lastRecord.get().getLongitude(),
                    dto.getLatitude(),
                    dto.getLongitude()
            );
            record.setDistanceFromPrevious(distance);
        }

        return locationRecordRepository.save(record);
    }

    // Получение локаций за период
    public List<LocationRecord> getLocationHistory(Integer userId, LocalDate date) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault());

        return locationRecordRepository.findByUserAndTimestampBetweenOrderByTimestampAsc(
                user,
                startOfDay.toInstant(),
                endOfDay.toInstant()
        );
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Радиус Земли в метрах

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private Duration calculateTimeAtWork(List<LocationRecord> records) {
        if (records.isEmpty()) return Duration.ZERO;

        Duration totalDuration = Duration.ZERO;
        LocationRecord previousRecord = null;

        for (LocationRecord record : records) {
            if (previousRecord != null) {
                Duration interval = Duration.between(
                        previousRecord.getTimestamp(),
                        record.getTimestamp()
                );

                if (interval.toMinutes() < 30) {
                    totalDuration = totalDuration.plus(interval);
                }
            }
            previousRecord = record;
        }

        return totalDuration;
    }


}
