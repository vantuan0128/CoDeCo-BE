package com.prj.furni_shop.modules.cart.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.cart.dto.request.AddToCartRequest;
import com.prj.furni_shop.modules.cart.dto.request.EditCartRequest;
import com.prj.furni_shop.modules.cart.dto.response.CartItemResponse;
import com.prj.furni_shop.modules.cart.dto.response.CartResponse;
import com.prj.furni_shop.modules.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Giỏ hàng", description = "APIs liên quan đến giỏ hàng")
public class CartController {

    CartService cartService;

    @Operation(summary = "Thêm vào giỏ hàng", description = "Thêm vào giỏ hàng")
    @PostMapping("/add-to-cart")
    public ApiResponse<Void> addToCart(
            @RequestBody AddToCartRequest request
    ) {
        return ApiResponse.<Void>builder()
                .message(cartService.addToCart(request))
                .build();
    }

    @Operation(summary = "Xem giỏ hàng", description = "Xem giỏ hàng")
    @GetMapping("/show-cart")
    public ApiResponse<CartResponse> showCart() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart())
                .build();
    }

    @Operation(summary = "Xóa 1 item trong cart", description = "Xóa 1 item trong cart")
    @DeleteMapping("/remove-item/{cartItemId}")
    public ApiResponse<Void> removeItem(
            @PathVariable int cartItemId
    ){
        return ApiResponse.<Void>builder()
                .message(cartService.removeOneCartItem(cartItemId))
                .build();
    }

    @Operation(summary = "Sửa số lượng cart item", description = "Sửa số lượng cart item")
    @PutMapping("/edit-cart/{cartItemId}")
    public ApiResponse<Void> editCart(
            @PathVariable Integer cartItemId,
            @RequestBody EditCartRequest request
    ) {
        return ApiResponse.<Void>builder()
                .message(cartService.editCartItem(cartItemId, request))
                .build();
    }

    @Operation(summary = "Select 1 item trong cart", description = "Select 1 item trong cart")
    @PutMapping("/select-item/{cartItemId}")
    public ApiResponse<Void> selectItem(
            @PathVariable Integer cartItemId
    ) {
        return ApiResponse.<Void>builder()
                .message(cartService.selectOneCartItem(cartItemId))
                .build();
    }

    @Operation(summary = "Select all item trong cart", description = "Select all item trong cart")
    @PutMapping("/select-all")
    public ApiResponse<Void> selectAllItem() {
        return ApiResponse.<Void>builder()
                .message(cartService.selectAllCartItem())
                .build();
    }

    @Operation(summary = "Xóa các item đang select trong cart", description = "Xóa các item đang select trong cart")
    @DeleteMapping("/remove-items")
    public ApiResponse<Void> removeItems(){
        return ApiResponse.<Void>builder()
                .message(cartService.removeCartItems())
                .build();
    }
}
