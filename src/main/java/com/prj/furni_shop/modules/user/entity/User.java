package com.prj.furni_shop.modules.user.entity;

import com.prj.furni_shop.modules.authentication.entity.Token;
import com.prj.furni_shop.modules.cart.entity.Cart;
import com.prj.furni_shop.modules.notification.entity.Notification;
import com.prj.furni_shop.modules.order.entity.Order;
import com.prj.furni_shop.modules.user.enums.Role;
import com.prj.furni_shop.modules.user.enums.Status;
import com.prj.furni_shop.modules.voucher.entity.UserVoucher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "user")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int userId;

    String email;

    String password;

    String phoneNumber;

    String firstName;

    String lastName;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    Role role = Role.USER;

    String avatarUrl;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    Status isActive = Status.PENDING;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    String otp;

    LocalDateTime otpGeneratedTime;

    @Builder.Default
    int authVersion = 1;

    @Builder.Default
    int googleAccountId = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Token> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Address> addresses;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<UserVoucher> userVouchers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Notification> notifications;

    public void refreshAuthVersion(int AUTH_VERSION_DIV) {
        this.authVersion = (int) (System.currentTimeMillis() % AUTH_VERSION_DIV);
    }

    public String getFullName() {
        return String.join(" ", this.firstName != null ? this.firstName : "", this.lastName != null ? this.lastName : "");
    }
}
