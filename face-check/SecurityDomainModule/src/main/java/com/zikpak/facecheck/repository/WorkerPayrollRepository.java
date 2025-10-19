package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.requestsResponses.YearToDateForWorkerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerPayrollRepository extends JpaRepository<WorkerPayroll, Integer> {


    @Query("""
    SELECT new com.zikpak.facecheck.requestsResponses.YearToDateForWorkerResponse(
        COALESCE(SUM(wp.netPay), 0.0),
        COALESCE(SUM(wp.grossPay), 0.0),
        COALESCE(SUM(wp.medicare), 0.0),
        COALESCE(SUM(wp.socialSecurityEmployee), 0.0),
        COALESCE(SUM(wp.federalWithholding), 0.0),
        COALESCE(SUM(wp.nyStateWithholding), 0.0),
        COALESCE(SUM(wp.nyLocalWithholding), 0.0),
        COALESCE(SUM(wp.nyDisabilityWithholding), 0.0),
        COALESCE(SUM(wp.nyPaidFamilyLeave), 0.0)
    )
    FROM WorkerPayroll wp
    WHERE wp.worker.id = :workerId
    AND wp.company.id = :companyId
    AND YEAR(wp.periodStart) = :year
""")
    Optional<YearToDateForWorkerResponse> yearToDateTaxesForWorker(
            @Param("workerId") Integer workerId,
            @Param("companyId") Integer companyId,
            @Param("year") Integer year
    );



    Optional<WorkerPayroll> findFirstByWorkerIdAndPeriodEndIsNotNullOrderByPeriodEndDesc(Integer userId);

    // (опционально) проверить, есть ли запись, перекрывающая текущую неделю
    @Query("""
        select case when count(p) > 0 then true else false end
        from WorkerPayroll p
        where p.worker.id = :userId
          and p.periodStart <= :weekEnd
          and p.periodEnd   >= :weekStart
    """)
    boolean existsForWeek(@Param("userId") Integer userId,
                          @Param("weekStart") LocalDate weekStart,
                          @Param("weekEnd")   LocalDate weekEnd);


    @Query("""
    SELECT new com.zikpak.facecheck.requestsResponses.YearToDateForWorkerResponse(
        COALESCE(SUM(wp.netPay), 0.0),
        COALESCE(SUM(wp.grossPay), 0.0),
        COALESCE(SUM(wp.medicare), 0.0),
        COALESCE(SUM(wp.socialSecurityEmployee), 0.0),
        COALESCE(SUM(wp.federalWithholding), 0.0),
        COALESCE(SUM(wp.nyStateWithholding), 0.0),
        COALESCE(SUM(wp.nyLocalWithholding), 0.0),
        COALESCE(SUM(wp.nyDisabilityWithholding), 0.0),
        COALESCE(SUM(wp.nyPaidFamilyLeave), 0.0)
    )
    FROM WorkerPayroll wp
    WHERE wp.company.id = :companyId
    AND YEAR(wp.periodStart) = :year
""")
    Optional<YearToDateForWorkerResponse> yearToDateTaxesForAllWorkers(
            @Param("companyId") Integer companyId,
            @Param("year") Integer year
    );

    @Query("""
        SELECT wp
          FROM WorkerPayroll wp
         WHERE wp.company.id = :companyId
           AND wp.periodStart <= :endDate
           AND wp.periodEnd   >= :startDate
        """)
    List<WorkerPayroll> findAllByCompanyIdAndPeriodOverlap(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate);



    @Query("""
    SELECT COALESCE(SUM(wp.grossPay), 0)
    FROM WorkerPayroll wp
    WHERE wp.worker.company.id = :companyId
    AND YEAR(wp.periodStart) = :year
    AND MONTH(wp.periodStart) = :month
""")
    BigDecimal getTotalPayrollForMonth(Integer companyId, Integer year, Integer month);

    Optional<WorkerPayroll> findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(
            User worker, LocalDate date, LocalDate sameDate);

    List<WorkerPayroll> findAllByWorkerAndPeriodStartBetween(
            User worker, LocalDate startDate, LocalDate endDate);


    List<WorkerPayroll> findAllByWorkerIdAndPeriodEndLessThanEqual(Integer workerId, LocalDate endDate);


    @Query("SELECT wp FROM WorkerPayroll wp " +
            "WHERE wp.worker.id = :workerId " +
            "AND wp.periodStart >= :startOfYear " +
            "AND wp.periodStart <= :endOfYear")
    List<WorkerPayroll> findAllByWorkerIdAndYear(@Param("workerId") Integer workerId,
                                                 @Param("startOfYear") LocalDate startOfYear,
                                                 @Param("endOfYear") LocalDate endOfYear);


    @Query(value = """
    SELECT wp 
    FROM WorkerPayroll wp
    WHERE wp.worker.id = :userId
    AND wp.periodEnd = :periodEnd
""")
    Optional<WorkerPayroll> findWorkerPayrollByPeriodEnd(@Param("userId") Integer workerId,
                                                         @Param("periodEnd") LocalDate periodEnd);



    List<WorkerPayroll> findAllByWorkerIdAndPeriodEndBetween(Integer id, LocalDate of, LocalDate periodEnd);


    List<WorkerPayroll> findAllByPeriodEnd(LocalDate periodEnd);



    @Query("SELECT wp FROM WorkerPayroll wp WHERE wp.worker.company.id = :companyId " +
            "AND wp.periodStart >= :startDate AND wp.periodEnd <= :endDate")
    List<WorkerPayroll> findAllByCompanyIdAndPeriodBetween(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("""
    SELECT wp 
    FROM WorkerPayroll wp 
    WHERE wp.periodStart >= :startDate 
    AND wp.periodEnd <= :endDate
""")
    List<WorkerPayroll> findAllByPeriodBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            Integer companyId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
select wp
from WorkerPayroll wp
join fetch wp.worker w 
join fetch wp.riskClass
where wp.company.id = :companyId
  and wp.periodEnd between :start and :end
""")
    List<WorkerPayroll> findByCompanyIdAndPeriodEndBetweenFetchRisk(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end
    );


    @Query("SELECT COALESCE(SUM(e.grossPay), 0) " +
            "FROM WorkerPayroll e " +
            "WHERE e.company.id = :companyId " +
            "AND e.periodStart BETWEEN :start AND :end")
    BigDecimal sumGrossWages(
            @Param("companyId") Integer companyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    List<WorkerPayroll> findAllByCompanyId(Integer companyId);
}