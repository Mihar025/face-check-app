package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.employee.WorkSite;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LocationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    // Точность GPS в метрах
    private Float accuracy;

    // Скорость движения (м/с)
    private Float speed;

    // Направление движения (0-360 градусов)
    private Float bearing;

    // Высота над уровнем моря
    private Double altitude;

    // Источник геолокации (GPS, NETWORK, FUSED)
    @Enumerated(EnumType.STRING)
    private LocationProvider provider;

    // Уровень заряда батареи при записи
    private Integer batteryLevel;

    // Для оптимизации - расстояние от предыдущей точки
    private Double distanceFromPrevious;

}
