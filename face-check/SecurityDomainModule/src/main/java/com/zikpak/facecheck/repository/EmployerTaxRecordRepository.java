package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.EmployerTaxRecord;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployerTaxRecordRepository extends JpaRepository<EmployerTaxRecord, Integer> {

    List<EmployerTaxRecord> findByCompanyId(Integer companyId);

    List<EmployerTaxRecord> findByCompanyIdAndWeekStartBetween(Integer companyId, LocalDate start, LocalDate end);

    List<EmployerTaxRecord> findByEmployeeId(Integer employeeId);

    boolean existsByPayStubId(Integer payStubId);


}
