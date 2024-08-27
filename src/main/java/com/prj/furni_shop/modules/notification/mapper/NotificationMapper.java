package com.prj.furni_shop.modules.notification.mapper;

import com.prj.furni_shop.modules.notification.dto.request.NotificationRequest;
import com.prj.furni_shop.modules.notification.dto.response.NotificationResponse;
import com.prj.furni_shop.modules.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);

}
