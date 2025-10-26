package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.employee.WorkSite;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerSiteRepository extends JpaRepository<WorkSite, Integer> {

    @Query(
            """
    SELECT ws FROM WorkSite ws
    LEFT JOIN FETCH ws.company c
    WHERE c.id = :companyId
"""
    )
    Page<WorkSite> findAllByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);


    @Query(
            """
    SELECT ws FROM WorkSite ws
    LEFT JOIN FETCH ws.company c
    WHERE c.id = :companyId
"""
    )
    List<WorkSite> findAllByCompanyId(@Param("companyId") Integer companyId);


    @Query("""
    SELECT ws FROM WorkSite ws
    LEFT JOIN FETCH ws.company c
    LEFT JOIN FETCH ws.users u
    WHERE ws.id = :workSiteId
    """)
    Optional<WorkSite> findByIdWithUsers(@Param("workSiteId") Integer workSiteId);


    @Query("""
    SELECT ws FROM WorkSite ws
    LEFT JOIN FETCH ws.company c
    WHERE ws.id = :id
    """)
    Optional<WorkSite> findByIdWithCompany(@Param("id") Integer id);



    @Query("SELECT COUNT(w) FROM WorkSite w WHERE w.company.id = :companyId")
    Integer countWorkSitesByCompanyId(@Param("companyId") Integer companyId);

    Integer countByCompanyId(Integer companyId);

    void clearWorkSiteReferences(Integer workSiteId);
}
