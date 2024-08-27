package com.prj.furni_shop.modules.order.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.order.dto.request.OrderRequest;
import com.prj.furni_shop.modules.order.dto.response.OrderResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderSummaryResponse;
import com.prj.furni_shop.modules.order.service.UserOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Đơn hàng", description = "APIs đơn hàng cá nhân")
public class UserOrderController {

    UserOrderService userOrderService;

    @Operation(summary = "Lấy tất cả đơn hàng của tôi theo các trạng thái", description = "Lấy tất cả đơn hàng của tôi theo từng trạng thái")
    @GetMapping("/myOrders")
    public ApiResponse<List<OrderSummaryResponse>> getAllMyOrdersByStatus(
            @RequestParam(value = "status", required = false) Integer status
    ) {
        return ApiResponse.<List<OrderSummaryResponse>>builder()
                .result(userOrderService.getAllMyOrders(status))
                .build();
    }

    @Operation(summary = "Xem chi tiết 1 đơn hàng", description = " Xem chi tiết 1 đơn hàng")
    @GetMapping("/myOrders/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetails(
            @PathVariable int orderId
    ) {
        return ApiResponse.<OrderResponse>builder()
                .result(userOrderService.getOrderDetails(orderId))
                .build();
    }

    @Operation(summary = "Tạo đơn hàng", description = "Tạo 1 đơn hàng")
    @PostMapping()
    public ApiResponse<String> createOrder(
            @RequestBody OrderRequest request
            ) {
        return ApiResponse.<String>builder()
                .message(userOrderService.createOrder(request))
                .build();
    }

    @Operation(summary = "Hủy đơn hàng", description = "Hủy 1 đơn hàng")
    @PutMapping("/delete-order/{orderId}")
    public ApiResponse<String> cancelOrder(
            @PathVariable int orderId
    ) {
        return ApiResponse.<String>builder()
                .result(userOrderService.cancelOrder(orderId))
                .build();
    }

    @Operation(summary = "Xác nhận đã nhận được hàng", description = "Xác nhận đã nhận được hàng")
    @PutMapping("/confirm-order/{orderId}")
    public ApiResponse<String> confirmOrder(
            @PathVariable int orderId
    ) {
        return ApiResponse.<String>builder()
                .result(userOrderService.confirmOrder(orderId))
                .build();
    }
}
