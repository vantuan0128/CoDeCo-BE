package com.prj.furni_shop.modules.category.repository;

import com.prj.furni_shop.modules.category.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
    boolean existsByName(String name);

    Page<Size> findAll(Pageable pageable);
}