package com.prj.furni_shop.modules.review.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.review.dto.request.ReviewRequest;
import com.prj.furni_shop.modules.review.dto.response.ReviewResponse;
import com.prj.furni_shop.modules.review.dto.response.ReviewSummaryResponse;
import com.prj.furni_shop.modules.review.service.UserReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Đánh giá", description = "APIs đánh giá")
public class UserReviewController {

    UserReviewService userReviewService;

    @Operation(summary = "Thêm đánh giá", description = "Thêm đánh giá 1 sản phẩm mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/create")
    public ApiResponse<Void> create(
            @RequestBody ReviewRequest request
    ) {
        return ApiResponse.<Void>builder()
                .message(userReviewService.createReview(request))
                .build();
    }

    @Operation(summary = "Lấy tổng quan đánh giá", description = "Lấy tổng quan đánh giá của 1 sản phẩm")
    @GetMapping("/get-review-summary/{productId}")
    public ApiResponse<ReviewSummaryResponse> getReviewSummary(
            @PathVariable int productId
    ) {
        return ApiResponse.<ReviewSummaryResponse>builder()
                .result(userReviewService.getReviewSummary(productId))
                .build();
    }

    @Operation(summary = "Lấy đánh giá theo các tiêu chí", description = "Lấy  đánh giá của 1 sản phẩm  theo các tiêu chí")
    @GetMapping("/get-review/{productId}")
    public ApiResponse<PaginationWrapper<ReviewResponse>> getReview(
            @PathVariable int productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "0") int rate
    ) {
        return ApiResponse.<PaginationWrapper<ReviewResponse>>builder()
                .result(userReviewService.getReview(productId, page, pageSize, rate))
                .build();
    }
    @Operation(summary = "Xóa đánh giá", description = "Xóa đánh giá")
    @DeleteMapping("/delete/{reviewId}")
    public ApiResponse<Void> delete(@PathVariable int reviewId){
        return ApiResponse.<Void>builder()
                .message(userReviewService.deleteReview(reviewId))
                .build();
    }

}