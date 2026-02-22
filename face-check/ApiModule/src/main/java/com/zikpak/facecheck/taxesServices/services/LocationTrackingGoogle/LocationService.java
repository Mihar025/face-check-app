package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import com.zikpak.facecheck.entity.LocationRecord;
import com.zikpak.facecheck.entity.LocationStatistics;
import com.zikpak.facecheck.repository.LocationRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRecordRepository locationRecordRepository;
    private final UserRepository userRepository;


    @Transactional
    public LocationRecordDto saveLocation(Integer userId, LocationUpdateDto dto) {
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

        LocationRecord savedRecord = locationRecordRepository.save(record);
        return toDto(savedRecord);
    }


    public List<LocationRecordDto> saveBatchLocations(Integer userId, List<LocationUpdateDto> locations) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LocationRecord> records = new ArrayList<>();
        LocationRecord previousRecord = locationRecordRepository
                .findTopByUserOrderByTimestampDesc(user)
                .orElse(null);

        for(LocationUpdateDto dto : locations){
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

            if(previousRecord != null){
                double distance = calculateDistance(
                        previousRecord.getLatitude(),
                        previousRecord.getLongitude(),
                        dto.getLatitude(),
                        dto.getLongitude()
                );
                record.setDistanceFromPrevious(distance);
            }

            records.add(record);
            previousRecord = record;
        }

        List<LocationRecord> savedRecords = locationRecordRepository.saveAll(records);

        // Возвращаем DTO
        return savedRecords.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public LocationRecordDto getLastLocation(Integer userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocationRecord record = locationRecordRepository.findTopByUserOrderByTimestampDesc(user)
                .orElseThrow(() -> new EntityNotFoundException("No Location data found"));

        return toDto(record);
    }



    @Transactional
    public List<LocationRecordDto> getLocationHistory(Integer userId, LocalDate date) {
        System.out.println("Searching for user: " + userId + ", date: " + date);

        List<LocationRecord> records = locationRecordRepository.findByUserAndDate(userId, date);

        System.out.println("Found records: " + records.size());

        return records.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }



    private double calculateTotalDistance(List<LocationRecord> records) {
        return records.stream()
                .mapToDouble(r -> r.getDistanceFromPrevious() != null ? r.getDistanceFromPrevious() : 0)
                .sum();
    }


    private double calculateAverageSpeed(List<LocationRecord> records) {
        List<Double> speeds = records.stream()
                .map(LocationRecord::getSpeed)
                .filter(speed -> speed != null && speed > 0)
                .collect(Collectors.toList());

        if(speeds.isEmpty()) return 0;

        return speeds.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
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



    private LocationRecordDto toDto(LocationRecord record) {
        return LocationRecordDto.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .userFirstName(record.getUser().getFirstName())
                .userLastName(record.getUser().getLastName())
                .latitude(record.getLatitude())
                .longitude(record.getLongitude())
                .timestamp(record.getTimestamp())
                .accuracy(record.getAccuracy())
                .speed(record.getSpeed())
                .bearing(record.getBearing())
                .altitude(record.getAltitude())
                .batteryLevel(record.getBatteryLevel())
                .distanceFromPrevious(record.getDistanceFromPrevious())
                .build();
    }


}
