package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("""
    
    SELECT u FROM User u 
    LEFT JOIN FETCH u.company c 
    LEFT JOIN FETCH u.workSites ws 
    WHERE u.email = :email
""")
    Optional<User> findByEmail(@Param("email") String email);



    boolean existsByEmail(@Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" , message = "Email is not formatted well!")
                          @NotBlank(message = "Email is required!")
                          @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters") String email);
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId ORDER BY u.createdDate DESC")
    Page<User> findAllEmployeesInCompany(Pageable pageable, @Param("companyId") Integer companyId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.company.id = :companyId")
    long countEmployeesInCompany(@Param("companyId") Integer companyId);





    @Query("""
    SELECT u 
    FROM User u 
    WHERE u.company.id = :companyId 
    AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) 
    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
""")
    Page<User> findEmployeesByNameOrEmail(
            @Param("companyId") Integer companyId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT u 
    FROM User u 
    JOIN u.roles r 
    WHERE u.company.id = :companyId 
    AND r.name = 'USER'
""")
    Page<User> findAllEmployeesInCompanyWhoseRoleIsUser(Pageable pageable, @Param("companyId") Integer companyId);


    @Query(
            """
    SELECT DISTINCT u
    FROM User u
    JOIN u.roles r
    WHERE u.company.id = :companyId
    AND r.name = 'FOREMAN'
"""
    )
    Page<User> findAllEmployeesInCompanyWhoseRoleIsForeman(Pageable pageable, @Param("companyId") Integer companyId);


    @Query(
            """
        SELECT DISTINCT u
        from User u
        JOIN u.roles r
        WHERE u.company.id = :companyId
        AND r.name = 'ADMIN'
"""
    )
    Page<User> findAllEmployeesInCompanyWhoseRoleIsAdmin(Pageable pageable, @Param("companyId") Integer companyId);


    @Query(
            """
        SELECT DISTINCT u
        from User u
        WHERE u.company.id = :companyId
"""
    )
    Page<User> findAllEmployeesInCertainCompany(Pageable pageable, @Param("companyId") Integer companyId);

    @Query("""
    SELECT DISTINCT u, wa.checkInTime FROM User u
    JOIN u.workSites ws
    JOIN WorkerAttendance wa ON wa.worker = u
    WHERE ws.id = :workSiteId
    AND u.company.id = :companyId
    AND ws.isActive = true
    AND wa.checkInTime IS NOT NULL
    AND wa.checkOutTime IS NULL
    AND CAST(wa.checkInTime AS date) = CURRENT_DATE
    ORDER BY wa.checkInTime DESC
""")
    Page<User> findAllUsersLocatedInWorkSite(
            Pageable pageable,
            @Param("workSiteId") Integer workSiteId,
            @Param("companyId") Integer companyId
    );




    @Query("""
SELECT DISTINCT u FROM User u 
LEFT JOIN FETCH u.workSites ws
LEFT JOIN FETCH u.attendances a
LEFT JOIN FETCH u.company c
WHERE :workSiteId MEMBER OF u.workSites
AND u.company.id = :companyId
AND EXISTS (
    SELECT 1 FROM WorkerAttendance att 
    WHERE att.worker = u 
    AND att.checkInTime IS NOT NULL 
    AND att.checkOutTime IS NULL
    AND FUNCTION('DATE', att.checkInTime) = FUNCTION('CURRENT_DATE')
)
ORDER BY u.createdDate DESC
""")
    List<User> findAllWorkerInWorkSite(
            @Param("workSiteId") Integer workSiteId,
            @Param("companyId") Integer companyId
    );

    @Query("""
SELECT DISTINCT u, wa
FROM User u
JOIN WorkerAttendance wa ON wa.worker = u
WHERE u.company.id = :companyId
AND wa.checkOutTime IS NULL
AND wa.checkInTime IS NOT NULL
AND u.id IN (
    SELECT us.id FROM User us
    JOIN us.workSites ws
    WHERE ws.id = :workSiteId
)
""")
    Page<Object[]> findAllActiveWorkersWithAttendance(
            Pageable pageable,
            @Param("companyId") Integer companyId,
            @Param("workSiteId") Integer workSiteId
    );


    List<User> findAllByCompanyId(Integer companyId);

    @Query("""
    SELECT DISTINCT wp.worker 
    FROM WorkerPayroll wp 
    WHERE wp.company.id = :companyId 
    AND YEAR(wp.periodStart) = :year
""")
    List<User> findWorkersWithPayrollInYear(
            @Param("companyId") Integer companyId,
            @Param("year") Integer year
    );

    @Query("""
    SELECT DISTINCT u 
    FROM User u 
    JOIN u.roles r 
    WHERE u.company.id = :companyId 
    AND r.name = :roleName
""")
    List<User> findAllByCompanyIdAndRole(@Param("companyId") Integer companyId,
                                         @Param("roleName") String roleName);

    List<User> findAllByCompanyIdIn(Collection<Integer> companyIds);

    @Query("""
    
    SELECT DISTINCT u FROM User u
    LEFT JOIN FETCH u.payrolls p
    LEFT JOIN FETCH u.company c 
    WHERE u.company.id = :companyId
    ORDER BY u.id DESC
    
""")
    List<User> findAllEmployeesInCompanyWithDetails(@Param("companyId") Integer companyId);


    @Query("""
    
    SELECT DISTINCT u FROM User u
    LEFT JOIN FETCH u.payrolls p
    LEFT JOIN FETCH u.company c 
    ORDER BY u.id DESC
""")
    List<User> findAllEmployeesForAppOwner();

    @Query("""

    SELECT u FROM User u 
    LEFT JOIN FETCH u.company c
    WHERE u.id = :employeeId
""")
    Optional<User> findByIdPersonalInfo(@Param("employeeId") Integer employeeId);
}
