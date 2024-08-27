package com.prj.furni_shop.modules.user.entity;

import com.prj.furni_shop.modules.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "address")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int addressId;

    String nameReceiver;

    String phoneReceiver;

    String nation;

    String province;

    String district;

    String detail;

    @Column(columnDefinition = "TINYINT UNSIGNED")
    int addressType;

    @Column(columnDefinition = "TINYINT UNSIGNED")
    @Builder.Default
    int isDefault = 1;

    @Column(name = "user_id")
    int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    User user;

    @OneToMany(mappedBy = "address", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    List<Order> orders;

    @PreRemove
    private void preRemove() {
        for (Order order : orders) {
            order.setAddress(null);
        }
    }

    @Override
    public String toString() {
        return String.join(", ", detail, district, province, nation);
    }
}