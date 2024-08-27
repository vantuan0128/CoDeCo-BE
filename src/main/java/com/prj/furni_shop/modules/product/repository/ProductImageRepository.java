package com.prj.furni_shop.modules.product.repository;

import com.prj.furni_shop.modules.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findAllByProductId(int productId);
}
