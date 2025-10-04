package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.TermsOfUseAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermOfUsageRepository extends JpaRepository<TermsOfUseAgreement, Integer> {


}
