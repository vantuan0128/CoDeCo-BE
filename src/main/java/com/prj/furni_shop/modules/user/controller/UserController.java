package com.prj.furni_shop.modules.user.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.user.dto.request.RecoverPasswordDto;
import com.prj.furni_shop.modules.user.dto.request.UpdatePasswordDto;
import com.prj.furni_shop.modules.user.dto.request.UserUpdateDto;
import com.prj.furni_shop.modules.user.dto.response.ChangePasswordResponse;
import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import com.prj.furni_shop.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class UserController {
    UserService userService;

    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Cập nhật thông tin cá nhân của người dùng hiện tại")
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyInfo(
            @Valid
            @RequestBody UserUpdateDto request
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMyInfo(request))
                .build();
    }

    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy thông tin cá nhân của người dùng hiện tại")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @Operation(summary = "Xóa tài khoản cá nhân", description = "Xóa tài khoản cá nhân của người dùng hiện tại")
    @DeleteMapping("/me")
    public ApiResponse<String> deleteMe() {
        userService.deleteMe();
        return ApiResponse.<String>builder()
                .message("User has been deleted")
                .build();
    }

    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu của người dùng hiện tại")
    @PutMapping("/change-password")
    public ApiResponse<ChangePasswordResponse> changePassword(
            @Valid
            @RequestBody UpdatePasswordDto request,
            HttpServletResponse response) {
        return ApiResponse.<ChangePasswordResponse>builder()
                .result(userService.changePassword(request, response))
                .build();
    }

    @Operation(summary = "Tải lên avatar", description = "Tải lên và cập nhật avatar cho người dùng hiện tại")
    @PostMapping("/set-avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.updateAvatar(file);
            return ApiResponse.<String>builder()
                    .message("Avatar uploaded successfully: " + avatarUrl)
                    .build();
        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                    .message("Error uploading avatar: " + e.getMessage())
                    .build();
        }
    }
}
