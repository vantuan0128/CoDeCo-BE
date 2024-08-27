package com.prj.furni_shop.modules.order.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.notification.service.NotificationService;
import com.prj.furni_shop.modules.order.dto.response.OrderItemResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderSummaryResponse;
import com.prj.furni_shop.modules.order.entity.Order;
import com.prj.furni_shop.modules.order.entity.OrderItem;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import com.prj.furni_shop.modules.order.mapper.OrderMapper;
import com.prj.furni_shop.modules.order.repository.OrderItemRepository;
import com.prj.furni_shop.modules.order.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    OrderMapper orderMapper;
    NotificationService notificationService;

    public PaginationWrapper<OrderSummaryResponse> getAllOrders(Integer status, int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Order> orderPages;
        if (status == null) {
            orderPages = orderRepository.findAll(pageable);
        } else {
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.fromValue(status);
            } catch (Exception e) {
                throw new AppException(ErrorCode.INVALID_INPUT_DATA);
            }
            orderPages = orderRepository.findAllByStatus(orderStatus, pageable);
        }

        List<OrderSummaryResponse> orderSummaryResponses = orderPages.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
                    OrderItem firstOrderItem = orderItems.isEmpty() ? null : orderItems.get(0);

                    return OrderSummaryResponse.builder()
                            .orderId(order.getOrderId())
                            .status(order.getStatus())
                            .totalMoney(order.getTotalMoney())
                            .productName(firstOrderItem.getName())
                            .imageUrl(firstOrderItem.getImageUrl())
                            .count(order.getCount())
                            .build();
                }).collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(orderPages.getTotalElements())
                .totalPages((int) Math.ceil((double) orderPages.getTotalElements() / pageSize))
                .hasNext(orderPages.hasNext())
                .hasPrevious(orderPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(orderSummaryResponses, paginationInfo);
    }

    public OrderResponse getOrderDetails(int orderId) {
        var context = SecurityContextHolder.getContext();
        var userId = Integer.parseInt(context.getAuthentication().getName());

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(orderItem ->
                        OrderItemResponse.builder()
                                .orderItemId(orderItem.getOrderItemId())
                                .name(orderItem.getName())
                                .imageUrl(orderItem.getImageUrl())
                                .size(orderItem.getSizeName())
                                .color(orderItem.getColorName())
                                .material(orderItem.getMaterialName())
                                .price(orderItem.getPrice())
                                .count(orderItem.getCount())
                                .build()
                )
                .collect(Collectors.toList());

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setUserId(order.getUserId());
        orderResponse.setPaymentMethod(order.getPaymentMethod() == 0 ? "Thanh toán khi nhận hàng" : "Thanh toán qua ví điện tử");
        orderResponse.setIsPaid(order.getIsPaid() == 0 ? "Chưa thanh toán" : "Đã thanh toán");
        orderResponse.setOrderItems(orderItemResponses);

        return orderResponse;

    }

    @Transactional
    public String changeStatus(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if(order.getStatus() == OrderStatus.PENDING) {
            notificationService.createNotificationForOrder(order.getUserId(), order.getOrderId(), 1);
            order.setStatus(OrderStatus.CONFIRMED);
        } else if(order.getStatus() == OrderStatus.CONFIRMED) {
            notificationService.createNotificationForOrder(order.getUserId(), order.getOrderId(), 2);
            order.setStatus(OrderStatus.DELIVERING);
        } else if (order.getStatus() == OrderStatus.DELIVERING) {
            notificationService.createNotificationForOrder(order.getUserId(), order.getOrderId(), 3);
            return "Success, waiting for user confirmation";
        } else throw new AppException(ErrorCode.INVALID_ORDER_STATUS);

        orderRepository.save(order);

        return "Change successfully";
    }

    public Map<String,Long> statsOrder() {
        Map<String,Long> stats = new HashMap<>();

        stats.put("totalOrders", orderRepository.count());
        stats.put("totalPendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
        stats.put("totalConfirmOrders", orderRepository.countByStatus(OrderStatus.CONFIRMED));
        stats.put("totalDeliveringOrders", orderRepository.countByStatus(OrderStatus.DELIVERING));
        stats.put("totalCompletedOrders", orderRepository.countByStatus(OrderStatus.COMPLETED));

        return stats;
    }

}
