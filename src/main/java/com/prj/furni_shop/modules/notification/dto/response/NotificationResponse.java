package com.prj.furni_shop.modules.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prj.furni_shop.modules.notification.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {

    int notificationId;

    String title;

    String content;

    int userId;

    NotificationType notificationType;

    int seen;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime createdAt;
}
