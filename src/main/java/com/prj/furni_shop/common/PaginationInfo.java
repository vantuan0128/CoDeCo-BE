package com.prj.furni_shop.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationInfo {

    long totalCount;

    int totalPages;

    boolean hasNext;

    boolean hasPrevious;
}
