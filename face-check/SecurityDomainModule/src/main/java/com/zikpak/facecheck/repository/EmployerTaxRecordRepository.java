package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.EmployerTaxRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface EmployerTaxRecordRepository extends JpaRepository<EmployerTaxRecord, Integer> {

    @Query("""
    SELECT etr FROM EmployerTaxRecord etr
    LEFT JOIN FETCH etr.company c
    LEFT JOIN FETCH etr.employee e
    LEFT JOIN FETCH etr.payStub ps
    WHERE etr.company.id = :companyId
    AND etr.periodStart BETWEEN :start AND :end
    """)
    List<EmployerTaxRecord> findByCompanyIdAndPeriodStartBetween(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    // Проверить, рассчитаны ли налоги для данной ведомости
    boolean existsByPayStubId(Integer payStubId);




    @Query(value = """
        SELECT COALESCE(SUM(gross_pay), 0)
        FROM employer_tax_record
        WHERE company_id = :companyId
          AND EXTRACT(YEAR FROM period_start) = :year
    """, nativeQuery = true)
    BigDecimal sumGrossPayByAllEmployeeAndYear(@Param("companyId") Integer companyId,
                                               @Param("year") int year );

    @Query(value = """
        SELECT employee_id AS empId,
               SUM(gross_pay) AS totalGross
          FROM employer_tax_record
         WHERE company_id = :companyId
           AND EXTRACT(YEAR FROM period_start) = :year
         GROUP BY employee_id
        HAVING SUM(gross_pay) > 7000
    """, nativeQuery = true)
    List<Object[]> findEmployeesWithYearlyGrossOver7000(
            @Param("companyId") Integer companyId,
            @Param("year") int year
    );






    @Query(
           value = """
    SELECT COALESCE(SUM(e.futaTax), 0)
    FROM EmployerTaxRecord  e 
    WHERE e.company.id = :companyId
    AND e.periodStart >= :periodStart
    AND e.periodEnd <= :periodEnd
"""
    )
    BigDecimal sumFutaForPeriodStartEnd(
            @Param("companyId") Integer companyId,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd
    );

    @Query(value = """
SELECT COALESCE(SUM(e.sutaTax), 0)
FROM EmployerTaxRecord e 
WHERE e.company.id = :companyId
AND e.periodStart >= :periodStart
AND e.periodEnd <= :periodEnd
""")
    BigDecimal sumSutaForPeriodStartEnd(
            @Param("companyId") Integer companyId,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd
    );





    /**
     * Количество уникальных сотрудников у компании за произвольный период
     */
    @Query("SELECT COUNT(DISTINCT e.employee.id) FROM EmployerTaxRecord e " +
            "WHERE e.company.id = :companyId AND e.periodStart BETWEEN :start AND :end")
    int countDistinctEmployeesByCompanyAndYear(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );



    @Query("SELECT COALESCE(SUM(e.federalWithholding), 0) " +
            "FROM EmployerTaxRecord e " +
            "WHERE e.company.id = :companyId " +
            "AND e.periodStart BETWEEN :start AND :end")
    BigDecimal sumFederalWithholding(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );




    @Query("SELECT COALESCE(SUM(e.grossPay), 0) " +
            "FROM EmployerTaxRecord e " +
            "WHERE e.company.id = :companyId " +
            "AND e.periodStart BETWEEN :start AND :end")
    BigDecimal sumGrossWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    // новые:
    @Query("SELECT COALESCE(SUM(r.socialSecurityTaxableWages),0) FROM EmployerTaxRecord r WHERE r.employee.id = :employeeId AND YEAR(r.createdAt) = :year")
    BigDecimal sumSsTaxableWagesByEmployeeAndYear(@Param("employeeId") Integer employeeId, @Param("year") int year);



    @Query("SELECT COALESCE(SUM(r.socialSecurityTaxableWages),0) " +
            "FROM EmployerTaxRecord r " +
            "WHERE r.company.id = :companyId " +
            "  AND r.periodStart BETWEEN :start AND :end")
    BigDecimal sumSocialSecurityTaxableWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end);


    @Query("SELECT COALESCE(SUM(r.socialSecurityTips),0) " +
            "FROM EmployerTaxRecord r " +
            "WHERE r.company.id = :companyId " +
            "  AND r.periodStart BETWEEN :start AND :end")
    BigDecimal sumSocialSecurityTips(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end);

    /**
     * Сумма Taxable Medicare wages & tips (5c) за период для компании
     */
    @Query("SELECT COALESCE(SUM(r.medicareTaxableWages),0) " +
            "FROM EmployerTaxRecord r " +
            "WHERE r.company.id = :companyId " +
            "  AND r.periodStart BETWEEN :start AND :end")
    BigDecimal sumMedicareTaxableWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end);

    /**
     * Сумма Wages subject to Additional Medicare Tax (5d) за период для компании
     */
    @Query("SELECT COALESCE(SUM(r.additionalMedicareWages),0) " +
            "FROM EmployerTaxRecord r " +
            "WHERE r.company.id = :companyId " +
            "  AND r.periodStart BETWEEN :start AND :end")
    BigDecimal sumAdditionalMedicareTaxableWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end);


    @Query("SELECT COALESCE(SUM(e.sutaTaxableWages), 0) FROM EmployerTaxRecord e WHERE e.employee.id = :id AND YEAR(e.periodEnd) = :year")
    BigDecimal sumSutaTaxableWagesByEmployeeAndYear(@Param("id") Integer id, @Param("year") int year);



    @Query("SELECT COALESCE(SUM(e.futaTaxableWages), 0) FROM EmployerTaxRecord e WHERE e.employee.id = :id AND YEAR(e.periodEnd) = :year")
    BigDecimal sumFutaTaxableWagesByEmployeeAndYear(@Param("id") Integer id, @Param("year") int year);



    boolean existsByEmployeeIdAndPeriodStartAndPeriodEnd(
            Integer employeeId,
            LocalDate periodStart,
            LocalDate periodEnd
    );




    @Query("""
    SELECT etr FROM EmployerTaxRecord etr
    LEFT JOIN FETCH etr.company c
    LEFT JOIN FETCH etr.employee e
    LEFT JOIN FETCH etr.payStub ps
    WHERE etr.company.id = :companyId
    AND etr.periodStart >= :startDate
    AND etr.periodEnd <= :endDate
    """)
    List<EmployerTaxRecord> findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Query("""
    SELECT COALESCE(SUM(e.sutaTaxableWages), 0)
    FROM EmployerTaxRecord e
    WHERE e.employee.id = :employeeId
      AND e.periodEnd < :periodStart
""")
    BigDecimal sumSutaTaxableWagesByEmployeeBeforeDate(@Param("employeeId") Integer employeeId,
                                                       @Param("periodStart") LocalDate periodStart);



    @Query("""
    SELECT COALESCE(SUM(e.grossPay),0)
    FROM EmployerTaxRecord e
    WHERE e.employee.id = :employeeId
      AND e.periodStart < :periodStart
""")
    BigDecimal sumGrossPayByEmployeeBeforeDate(
            @Param("employeeId") Integer employeeId,
            @Param("periodStart") LocalDate periodStart
    );

}
