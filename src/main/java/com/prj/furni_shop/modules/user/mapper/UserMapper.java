package com.prj.furni_shop.modules.user.mapper;

import com.prj.furni_shop.modules.user.dto.request.UserCreationDto;
import com.prj.furni_shop.modules.user.dto.request.UserUpdateDto;
import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import com.prj.furni_shop.modules.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUser(UserCreationDto request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateDto req);
}
