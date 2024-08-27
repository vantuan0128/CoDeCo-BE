package com.prj.furni_shop.modules.category.repository;

import com.prj.furni_shop.modules.category.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    boolean existsByName(String name);

    Page<Material> findAll(Pageable pageable);
}
