package com.prj.furni_shop.modules.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    int productId;

    String name;

    String description;

    int soldCount;

    int enable;

    int categoryId;

    List<String> productImages;

    Double percent;

    Integer minPrice;

    Integer maxPrice;

    double averageRating;

    LocalDateTime createdAt;

    LocalDateTime updateAt;
}
