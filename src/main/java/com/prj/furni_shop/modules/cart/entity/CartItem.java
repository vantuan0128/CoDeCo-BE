package com.prj.furni_shop.modules.cart.entity;

import com.prj.furni_shop.modules.product.entity.ProductDetail;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int cartItemId;

    @Column(name = "product_detail_id")
    int productDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", insertable = false, updatable = false)
    ProductDetail productDetail;

    int count;

    @Column(name = "cart_id")
    int cartId;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    Cart cart;

    @Builder.Default
    Boolean selected = false;
}
