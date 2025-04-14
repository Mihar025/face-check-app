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
    WHERE ws.company.id = :companyId
"""
    )
    Page<WorkSite> findAllByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);


    @Query(
            """
    SELECT ws FROM WorkSite ws
    WHERE ws.company.id = :companyId
"""
    )
    List<WorkSite> findAllByCompanyId(@Param("companyId") Integer companyId);

}
