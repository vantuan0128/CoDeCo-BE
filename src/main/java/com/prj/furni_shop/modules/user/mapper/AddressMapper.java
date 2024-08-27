package com.prj.furni_shop.modules.user.mapper;

import com.prj.furni_shop.modules.user.dto.request.AddressDto;
import com.prj.furni_shop.modules.user.dto.response.AddressResponse;
import com.prj.furni_shop.modules.user.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    @Mapping(target = "userId", ignore = true)
    Address toAddress(AddressDto request);

    AddressResponse toAddressResponse(Address request);

    @Mapping(target = "userId", ignore = true)
    void updateUserAddress(@MappingTarget Address address, AddressDto request);
}
