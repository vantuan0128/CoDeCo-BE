package com.prj.furni_shop.modules.notification.entity;

import com.prj.furni_shop.modules.notification.enums.NotificationType;
import com.prj.furni_shop.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int notificationId;

    String title;

    String content;

    @Column(name = "user_id")
    int userId;

    @Enumerated(EnumType.ORDINAL)
    NotificationType notificationType;

    @Column(columnDefinition = "TINYINT UNSIGNED")
    @Builder.Default
    int seen = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    LocalDateTime createdAt;
}
