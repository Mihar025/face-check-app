package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.employee.WorkSite;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    WHERE ws.id = :id
    """)
    Optional<WorkSite> findByIdWithCompany(@Param("id") Integer id);



    Integer countByCompanyId(Integer companyId);

    @Modifying
    @Query(value = "DELETE FROM work_site WHERE id = :workSiteId", nativeQuery = true)
    void clearWorkSiteReferences(@Param("workSiteId") Integer workSiteId);


    @Modifying
    @Query(value = "DELETE FROM user_work_sites WHERE work_site_id = :workSiteId", nativeQuery = true)
    void clearWorkSiteUserAssociations(@Param("workSiteId") Integer workSiteId);

    @Modifying
    @Query("UPDATE User u SET u.currentWorkSite = null WHERE u.currentWorkSite.id = :workSiteId")
    void clearCurrentWorkSiteReferences(@Param("workSiteId") Integer workSiteId);

}
