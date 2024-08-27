package com.prj.furni_shop.modules.product.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleDto {
    double percent;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime startDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime endDate;

    int productId;
}
