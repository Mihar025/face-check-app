package com.zikpak.facecheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class PaymentHistoryIrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentHistoryIrsId;

    @ManyToOne
    private Company company;

    private BigDecimal amount;

    private LocalDate paymentDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Integer quarter;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentTypeEnum;

    private String notes;

}
