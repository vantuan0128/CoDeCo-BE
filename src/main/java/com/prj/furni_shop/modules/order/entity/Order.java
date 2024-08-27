package com.prj.furni_shop.modules.order.entity;

import com.prj.furni_shop.modules.order.enums.OrderStatus;
import com.prj.furni_shop.modules.user.entity.Address;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.voucher.entity.Voucher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "orders")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int orderId;

    String note;

    int totalMoney;

    Double discountedPrice;

    int count;

    int paymentMethod;

    @Builder.Default
    int isPaid = 0;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    OrderStatus status = OrderStatus.PENDING;

    @Column(name = "user_id")
    int userId;

    String orderAddress;

    String nameReceiver;

    String phoneReceiver;

    String addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    Address address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<OrderItem> orderItems;

    LocalDateTime createdAt;
}
