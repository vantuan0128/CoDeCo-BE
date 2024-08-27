package com.prj.furni_shop.modules.notification.service;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.notification.dto.request.NotificationRequest;
import com.prj.furni_shop.modules.notification.dto.response.NotificationResponse;
import com.prj.furni_shop.modules.notification.entity.Notification;
import com.prj.furni_shop.modules.notification.enums.NotificationType;
import com.prj.furni_shop.modules.notification.mapper.NotificationMapper;
import com.prj.furni_shop.modules.notification.repository.NotificationRepository;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import com.prj.furni_shop.modules.order.repository.OrderRepository;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    UserRepository userRepository;
    OrderRepository orderRepository;
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public NotificationResponse createNotification(NotificationRequest request) {

        if (!userRepository.existsById(request.getUserId()))
            throw new AppException(ErrorCode.NOT_EXISTED);

        if (!orderRepository.existsById(request.getOrderId()))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Notification notification = createNotificationInternal(request);

        notificationRepository.save(notification);

        return notificationMapper.toNotificationResponse(notification);
    }

    public void createNotificationForOrder(int userId, int orderId, int notificationType) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setOrderId(orderId);
        request.setNotificationType(notificationType);

        Notification notification = createNotificationInternal(request);

        notificationRepository.save(notification);
    }

    private Notification createNotificationInternal(NotificationRequest request) {
        NotificationType notificationType;
        try {
            notificationType = NotificationType.fromValue(request.getNotificationType());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        String title;
        String content;

        switch (notificationType) {
            case CREATE_ORDER:
                title = "Tạo đơn hàng";
                content = String.format("Đơn hàng %d đã được tạo thành công và đang chờ người bán xác nhận.", request.getOrderId());
                break;
            case CONFIRM_ORDER:
                title = "Xác nhận đơn hàng";
                content = String.format("Đơn hàng %d đã được người bán xác nhận và sẽ sớm chuẩn bị hàng để vận chuyển.", request.getOrderId());
                break;
            case DELIVERING_ORDER:
                title = "Đơn hàng được bàn giao cho đơn vị vận chuyển";
                content = String.format("Đơn hàng %d đã được người bán bàn giao vận chuyển", request.getOrderId());
                break;
            case COMPLETED_ORDER:
                title = "Đơn hàng giao thành công";
                content = String.format("Đơn hàng %d đã được giao thành công tới bạn, vui lòng nhấn đã nhận được hàng. ", request.getOrderId());
                break;
            default:
                throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        return Notification.builder()
                .title(title)
                .content(content)
                .notificationType(notificationType)
                .userId(request.getUserId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<NotificationResponse> getAllMyNotifications(int page, int pageSize) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Notification> notificationPages = notificationRepository.findNotificationsByUserId(userId, pageable);

        return notificationPages.getContent().stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    public String setAsRead(int notificationId) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (notification.getUserId() != userId)
            throw new AppException(ErrorCode.UNAUTHORIZED);

        notification.setSeen(1);

        notificationRepository.save(notification);
        return "Success";
    }

    @Transactional
    public String setAllAsRead() {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        notificationRepository.markAllAsReadByUserId(userId);
        return "Success";
    }

}
