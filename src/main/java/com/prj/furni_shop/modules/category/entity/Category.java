package com.prj.furni_shop.modules.category.entity;

import com.prj.furni_shop.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "category")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int categoryId;

    String name;

    @Column(name = "parent_id", nullable = true)
    Integer parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true, insertable = false, updatable = false)
    Category parent;

    @OneToMany(mappedBy = "parent")
    List<Category> subCategories;

    @OneToMany(mappedBy = "category")
    List<Product> products;
}
