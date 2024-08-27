package com.prj.furni_shop.modules.cart.repository;

import com.prj.furni_shop.modules.cart.entity.Cart;
import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByProductDetailAndCart(ProductDetail productDetail, Cart savedCart);

    List<CartItem> findAllByCartIdAndSelected(int cartId, boolean selected);

    @Procedure(value = "remove_cart_item")
    int removeCartItem(@Param("p_cart_item_id") int cartItemId,
                       @Param("p_selected") boolean selected);
}
