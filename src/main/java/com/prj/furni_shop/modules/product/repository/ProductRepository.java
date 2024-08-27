package com.prj.furni_shop.modules.product.repository;

import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.product.repository.CustomProductRepo.CustomProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, CustomProductRepo {

    @Procedure(value = "create_product")
    int createProduct(@Param("p_name") String name,
                      @Param("p_description") String description,
                      @Param("p_category_id") int categoryId);

    @Procedure(value = "update_product")
    int updateProduct(@Param("p_product_id") int productId,
                      @Param("p_name") String name,
                      @Param("p_description") String description,
                      @Param("p_category_id") int categoryId);

    @Procedure(value = "delete_product")
    int deleteProduct(@Param("p_product_id") int productId);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByEnable(int enable, Pageable pageable);

    Page<Product> findByEnableAndCategoryIdIn(int enable, List<Integer> categoryIds, Pageable pageable);
}
