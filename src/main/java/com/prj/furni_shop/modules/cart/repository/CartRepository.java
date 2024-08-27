package com.prj.furni_shop.modules.cart.repository;

import com.prj.furni_shop.modules.cart.entity.Cart;
import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    Optional<Cart> findByUserId(int userId);

}
