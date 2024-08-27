package com.prj.furni_shop.modules.user.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.user.dto.request.UserCreationDto;
import com.prj.furni_shop.modules.user.dto.request.UserUpdateDto;
import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import com.prj.furni_shop.modules.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý người dùng", description = "APIs quản lý người dùng")
public class AdminController {
    AdminService adminService;

    @Operation(summary = "Tạo người dùng mới", description = "Tạo người dùng mới với các thông tin chi tiết được cung cấp")
    @PostMapping()
    public ApiResponse<UserResponse> createUser(
            @Valid
            @RequestBody UserCreationDto request
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(adminService.createUser(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách người dùng", description = "Lấy danh sách tất cả người dùng")
    @GetMapping("/users")
    public ApiResponse<PaginationWrapper<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<UserResponse>>builder()
                .result(adminService.getUsers(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Thống kê số lượng người dùng theo các trạng thái", description = "Thống kê số lượng người dùng theo các trạng thái")
    @GetMapping("/stats-users")
    public ApiResponse<Map<String,Long>> statsUser(
    ) {
        return ApiResponse.<Map<String,Long>>builder()
                .result(adminService.statsUser())
                .build();
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID", description = "Lấy thông tin chi tiết của người dùng theo ID")
    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable int userId) {
        return ApiResponse.<UserResponse>builder()
                .result(adminService.getUser(userId))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin người dùng theo ID", description = "Cập nhật các thông tin của người dùng theo ID")
    @PutMapping("/users/{userId}")
    public ApiResponse<UserResponse> updateUser(
            @Valid
            @RequestBody UserUpdateDto request,
            @PathVariable int userId
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(adminService.updateUser(userId,request))
                .build();
    }

    @Operation(summary = "Xóa người dùng theo ID", description = "Xóa người dùng theo ID được cung cấp")
    @DeleteMapping("/users/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable int userId) {
        return ApiResponse.<String>builder()
                .message(adminService.deleteUser(userId))
                .build();
    }

    @Operation(summary = "Vô hiệu hóa người dùng theo ID", description = "Vô hiệu hóa người dùng theo ID được cung cấp")
    @PutMapping("/users/banUser/{userId}")
    public ApiResponse<String> banUser(@PathVariable int userId) {
        return ApiResponse.<String>builder()
                .message(adminService.banUser(userId))
                .build();
    }

    @Operation(summary = "Xóa nhiều người dùng", description = "Xóa nhiều người dùng theo danh sách ID được cung cấp")
    @DeleteMapping("/users/bulk-delete")
    public ApiResponse<String> bulkDeleteUser(
            @RequestBody List<Integer> userIds
    ) {
        adminService.bulkDeleteUser(userIds);
        return ApiResponse.<String>builder()
                .message("Users have been deleted")
                .build();
    }

}