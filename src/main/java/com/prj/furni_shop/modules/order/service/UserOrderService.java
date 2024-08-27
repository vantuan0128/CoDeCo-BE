package com.prj.furni_shop.modules.order.service;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.cart.repository.CartItemRepository;
import com.prj.furni_shop.modules.notification.service.NotificationService;
import com.prj.furni_shop.modules.order.dto.request.OrderRequest;
import com.prj.furni_shop.modules.order.dto.response.OrderItemResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderResponse;
import com.prj.furni_shop.modules.order.dto.response.OrderSummaryResponse;
import com.prj.furni_shop.modules.order.entity.Order;
import com.prj.furni_shop.modules.order.entity.OrderItem;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import com.prj.furni_shop.modules.order.mapper.OrderMapper;
import com.prj.furni_shop.modules.order.repository.OrderItemRepository;
import com.prj.furni_shop.modules.order.repository.OrderRepository;
import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import com.prj.furni_shop.modules.product.repository.ProductDetailRepository;
import com.prj.furni_shop.modules.product.repository.ProductRepository;
import com.prj.furni_shop.modules.user.repository.AddressRepository;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import com.prj.furni_shop.modules.voucher.dto.request.CheckValidVoucherRequest;
import com.prj.furni_shop.modules.voucher.entity.UserVoucher;
import com.prj.furni_shop.modules.voucher.entity.Voucher;
import com.prj.furni_shop.modules.voucher.repository.UserVoucherRepository;
import com.prj.furni_shop.modules.voucher.repository.VoucherRepository;
import com.prj.furni_shop.modules.voucher.service.UserVoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserOrderService {

    UserVoucherService userVoucherService;

    OrderRepository orderRepository;
    OrderMapper orderMapper;
    OrderItemRepository orderItemRepository;
    UserRepository userRepository;
    AddressRepository addressRepository;
    ProductRepository productRepository;
    ProductDetailRepository productDetailRepository;
    CartItemRepository cartItemRepository;
    VoucherRepository voucherRepository;
    UserVoucherRepository userVoucherRepository;
    NotificationService notificationService;

    public List<OrderSummaryResponse> getAllMyOrders(Integer status) {

        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        if(!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        List<Order> orders;
        if (status == null) {
            orders = orderRepository.findAllByUserId(userId);
        } else {
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.fromValue(status);
            } catch (Exception e) {
                throw new AppException(ErrorCode.INVALID_INPUT_DATA);
            }
            orders = orderRepository.findAllByUserIdAndStatus(userId, orderStatus);
        }

        return orders.stream()
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
                })
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetails(int orderId) {
        var context = SecurityContextHolder.getContext();
        var userId = Integer.parseInt(context.getAuthentication().getName());

        if(!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if(order.getUserId() != userId)
            throw new AppException(ErrorCode.UNAUTHORIZED);

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
    public String createOrder(OrderRequest request) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        var address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<CartItem> cartItemList = cartItemRepository.findAllByCartIdAndSelected(user.getCart().getCartId(), true);

        if (cartItemList.isEmpty()) {
            throw new AppException(ErrorCode.NOT_EXISTED);
        }

        List<Integer> cartItemIds = cartItemList.stream()
                .map(CartItem::getCartItemId)
                .toList();

        int totalMoney = 0;
        Double discountedPrice;
        int count = 0;

        List<ProductDetail> productDetailsToUpdate = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        for (Integer cartItemId : cartItemIds) {
            var cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            count += cartItem.getCount();

            ProductDetail productDetail = cartItem.getProductDetail();
            Product product = productDetail.getProduct();

            Double percent = product.getSale() == null ? null : product.getSale().getPercent();
            if (percent != null) {
                totalMoney += (int) (cartItem.getProductDetail().getPrice() * cartItem.getCount() * (100.0 - percent) / 100.0);
            } else {
                totalMoney += cartItem.getProductDetail().getPrice() * cartItem.getCount();
            }

            int quantityToUpdate = cartItem.getCount();

            int newQuantityDetail = productDetail.getQuantity() - quantityToUpdate;

            if (newQuantityDetail < 0) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // Cập nhật số lượng hàng còn lại của product detail
            productDetail.setQuantity(newQuantityDetail);
            productDetailsToUpdate.add(productDetail);

            // Cập nhật số lượng đã bán trong product
            product.setSoldCount(product.getSoldCount() + quantityToUpdate);
            if (!productsToUpdate.contains(product)) {
                productsToUpdate.add(product);
            }
        }

        Order order = orderMapper.toOrder(request);
        order.setUserId(userId);
        order.setTotalMoney(totalMoney);
        order.setCount(count);
        order.setOrderAddress(address.toString());
        order.setPhoneReceiver(address.getPhoneReceiver());
        order.setNameReceiver(address.getNameReceiver());
        order.setAddressType(address.getAddressType() == 0 ? "Nhà riêng" : "Công ty");
        order.setIsPaid(request.getPaymentMethod() == 0 ? 0 : 1);
        order.setCreatedAt(LocalDateTime.now());

        // Check lại lần 2
        if (request.getVoucherId() != null) {

            Voucher voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

            CheckValidVoucherRequest checkValidVoucherRequest = new CheckValidVoucherRequest(voucher.getVoucherId(), totalMoney);

            discountedPrice = userVoucherService.checkValidVoucher(checkValidVoucherRequest);
            if (discountedPrice == null)
                throw new AppException(ErrorCode.VOUCHER_CONDITION_NOT_MET);

            order.setVoucher(voucher);
            order.setDiscountedPrice(discountedPrice);

            UserVoucher userVoucher = userVoucherRepository.findByUserIdAndVoucherId(userId, request.getVoucherId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            userVoucher.setIsUsed(true);
            userVoucherRepository.save(userVoucher);
        }

        Order savedOrder = orderRepository.save(order);

        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

            // Loại bỏ các mặt hàng đã đặt hàng khỏi giỏ hàng
            int result = cartItemRepository.removeCartItem(cartItemId, true);
            if (result == -3) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            } else if (result == -2) {
                throw new AppException(ErrorCode.NOT_EXISTED);
            }

            var productDetail = cartItem.getProductDetail();
            var product = productDetail.getProduct();
            String imageUrl = product.getProductImages().isEmpty() ? "" : product.getProductImages().get(0).getImageUrl();

            OrderItem orderItem = OrderItem.builder()
                    .orderId(savedOrder.getOrderId())
                    .productDetail(productDetail)
                    .name(product.getName())
                    .imageUrl(imageUrl)
                    .sizeName(productDetail.getSize().getName())
                    .colorName(productDetail.getColor().getName())
                    .materialName(productDetail.getMaterial().getName())
                    .price(productDetail.getPrice())
                    .count(cartItem.getCount())
                    .build();

            orderItemRepository.save(orderItem);

        }

        // Lưu các thay đổi
        for (ProductDetail productDetail : productDetailsToUpdate) {
            productDetailRepository.save(productDetail);
        }

        for (Product product : productsToUpdate) {
            productRepository.save(product);
        }

        notificationService.createNotificationForOrder(userId, savedOrder.getOrderId(), 0);

        return "Success";
    }

    @Transactional
    public String cancelOrder(int orderId) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if(order.getStatus() != OrderStatus.PENDING)
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);

        if(order.getUserId() == userId) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            for(OrderItem orderItem : orderItems) {
                ProductDetail productDetail = productDetailRepository.findById(orderItem.getProductDetail().getProductDetailId())
                        .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

                Product product = productDetail.getProduct();

                // Cập nhật lại số lượng sản phẩm trong product detail
                productDetail.setQuantity(productDetail.getQuantity() + orderItem.getCount());
                productDetailRepository.save(productDetail);

                // Hoàn trả lại số lượng đã bán trong product
                product.setSoldCount(product.getSoldCount() - orderItem.getCount());
                productRepository.save(product);
            }

            return "Cancel successfully";
        }
        else throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public String confirmOrder(int orderId) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if(order.getStatus() != OrderStatus.DELIVERING)
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);

        if(order.getUserId() == userId) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            return "Confirm successfully";
        }
        else throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
