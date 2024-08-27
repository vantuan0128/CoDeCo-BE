package com.prj.furni_shop.modules.order.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.order.dto.response.OrderResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderSummaryResponse;
import com.prj.furni_shop.modules.order.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý đơn hàng", description = "APIs liên quan đến quản lý đơn hàng")
public class AdminOrderController {

    AdminOrderService adminOrderService;

    @Operation(summary = "Lấy tất cả đơn hàng của người dùng", description = "Lấy tất cả đơn hàng của người dùng ở tất cả trạng thái")
    @GetMapping("/allOrders")
    public ApiResponse<PaginationWrapper<OrderSummaryResponse>> getAllOrders(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<OrderSummaryResponse>>builder()
                .result(adminOrderService.getAllOrders(status, page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Xem đơn hàng của người dùng", description = "Xem đơn hàng của người dùng")
    @GetMapping("/allOrders/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetails(
            @PathVariable int orderId
    ) {
        return ApiResponse.<OrderResponse>builder()
                .result(adminOrderService.getOrderDetails(orderId))
                .build();
    }

    @Operation(summary = "Thay đổi trạng thái đơn hàng của người dùng", description = "Thay đổi trạng thái đơn hàng của người dùng")
    @PutMapping("change-status/{orderId}")
    public ApiResponse<String> changeStatus(
            @PathVariable int orderId) {
        return ApiResponse.<String>builder()
                .result(adminOrderService.changeStatus(orderId))
                .build();
    }

    @Operation(summary = "Thống kê đơn hàng theo các trạng thái", description = "Thống kê đơn hàng theo các trạng thái")
    @GetMapping("/stats-orders")
    public ApiResponse<Map<String,Long>> statsOrder(
    ) {
        return ApiResponse.<Map<String,Long>>builder()
                .result(adminOrderService.statsOrder())
                .build();
    }

}
