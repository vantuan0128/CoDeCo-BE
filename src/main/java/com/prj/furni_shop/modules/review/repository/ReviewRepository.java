package com.prj.furni_shop.modules.review.repository;

import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByProductId(int productId);

    Page<Review> findByProductId(int productId, Pageable pageable);

    Page<Review> findByProductIdAndRating(int productId, int rating, Pageable pageable);

    Page<Review> findByProductIdAndCommentIsNotNullAndCommentIsNotEmpty(int productId, Pageable pageable);
}