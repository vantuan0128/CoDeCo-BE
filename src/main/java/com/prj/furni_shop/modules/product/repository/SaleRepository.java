package com.prj.furni_shop.modules.product.repository;

import com.prj.furni_shop.modules.product.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    boolean existsByProductId(int productId);

    Sale findByProductId(int productId);

    Page<Sale> findAll(Pageable pageable);

    void deleteByEndDateBefore(LocalDateTime endDate);
}
