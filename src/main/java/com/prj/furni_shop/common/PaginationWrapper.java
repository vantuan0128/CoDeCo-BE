package com.prj.furni_shop.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationWrapper<T> {
    List<T> data;
    PaginationInfo pagination;
}
