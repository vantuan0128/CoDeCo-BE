package com.prj.furni_shop.modules.product.repository.CustomProductRepo;

import com.prj.furni_shop.modules.product.dto.request.ProductFilterDto;
import com.prj.furni_shop.modules.product.entity.Product;

import java.util.List;

public interface CustomProductRepo {
    List<Product> getFilteredProducts(ProductFilterDto filter);
}
