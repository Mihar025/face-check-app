package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wc_risk_class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WcRiskClass {

    @Id
    @Column(length = 10, nullable = true, name = "code")
    private String code;

    @Column(nullable = true, name = "description")
    private String description = "";

    @Column(precision = 6, scale = 4, nullable = true, name = "rate")
    private BigDecimal rate = BigDecimal.ZERO;;

    @Column(length = 30, nullable = true, name = "industry_tag")
    private String industryTag = "";

    @Column(nullable = true, name = "effective_year")
    private Integer effectiveYear = 0;
}