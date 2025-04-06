package com.zikpak.facecheck.repository;


import com.zikpak.facecheck.entity.Token;
import com.zikpak.facecheck.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndUser(String code, User user);
}
