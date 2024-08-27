package com.prj.furni_shop.modules.authentication.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.authentication.dto.request.*;
import com.prj.furni_shop.modules.authentication.dto.respone.LoginRespone;
import com.prj.furni_shop.modules.authentication.dto.respone.RefreshTokenRespone;
import com.prj.furni_shop.modules.authentication.service.AuthService;
import com.prj.furni_shop.modules.user.dto.request.RecoverPasswordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Xác thực", description = "APIs liên quan đến quản lý xác thực")
public class AuthController {

    AuthService authService;

    @Operation(summary = "Đăng ký tài khoản", description = "API để đăng ký tài khoản mới")
    @PostMapping("/register")
    public ApiResponse<?> register(
            @Valid
            @RequestBody RegisterRequest request
    ) {
        return ApiResponse.builder()
                .message(authService.register(request))
                .build();
    }

    @Operation(summary = "Xác nhận đường link xác thực", description = "API để xác nhận đường link xác thực")
    @PostMapping("/verify-link")
    public ApiResponse<Boolean> verifyOtp(
            @RequestBody LinkRequest request
    ) {
        return ApiResponse.<Boolean>builder()
                .result(authService.verifyLink(request))
                .build();
    }

    @Operation(summary = "Xác nhận OTP", description = "API để xác nhận OTP")
    @PostMapping("/verify-otp")
    public ApiResponse<Boolean> verifyOtp(
            @RequestBody VerifyOtpRequest request
    ) {
        return ApiResponse.<Boolean>builder()
                .result(authService.verifyOtp(request))
                .build();
    }

    @Operation(summary = "Tạo lại OTP", description = "API để tạo lại mã OTP")
    @PostMapping("/regenerate-otp")
    public ApiResponse<String> regenerateOtp (
            @RequestParam("email") String email
    ) {
        return ApiResponse.<String>builder()
                .message(authService.regenerateOtp(email))
                .build();
    }

    @Operation(summary = "Đăng nhập", description = "API để đăng nhập")
    @PostMapping("/login")
    public ApiResponse<LoginRespone> login(
            @Valid
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        return ApiResponse.<LoginRespone>builder()
                .result(authService.login(request, response))
                .build();
    }

    @Operation(summary = "Làm mới token", description = "API để làm mới token đăng nhập")
    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenRespone> refreshToken(
            @RequestBody RefreshTokenRequest request,
            HttpServletResponse response
    ){
        return ApiResponse.<RefreshTokenRespone>builder()
                .result(authService.refreshToken(request, response))
                .build();
    }


    @Operation(summary = "Gửi mã OTP đến email để lấy lại mật khẩu", description = "API gửi mã OTP đến email để lấy lại mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(
            @RequestParam("email") String email) {
        return ApiResponse.<String>builder()
                .message(authService.forgotPassword(email))
                .build();
    }

    @Operation(summary = "Khôi phục lại mật khẩu", description = "Đặt lại mật khẩu cho người dùng")
    @PostMapping("/recover-password")
    public ApiResponse<String> recoverPassword(
            @Valid
            @RequestBody RecoverPasswordDto request) {
        return ApiResponse.<String>builder()
                .message(authService.recoverPassword(request))
                .build();
    }

    @Operation(summary = "Đăng xuất khỏi 1 thiết bị", description = "API để đăng xuất khỏi thiết bị đang sử dụng")
    @PostMapping("/logout-once")
    public ApiResponse<?> logoutOnce(
            @RequestBody LogoutRequest request
    ){
        return ApiResponse.builder()
                .message(authService.logoutOnce(request))
                .build();
    }

    @Operation(summary = "Đăng xuất khỏi tất cả các thiết bị", description = "API để đăng xuất khỏi tất cả các thiết bị")
    @PostMapping("/logout")
    public ApiResponse<?> logoutEverywhere(
            @RequestBody LogoutRequest request
    ){
        return ApiResponse.builder()
                .message(authService.logoutEverywhere(request))
                .build();
    }

//    @Operation(summary = "Đăng nhập bằng bên thứ 3", description = "API để đăng nhập qua google")
//    @GetMapping("/oauth/google")
//    public ResponseEntity<Void> loginWithGoogle(
//            @RequestParam("code") String code
//    ){
//        String redirectUrl = authService.loginWithGoogle(code);
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create(redirectUrl))
//                .build();
//    }

    @Operation(summary = "Đăng nhập bằng bên thứ 3", description = "API để đăng nhập qua google")
    @GetMapping("/oauth/google")
    public ApiResponse<LoginRespone> loginWithGoogle(
            @RequestParam("code") String code,
            HttpServletResponse response
    ){
        return ApiResponse.<LoginRespone>builder()
                .result(authService.loginWithGoogle(code, response))
                .build();
    }
}
