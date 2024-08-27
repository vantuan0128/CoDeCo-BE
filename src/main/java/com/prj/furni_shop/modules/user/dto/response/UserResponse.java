package com.prj.furni_shop.modules.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prj.furni_shop.modules.user.enums.Role;
import com.prj.furni_shop.modules.user.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    int userId;

    String email;

    String phoneNumber;

    String firstName;

    String lastName;

    Role role;

    String avatarUrl;

    Status isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

    int googleAccountId;
}
