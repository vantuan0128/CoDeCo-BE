package com.prj.furni_shop.modules.user.repository;

import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Procedure(name = "log_auth")
    void log_auth(String type, int userId, String email, String status, String message);

    Page<User> findAll(Pageable pageable);

    long count();

    long countByIsActive(Status isActive);

    @Query("SELECT COUNT(u) FROM user u WHERE u.createdAt <= :endDate")
    Long countUserUntil(@Param("endDate") LocalDateTime endDate);
}
