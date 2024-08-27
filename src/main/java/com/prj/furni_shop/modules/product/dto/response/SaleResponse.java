package com.prj.furni_shop.modules.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleResponse {
    int saleId;

    double percent;

    LocalDateTime startDate;

    LocalDateTime endDate;

    int productId;
}
