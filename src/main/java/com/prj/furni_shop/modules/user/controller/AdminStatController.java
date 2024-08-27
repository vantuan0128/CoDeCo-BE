package com.prj.furni_shop.modules.user.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.user.dto.response.AllStatisticsResponse;
import com.prj.furni_shop.modules.user.dto.response.StatisticsChartResponse;
import com.prj.furni_shop.modules.user.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Thống kê", description = "APIs thống kê")
public class AdminStatController {

    AdminDashboardService adminDashboardService;

    @Operation(summary = "Lấy tất cả thống kê", description = "Lấy tất cả thống kê người dùng, đơn hàng, doanh thu, lượt bán")
    @GetMapping()
    public ApiResponse<AllStatisticsResponse> getAllStatistics() {
        return ApiResponse.<AllStatisticsResponse>builder()
                .result(adminDashboardService.getAllStatistics())
                .build();
    }

    @Operation(summary = "Lấy tất cả thống kê theo khoảng thời gian", description = "Lấy tất cả thống kê theo khoảng thời gian được cung cấp")
    @GetMapping("/chart")
    public ApiResponse<List<StatisticsChartResponse>> getStatisticsChart(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam String type
    ) {
        return ApiResponse.<List<StatisticsChartResponse>>builder()
                .result(adminDashboardService.getStatisticsChart(start, end, type))
                .build();
    }

}
