package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.DocumentsI9;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentsI9Repository extends JpaRepository<DocumentsI9, Integer> {

    @Query(value = """
    SELECT d 
    FROM DocumentsI9 d
    WHERE d.user.id = :userId
""")
    List<DocumentsI9> findAllDocumentsByUserId(@Param("userId") Integer userId);


}
