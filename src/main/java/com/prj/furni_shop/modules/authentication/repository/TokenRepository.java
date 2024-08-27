package com.prj.furni_shop.modules.authentication.repository;

import com.prj.furni_shop.modules.authentication.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUserId(int userId);
}
