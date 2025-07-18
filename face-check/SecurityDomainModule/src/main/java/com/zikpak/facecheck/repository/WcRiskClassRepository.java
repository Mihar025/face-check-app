package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.WcRiskClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcRiskClassRepository extends JpaRepository<WcRiskClass, String> {

    List<WcRiskClass> findByIndustryTagAndEffectiveYear(String industryTag, Integer year);
}