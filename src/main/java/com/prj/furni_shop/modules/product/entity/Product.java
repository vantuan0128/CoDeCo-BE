package com.prj.furni_shop.modules.product.entity;

import com.prj.furni_shop.modules.category.entity.Category;
import com.prj.furni_shop.modules.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "product")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int productId;

    String name;

    String description;

    @Builder.Default
    int soldCount = 0;

    @Column(columnDefinition = "TINYINT UNSIGNED")
    @Builder.Default
    int enable = 0;

    @Column(name = "category_id")
    int categoryId;

    Integer minPrice;

    Integer maxPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    Category category;

    @OneToMany(mappedBy = "product")
    List<ProductDetail> productDetails;

    @OneToMany(mappedBy = "product")
    List<ProductImage> productImages;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Sale sale;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Review> reviews;

    LocalDateTime createdAt;

    LocalDateTime updateAt;
}
