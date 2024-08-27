package com.prj.furni_shop.modules.notification.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.notification.dto.response.NotificationResponse;
import com.prj.furni_shop.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Thông báo", description = "APIs liên quan đến thông báo cá nhân")
public class UserNotificationController {

    NotificationService notificationService;

    @Operation(summary = "Lấy tất cả thông báo của tôi", description = "Lấy tất cả thông báo của ")
    @GetMapping()
    public ApiResponse<List<NotificationResponse>> getAllMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getAllMyNotifications(page, pageSize))
                .build();
    }

    @Operation(summary = "Đánh dấu 1 thông báo đã đọc", description = "Đánh dấu 1 thông báo đã đọc")
    @PutMapping("/set-as-read/{notificationId}")
    public ApiResponse<String> setAsRead(
            @PathVariable int notificationId
    ) {
        return ApiResponse.<String>builder()
                .message(notificationService.setAsRead(notificationId))
                .build();
    }

    @Operation(summary = "Đánh dấu tất cả thông báo đã đọc", description = "Đánh dấu tất cả thông báo đã đọc")
    @PostMapping("/set-all-as-read")
    public ApiResponse<String> setAllAsRead() {
        return ApiResponse.<String>builder()
                .message(notificationService.setAllAsRead())
                .build();
    }

}
