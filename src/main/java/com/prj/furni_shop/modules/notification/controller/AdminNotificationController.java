package com.prj.furni_shop.modules.notification.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.notification.dto.request.NotificationRequest;
import com.prj.furni_shop.modules.notification.dto.response.NotificationResponse;
import com.prj.furni_shop.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý thông báo", description = "APIs liên quan đến quản lý thông báo người dùng")
public class AdminNotificationController {

    NotificationService notificationService;

    @Operation(summary = "Tạo thông báo cho người dùng", description = "Tạo thông báo cho người dùng")
    @PostMapping("/create")
    public ApiResponse<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request
    ) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.createNotification(request))
                .build();
    }

}
