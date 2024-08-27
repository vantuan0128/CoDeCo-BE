package com.prj.furni_shop.modules.review.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewSummaryResponse {

    double averageRating;

    long totalReviews;

    long fiveStarReviews;

    long fourStarReviews;

    long threeStarReviews;

    long twoStarReviews;

    long oneStarReviews;

    long commentReviews;
}