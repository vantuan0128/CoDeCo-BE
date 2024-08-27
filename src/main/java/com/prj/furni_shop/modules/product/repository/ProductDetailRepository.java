package com.prj.furni_shop.modules.product.repository;

import com.prj.furni_shop.modules.product.entity.ProductDetail;
import com.prj.furni_shop.modules.product.repository.CustomProductDetailRepo.CustomProductDetailRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer>, CustomProductDetailRepo {
    List<ProductDetail> findByProductId(int productId);

    @Query("SELECT pd FROM product_detail pd WHERE pd.productId = :productId AND pd.sizeId = :sizeId AND pd.colorId = :colorId AND pd.materialId = :materialId")
    Optional<ProductDetail> findByProductAndSizeAndColorAndMaterial(
            @Param("productId") int productId,
            @Param("sizeId") int sizeId,
            @Param("colorId") int colorId,
            @Param("materialId") int materialId);

    Page<ProductDetail> findAll(Pageable pageable);

    List<ProductDetail> findAllByProductId(int productId);

    boolean existsByProductId(int productId);
}
