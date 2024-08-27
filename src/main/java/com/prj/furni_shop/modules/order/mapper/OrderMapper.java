package com.prj.furni_shop.modules.order.mapper;

import com.prj.furni_shop.modules.order.dto.request.OrderRequest;
import com.prj.furni_shop.modules.order.dto.response.OrderResponse;
import com.prj.furni_shop.modules.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    Order toOrder(OrderRequest dto);

    OrderResponse toOrderResponse(Order order);
}
