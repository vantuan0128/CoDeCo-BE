package com.prj.furni_shop.modules.review.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.order.repository.OrderItemRepository;
import com.prj.furni_shop.modules.product.repository.ProductRepository;
import com.prj.furni_shop.modules.review.dto.request.ReviewRequest;
import com.prj.furni_shop.modules.review.dto.response.ReviewResponse;
import com.prj.furni_shop.modules.review.dto.response.ReviewSummaryResponse;
import com.prj.furni_shop.modules.review.entity.Review;
import com.prj.furni_shop.modules.review.repository.ReviewRepository;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserReviewService {

    OrderItemRepository orderItemRepository;
    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    public String createReview(ReviewRequest request){
        var orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        var productId = orderItem.getProductDetail().getProductId();

        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());
        if(!userRepository.existsById(userId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        var review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .orderItemId(request.getOrderItemId())
                .userId(userId)
                .productId(productId)
                .build();

        reviewRepository.save(review);
        return "Success";
    }

    public PaginationWrapper<ReviewResponse> getReview(int productId, int page, int pageSize, int rate){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        if(!productRepository.existsById(productId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Page<Review> pageReviews;

        if(rate == 0){
            pageReviews = reviewRepository.findByProductId(productId, pageable);
        } else if (rate > 0 && rate < 6) {
            pageReviews = reviewRepository.findByProductIdAndRating(productId, rate, pageable);
        } else if (rate == 6) {
            pageReviews = reviewRepository.findByProductIdAndCommentIsNotNullAndCommentIsNotEmpty(productId, pageable);
        }else{
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        var reviews = pageReviews.getContent();

        var reviewResponses = reviews.stream().map(review -> {
            var user = review.getUser();
            var productDetail = review.getOrderItem().getProductDetail();

            var userName = user.getFullName();
            var avatar = user.getAvatarUrl();
            var createdAt = review.getCreatedAt();
            var updateAt = review.getUpdateAt();
            var sizeName = productDetail.getSize().getName();
            var colorName = productDetail.getColor().getName();
            var materialName = productDetail.getMaterial().getName();
            var rating = review.getRating();
            var comment = review.getComment();

            return ReviewResponse.builder()
                    .userName(userName)
                    .avatar(avatar)
                    .createdAt(createdAt)
                    .updateAt(updateAt)
                    .sizeName(sizeName)
                    .colorName(colorName)
                    .materialName(materialName)
                    .rating(rating)
                    .comment(comment)
                    .build();
        }).toList();

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(pageReviews.getTotalElements())
                .totalPages((int) Math.ceil((double) pageReviews.getTotalElements() / pageSize))
                .hasNext(pageReviews.hasNext())
                .hasPrevious(pageReviews.hasPrevious())
                .build();

        return new PaginationWrapper<>(reviewResponses, paginationInfo);

    }

    public ReviewSummaryResponse getReviewSummary(int productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        long totalReviews = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        double roundedAverageRating = Math.round(averageRating * 10.0) / 10.0;

        long fiveStarReviews = reviews.stream().filter(review -> review.getRating() == 5).count();
        long fourStarReviews = reviews.stream().filter(review -> review.getRating() == 4).count();
        long threeStarReviews = reviews.stream().filter(review -> review.getRating() == 3).count();
        long twoStarReviews = reviews.stream().filter(review -> review.getRating() == 2).count();
        long oneStarReviews = reviews.stream().filter(review -> review.getRating() == 1).count();
        long commentReviews = reviews.stream().filter(review-> review.getComment() != null && !review.getComment().isEmpty()).count();

        return ReviewSummaryResponse.builder()
                .averageRating(roundedAverageRating)
                .totalReviews(totalReviews)
                .fiveStarReviews(fiveStarReviews)
                .fourStarReviews(fourStarReviews)
                .threeStarReviews(threeStarReviews)
                .twoStarReviews(twoStarReviews)
                .oneStarReviews(oneStarReviews)
                .commentReviews(commentReviews)
                .build();
    }

    public String deleteReview(int reviewId){
        if(!reviewRepository.existsById(reviewId)) throw new AppException(ErrorCode.NOT_EXISTED);
        reviewRepository.deleteById(reviewId);
        return "Success";
    }
}