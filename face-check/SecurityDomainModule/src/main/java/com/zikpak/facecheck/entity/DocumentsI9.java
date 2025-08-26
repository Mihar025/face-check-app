package com.zikpak.facecheck.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "documents_i9")
@Entity
public class DocumentsI9 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_title")
    private String documentTitle = "";

    @Column(name = "issuing_authority")
    private String issuingAuthority = "";

    @Column(name = "document_number")
    private String documentNumber = "";

    @Column(name = "expiration_date")
    private LocalDate expirationDate ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
