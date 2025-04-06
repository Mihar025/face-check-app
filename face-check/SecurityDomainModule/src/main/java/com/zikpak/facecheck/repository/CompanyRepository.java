package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.requestsResponses.company.finance.EmployeeSalaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findByCompanyName(String companyName);

    @Modifying
    @Query("UPDATE Company c SET c.workersQuantity = c.workersQuantity + 1 WHERE c.companyName = :companyName")
    void incrementWorkers(@Param("companyName") String companyName);




    @Query("""
    SELECT new com.zikpak.facecheck.requestsResponses.company.finance.EmployeeSalaryResponse(
        u.id,
        u.firstName,
        u.lastName,
        u.email,
        (SELECT wp2.baseHourlyRate 
         FROM WorkerPayroll wp2 
         WHERE wp2.worker = u 
         ORDER BY wp2.periodStart DESC 
         LIMIT 1)
    )
    FROM User u
    WHERE u.company.id = :companyId
""")
    List<EmployeeSalaryResponse> findAllInCompanyByBaseHourRate(@Param("companyId") Integer companyId);

    @Query("""
    SELECT new com.zikpak.facecheck.requestsResponses.company.finance.EmployeeSalaryResponse(
        u.id,
        u.firstName,
        u.lastName,
        u.email,
        wp.baseHourlyRate
    )
    FROM User u
    JOIN WorkerPayroll wp ON wp.worker = u
    WHERE u.company.id = :companyId AND u.id = :userId
    ORDER BY wp.periodStart DESC 
    LIMIT 1
""")
    EmployeeSalaryResponse findEmployeeByCompanyAndUserIdWithBaseHourRate(
            @Param("companyId") Integer companyId,
            @Param("userId") Integer userId
    );



}