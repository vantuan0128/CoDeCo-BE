package com.prj.furni_shop.modules.category.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorResponse {
    int colorId;

    String name;

    String colorCode;
}