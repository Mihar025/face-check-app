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

    @Column(name = "amount")
    private BigDecimal amount  = BigDecimal.ZERO;;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "quarter")
    private Integer quarter = 0;

    @Column(name = "year")
    private Integer year = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type_enum")
    private PaymentType paymentTypeEnum;

    @Column(name = "notes")
    private String notes = "";

}
