package com.prj.furni_shop.modules.review.entity;

import com.prj.furni_shop.modules.order.entity.OrderItem;
import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int reviewId;

    int rating;

    String comment;

    @Column(name = "user_id")
    int userId;

    @Column(name = "order_item_id")
    int orderItemId;

    @Column(name = "product_id")
    int productId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    User user;

    @OneToOne
    @JoinColumn(name = "order_item_id", nullable = false, insertable = false, updatable = false)
    OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
    Product product;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updateAt;

}