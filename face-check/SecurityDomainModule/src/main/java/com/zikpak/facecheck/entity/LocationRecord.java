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

    @Column(nullable = false, name = "latitude")
    private double latitude;

    @Column(nullable = false, name = "longitude")
    private double longitude;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "bearing")
    private Double bearing;

    @Column(name = "altitude")
    private Double altitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private LocationProvider provider;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "distance_from_previous")
    private Double distanceFromPrevious;

}
