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

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String username);

    boolean existsByEmail(@Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!") @NotBlank(message = "Email is required!") @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters") String email);




    @Query("SELECT u FROM User u WHERE u.company.id = :companyId ORDER BY u.createdDate DESC")
    Page<User> findAllEmployeesInCompany(Pageable pageable, @Param("companyId") Integer companyId);

    // Дополнительные полезные методы:
    @Query("SELECT COUNT(u) FROM User u WHERE u.company.id = :companyId")
    long countEmployeesByCompanyId(@Param("companyId") Integer companyId);

    // Поиск по имени или email
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
JOIN u.workSites w 
WHERE w.id = :workSiteId 
AND u.company.id = :companyId
AND EXISTS (
    SELECT 1 FROM WorkerAttendance a 
    WHERE a.worker = u 
    AND a.checkInTime IS NOT NULL 
    AND a.checkOutTime IS NULL
    AND FUNCTION('DATE', a.checkInTime) = FUNCTION('CURRENT_DATE')
)
""")
    Page<User> findAllWorkerInWorkSite(Pageable pageable,
                                       @Param("workSiteId") Integer workSiteId,
                                       @Param("companyId") Integer companyId);


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

    Optional<User> findUserByFirstName(String firstName);
}
