package com.prj.furni_shop.modules.notification.repository;

import com.prj.furni_shop.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findNotificationsByUserId(int userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = 1 WHERE n.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") int userId);

}
