package com.zikpak.facecheck.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class LocationStatistics {
    private LocalDate date;
    private double totalDistance; // в метрах
    private Duration timeAtWork;
    private double averageSpeed; // м/с
    private int pointsCount;
    private LocationRecord firstLocation;
    private LocationRecord lastLocation;

    public static LocationStatistics empty() {
        return LocationStatistics.builder()
                .totalDistance(0)
                .timeAtWork(Duration.ZERO)
                .averageSpeed(0)
                .pointsCount(0)
                .build();
    }

    // Конвертеры для удобства
    public double getTotalDistanceKm() {
        return totalDistance / 1000.0;
    }

    public double getAverageSpeedKmh() {
        return averageSpeed * 3.6;
    }

    public String getTimeAtWorkFormatted() {
        long hours = timeAtWork.toHours();
        long minutes = timeAtWork.toMinutes() % 60;
        return String.format("%d:%02d", hours, minutes);
    }
}
