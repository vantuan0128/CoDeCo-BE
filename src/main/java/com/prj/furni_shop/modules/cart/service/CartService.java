package com.prj.furni_shop.modules.cart.service;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.cart.dto.request.AddToCartRequest;
import com.prj.furni_shop.modules.cart.dto.request.EditCartRequest;
import com.prj.furni_shop.modules.cart.dto.response.CartItemResponse;
import com.prj.furni_shop.modules.cart.dto.response.CartResponse;
import com.prj.furni_shop.modules.cart.entity.Cart;
import com.prj.furni_shop.modules.cart.entity.CartItem;
import com.prj.furni_shop.modules.cart.repository.CartItemRepository;
import com.prj.furni_shop.modules.cart.repository.CartRepository;
import com.prj.furni_shop.modules.product.repository.ProductDetailRepository;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;

    CartItemRepository cartItemRepository;

    ProductDetailRepository productDetailRepository;

    UserRepository userRepository;

    public CartResponse getCart() {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Cart cart = user.getCart();
        if (cart == null || cart.getCartItemList() == null || cart.getCartItemList().isEmpty()) {
            return CartResponse.builder()
                    .totalCount(0)
                    .selectedAll(false)
                    .selectedCount(0)
                    .cartItemResponses(Collections.emptyList())
                    .total(0.0)
                    .build();
        }
        List<CartItem> cartItems = cart.getCartItemList();
        // Cập nhật và lọc các CartItem hết hàng hoặc bị disable
        List<CartItem> updateCartItems = cartItems.stream()
                .peek(cartItem -> {
                    int currentQuantity = cartItem.getProductDetail().getQuantity();
                    if (currentQuantity < cartItem.getCount()) {
                        cartItem.setCount(currentQuantity);
                        cartItemRepository.save(cartItem);
                    }
                })
                .filter(cartItem -> {
                    if (cartItem.getCount() == 0 || cartItem.getProductDetail().getProduct().getEnable()==0) {
                        cartItemRepository.delete(cartItem);
                        return false;
                    }
                    return true;
                })
                .toList();
        int totalCount = 0;
        boolean selectedAll = false;
        int selectedCount = 0;
        double total = 0;
        int countSelect = 0;
        for(CartItem cartItem : updateCartItems){
            var count = cartItem.getCount();
            var price = cartItem.getProductDetail().getPrice();
            totalCount += count;
            if(cartItem.getSelected()){
                selectedCount+=count;
                countSelect++;
                total += (price*count);
            }

        }
        if(countSelect == updateCartItems.size()){
            selectedAll = true;
        }
        List<CartItemResponse> cartItemResponses = updateCartItems.stream()
                .map(cartItem -> {
                    var productDetail = cartItem.getProductDetail();
                    var product = productDetail.getProduct();
                    String imageUrl = product.getProductImages().isEmpty() ? "" : product.getProductImages().get(0).getImageUrl();
                    return CartItemResponse.builder()
                            .cartItemId(cartItem.getCartItemId())
                            .productDetailId(productDetail.getProductDetailId())
                            .image(imageUrl)
                            .productName(product.getName())
                            .colorName(productDetail.getColor().getName())
                            .materialName(productDetail.getMaterial().getName())
                            .sizeName(productDetail.getSize().getName())
                            .price(productDetail.getPrice())
                            .count(cartItem.getCount())
                            .percent(product.getSale() == null ? null : product.getSale().getPercent())
                            .selected(cartItem.getSelected())
                            .build();
                })
                .toList();
        return CartResponse.builder()
                .cartId(cart.getCartId())
                .totalCount(totalCount)
                .selectedAll(selectedAll)
                .selectedCount(selectedCount)
                .total(total)
                .cartItemResponses(cartItemResponses)
                .build();
    }

    public String removeOneCartItem(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        cartItemRepository.delete(cartItem);

        return "Success";
    }

    public String editCartItem(Integer cartItemId, EditCartRequest request) {
        if(request.getCount() < 1){
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (request.getCount() > cartItem.getProductDetail().getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        cartItem.setCount(request.getCount());

        cartItemRepository.save(cartItem);

        return "Success";

    }

    public String selectOneCartItem(Integer cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        cartItem.setSelected(!cartItem.getSelected());
        cartItemRepository.save(cartItem);
        return "Success";
    }

    public String selectAllCartItem() {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Cart cart = user.getCart();
        List<CartItem> cartItems = cart.getCartItemList();

        int countSelect = 0;
        boolean selectedAll;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getSelected()) {
                countSelect++;
            }
        }

        selectedAll = countSelect == cartItems.size();

        cartItems.forEach(cartItem -> cartItem.setSelected(!selectedAll));
        cartItemRepository.saveAll(cartItems);

        return "Success";
    }

    @Transactional
    public String removeCartItems(){
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Cart cart = user.getCart();
        List<CartItem> cartItems = cart.getCartItemList();

        var removeCartItems = cartItems.stream()
                .filter(CartItem::getSelected)
                .toList();
        if (removeCartItems.isEmpty()) {
            throw new AppException(ErrorCode.NOT_EXISTED);
        }
        removeCartItems.forEach(cartItem ->
            {
                int result = cartItemRepository.removeCartItem(cartItem.getCartItemId(), true);
                if (result == -3) {
                    throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                } else if (result == -2) {
                    throw new AppException(ErrorCode.NOT_EXISTED);
                }
            }
        );
        return "Success";
    }

    public String addToCart(AddToCartRequest request) {
        if(request.getCount() < 1){
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }
        var productDetail = productDetailRepository.findById(request.getProductDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (request.getCount() > productDetail.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Cart cart = user.getCart();
        if(cart == null){
            cart = Cart.builder().userId(user.getUserId()).build();
        }

        var savedCart = cartRepository.save(cart);

        var existedCartItem = cartItemRepository.findByProductDetailAndCart(productDetail, savedCart)
                .orElse(null);
        if (existedCartItem == null) {
            var cartItem = CartItem.builder()
                    .productDetailId(productDetail.getProductDetailId())
                    .count(request.getCount())
                    .cartId(savedCart.getCartId())
                    .build();

            cartItemRepository.save(cartItem);
        } else {
            existedCartItem.setCount(existedCartItem.getCount() + request.getCount());
            cartItemRepository.save(existedCartItem);
        }
        return "Success";
    }
}




