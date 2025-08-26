package com.zikpak.facecheck.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_dependents")
@Entity
public class Dependents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName ="";

    @Column(name = "last_name")
    private String lastName = "";

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
