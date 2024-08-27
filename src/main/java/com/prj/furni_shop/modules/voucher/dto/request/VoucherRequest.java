package com.prj.furni_shop.modules.voucher.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {
    String code;

    String title;

    Double discountPercent;

    Double minValueOrder;

    Double maxValueDiscount;

    Integer quantity;

    String description;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime startDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime endDate;
}