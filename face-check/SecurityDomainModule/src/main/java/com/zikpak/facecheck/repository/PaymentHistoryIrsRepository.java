package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.entity.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryIrsRepository extends JpaRepository<PaymentHistoryIrs, Integer> {

    Optional<PaymentHistoryIrs> findPaymentHistoryIrsByPaymentDate(LocalDate date);


    Page<PaymentHistoryIrs> findAllByCompany_Id(Integer companyId, Pageable pageable);

    Page<PaymentHistoryIrs> findAllByCompany_IdAndYearAndQuarter(
            Integer companyId,
            int year,
            int quarter,
            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.PAYROLL_TAX_941
""")    BigDecimal getTotalPaidForQuarter(Integer companyId, int quarter, int year);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.PAYROLL_TAX_941
       AND p.paymentDate BETWEEN :start AND :end
    """)
    BigDecimal getTotalPaidForPeriod(@Param("companyId") Integer companyId,
                                     @Param("start")     LocalDate start,
                                     @Param("end")       LocalDate end);



    Optional<PaymentHistoryIrs> findByCompany_IdAndPaymentDateAndYearAndQuarterAndPaymentTypeEnum(
            Integer companyId,
            LocalDate paymentDate,
            int year,
            int quarter,
            PaymentType paymentTypeEnum
    );

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.UNEMPLOYMENT_TAX_940
""")    BigDecimal getTotalPaidForFUTA(Integer companyId, int year);



    //TODO For reports future!

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.UNEMPLOYMENT_TAX_940
""")    BigDecimal getTotalPaidForQuarterFUTA(Integer companyId, int quarter, int year);


    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.STATE_UNEMPLOYMENT_TAX
""")    BigDecimal getTotalPaidForQuarterSUTA(Integer companyId, int quarter, int year);



    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.PERSONAL_INSURANCE
""")    BigDecimal getTotalPaidForQuarterPersonalInsurance(Integer companyId, int quarter, int year);




    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.UNEMPLOYMENT_TAX_940
       AND p.paymentDate BETWEEN :start AND :end
    """)
    BigDecimal getTotalPaidForPeriodFUTA(@Param("companyId") Integer companyId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.STATE_UNEMPLOYMENT_TAX
       AND p.paymentDate BETWEEN :start AND :end
    """)
    BigDecimal getTotalPaidForPeriodSUTA(@Param("companyId") Integer companyId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.PAYROLL_TAX_941
    """)
    BigDecimal getTotalPaidForQuarter941(Integer companyId, int quarter, int year);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.quarter = :quarter
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.WC_Payment
    """)
    BigDecimal getTotalPaidForWCByQuarter(  @Param("companyId") Integer companyId,
                                            @Param("quarter") int quarter,
                                            @Param("year") int year);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
      FROM PaymentHistoryIrs p
     WHERE p.company.id = :companyId
       AND p.year = :year
       AND p.paymentTypeEnum = com.zikpak.facecheck.entity.PaymentType.WC_Payment
    """)
    BigDecimal getTotalPaidForWCPerYear(  @Param("companyId") Integer companyId,
                                          @Param("year") int year);


    @Query("""
  SELECT COALESCE(SUM(p.amount),0)
    FROM PaymentHistoryIrs p
   WHERE p.company.id = :companyId
     AND p.year      = :year
     AND p.quarter   = :quarter
     AND p.paymentTypeEnum IN (
         com.zikpak.facecheck.entity.PaymentType.MCTMT_PREPAYMENT,
         com.zikpak.facecheck.entity.PaymentType.MCTMT_CREDIT
     )
""")
    BigDecimal getTotalMctmtPrepaymentsAndCredits(
            @Param("companyId") Integer companyId,
            @Param("year")      int year,
            @Param("quarter")   int quarter
    );



}
