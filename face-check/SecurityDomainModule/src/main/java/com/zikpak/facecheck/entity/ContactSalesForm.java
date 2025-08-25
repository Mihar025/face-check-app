package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact_sales_form")
@Entity
public class ContactSalesForm {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String firstName = "";

    private String lastName = "";

    private String phoneNumber = "";

    @CreatedDate
    private LocalDate createdDate;




}
