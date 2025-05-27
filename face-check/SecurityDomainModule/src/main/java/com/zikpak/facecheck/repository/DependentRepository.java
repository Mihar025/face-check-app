package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.Dependents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependentRepository extends JpaRepository<Dependents, Integer> {
    List<Dependents> findAllByUser_Id(Integer userId);
}
