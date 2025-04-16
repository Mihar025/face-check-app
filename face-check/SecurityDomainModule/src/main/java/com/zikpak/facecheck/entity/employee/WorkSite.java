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

    private String siteName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double allowedRadius;


    private LocalTime workDayStart;
    private LocalTime workDayEnd;
    private Boolean isActive;
    @Column(nullable = true)
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