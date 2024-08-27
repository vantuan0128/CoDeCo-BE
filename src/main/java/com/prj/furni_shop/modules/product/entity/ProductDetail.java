package com.prj.furni_shop.modules.product.entity;

import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.category.entity.Color;
import com.prj.furni_shop.modules.category.entity.Material;
import com.prj.furni_shop.modules.category.entity.Size;
import com.prj.furni_shop.modules.order.entity.OrderItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "product_detail")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int productDetailId;

    int price;

    @Builder.Default
    int quantity = 0;

    @Column(name = "product_id")
    int productId;

    @Column(name = "size_id")
    int sizeId;

    @Column(name = "color_id")
    int colorId;

    @Column(name = "material_id")
    int materialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false, insertable = false, updatable = false)
    Size size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false, insertable = false, updatable = false)
    Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false, insertable = false, updatable = false)
    Material material;

    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<CartItem> cartItems;

    @OneToMany(mappedBy = "productDetail", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    List<OrderItem> orderItems;

    @PreRemove
    private void preRemove() {
        for (OrderItem orderItem : orderItems) {
            orderItem.setProductDetail(null);
        }
    }
}
