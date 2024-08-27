package com.prj.furni_shop.modules.category.repository;

import com.prj.furni_shop.modules.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    List<Category> findByParentId(Integer parentId);

    Page<Category> findAll(Pageable pageable);
}
