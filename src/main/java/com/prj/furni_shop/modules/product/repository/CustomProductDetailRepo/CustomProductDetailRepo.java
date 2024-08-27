package com.prj.furni_shop.modules.product.repository.CustomProductDetailRepo;

import com.prj.furni_shop.modules.product.dto.response.ProductDetailInfoResponse;

public interface CustomProductDetailRepo {
   ProductDetailInfoResponse getTotalQuantityAndPrice(Integer productId, Integer sizeId, Integer colorId, Integer materialId);
}
