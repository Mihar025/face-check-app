package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.employee.WorkSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerSiteRepository extends JpaRepository<WorkSite, Integer> {


}
