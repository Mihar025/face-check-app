package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wc_risk_class")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WcRiskClass {

    /** Код класса риска (PRIMARY KEY) */
    @Id
    @Column(length = 10, nullable = true)
    private String code;

    /** Описание: например, "Строительные работы — общие" */
    @Column(nullable = true)
    private String description;

    @Column(precision = 6, scale = 4, nullable = true)
    private BigDecimal rate;

    /** Отраслевой тег для удобства: CONSTRUCTION, OFFICE, MEDICAL и т.д. */
    @Column(length = 30, nullable = true)
    private String industryTag;

    /** Год начала действия этой ставки (чтобы версии хранить) */
    @Column(nullable = true)
    private Integer effectiveYear;
}