package com.zikpak.facecheck.entity.employee;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "work_site")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "allowed_radius")
    private Double allowedRadius;

    @Column(name = "work_day_start")
    private LocalTime workDayStart;

    @Column(name = "work_day_end")
    private LocalTime workDayEnd;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(nullable = true, name = "is_worker_did_punch_in")
    private Boolean isWorkerDidPunchIn;

    @ElementCollection
    @CollectionTable(name = "inactive_days")
    @Column(name = "inactive_date")
    private Set<LocalDate> inactiveDays;


    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;


    @ManyToMany
    @JoinTable(
            name = "user_work_sites",
            joinColumns = @JoinColumn(name = "work_site_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "custom_worker_radius")
    @MapKeyColumn(name = "worker_id")
    @Column(name = "radius")
    private Map<Integer, Double> customRadius = new HashMap<>();


    public void removeUser(User user) {
        users.remove(user);
        user.getWorkSites().remove(this);
    }


}