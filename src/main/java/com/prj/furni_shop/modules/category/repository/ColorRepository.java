package com.prj.furni_shop.modules.category.repository;

import com.prj.furni_shop.modules.category.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    boolean existsByName(String name);

    Page<Color> findAll(Pageable pageable);
}
