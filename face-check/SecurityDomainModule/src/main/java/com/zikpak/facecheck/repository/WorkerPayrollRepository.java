package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerPayrollRepository extends JpaRepository<WorkerPayroll, Integer> {


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


}