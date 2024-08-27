package com.prj.furni_shop.modules.order.entity;

import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import com.prj.furni_shop.modules.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "order_item")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    ProductDetail productDetail;

    String name;

    String imageUrl;

    String sizeName;

    String colorName;

    String materialName;

    int price;

    int count;

    @Column(name = "order_id")
    int orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, insertable = false, updatable = false)
    Order order;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Review review;
}
