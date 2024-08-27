package com.prj.furni_shop.modules.product.dto.response;

import com.prj.furni_shop.modules.category.dto.response.ColorResponse;
import com.prj.furni_shop.modules.category.dto.response.MaterialResponse;
import com.prj.furni_shop.modules.category.dto.response.SizeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableAttributesResponse {

    ProductResponse productResponse;

    List<SizeResponse> sizes;

    List<ColorResponse> colors;

    List<MaterialResponse> materials;
}
