package com.prj.furni_shop.modules.voucher.entity;

import com.prj.furni_shop.modules.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int voucherId;

    String code;

    String title;

    Double discountPercent;

    Double minValueOrder;

    Double maxValueDiscount;

    Integer quantity;

    String description;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<UserVoucher> userVouchers;

    @OneToMany(mappedBy = "voucher", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    List<Order> orders;

    @PreRemove
    private void preRemove() {
        for (Order order : orders) {
            order.setVoucher(null);
        }
    }
}
