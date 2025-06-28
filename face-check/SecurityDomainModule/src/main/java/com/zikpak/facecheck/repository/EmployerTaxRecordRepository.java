package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.EmployerTaxRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий для записей налогов работодателя (EmployerTaxRecord).
 */
@Repository
public interface EmployerTaxRecordRepository extends JpaRepository<EmployerTaxRecord, Integer> {

    // Найти все записи по компании
    List<EmployerTaxRecord> findByCompanyId(Integer companyId);

    List<EmployerTaxRecord> findByCompanyIdAndPeriodStartBetween(
            Integer companyId,
            LocalDate start,
            LocalDate end
    );

    // Найти все записи по сотруднику
    List<EmployerTaxRecord> findByEmployeeId(Integer employeeId);

    // Проверить, рассчитаны ли налоги для данной ведомости
    boolean existsByPayStubId(Integer payStubId);

    /**
     * Сумма валовой оплаты (gross_pay) по сотруднику за указанный год
     */
    @Query(value = """
        SELECT COALESCE(SUM(gross_pay), 0)
        FROM employer_tax_record
        WHERE employee_id = :employeeId
          AND EXTRACT(YEAR FROM period_start) = :year
    """, nativeQuery = true)
    BigDecimal sumGrossPayByEmployeeAndYear(
            @Param("employeeId") Integer employeeId,
            @Param("year") int year
    );

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




    /**
     * Сумма FUTA-налога (futa_tax) для компании за указанный год
     */
    @Query(value = """
        SELECT COALESCE(SUM(futa_tax), 0)
        FROM employer_tax_record
        WHERE company_id = :companyId
          AND EXTRACT(YEAR FROM period_start) = :year
    """, nativeQuery = true)
    BigDecimal sumFutaWagesForYear(
            @Param("companyId") Integer companyId,
            @Param("year") int year
    );

    /**
     * Количество уникальных сотрудников у компании за указанный год
     */
    @Query(value = """
        SELECT COUNT(DISTINCT employee_id)
        FROM employer_tax_record
        WHERE company_id = :companyId
          AND EXTRACT(YEAR FROM period_start) = :year
    """, nativeQuery = true)
    int countDistinctEmployeesForYear(
            @Param("companyId") Integer companyId,
            @Param("year") int year
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

    @Query("SELECT COALESCE(SUM(e.socialSecurityTax), 0) " +
            "FROM EmployerTaxRecord e " +
            "WHERE e.company.id = :companyId " +
            "AND e.periodStart BETWEEN :start AND :end")
    BigDecimal sumSocialSecurity(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT COALESCE(SUM(e.medicareTax), 0) " +
            "FROM EmployerTaxRecord e " +
            "WHERE e.company.id = :companyId " +
            "AND e.periodStart BETWEEN :start AND :end")
    BigDecimal sumMedicare(
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

    @Query("SELECT COALESCE(SUM(r.socialSecurityTips),0) FROM EmployerTaxRecord r WHERE r.employee.id = :employeeId AND YEAR(r.createdAt) = :year")
    BigDecimal sumSsTipsByEmployeeAndYear(@Param("employeeId") Integer employeeId, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(r.medicareTaxableWages),0) FROM EmployerTaxRecord r WHERE r.employee.id = :employeeId AND YEAR(r.createdAt) = :year")
    BigDecimal sumMedicareTaxableByEmployeeAndYear(@Param("employeeId") Integer employeeId, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(r.additionalMedicareWages),0) FROM EmployerTaxRecord r WHERE r.employee.id = :employeeId AND YEAR(r.createdAt) = :year")
    BigDecimal sumAddlMedicareByEmployeeAndYear(@Param("employeeId") Long employeeId, @Param("year") int year);


    @Query("SELECT COALESCE(SUM(r.socialSecurityTaxableWages),0) " +
            "FROM EmployerTaxRecord r " +
            "WHERE r.company.id = :companyId " +
            "  AND r.periodStart BETWEEN :start AND :end")
    BigDecimal sumSocialSecurityTaxableWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end);

    /**
     * Сумма Taxable Social Security tips (5b) за период для компании
     */
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


    List<EmployerTaxRecord> findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            Integer companyId,
            LocalDate startDate,
            LocalDate endDate
    );



}
