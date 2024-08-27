package com.prj.furni_shop.modules.product.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.dto.response.ColorResponse;
import com.prj.furni_shop.modules.category.dto.response.MaterialResponse;
import com.prj.furni_shop.modules.category.dto.response.SizeResponse;
import com.prj.furni_shop.modules.category.entity.Category;
import com.prj.furni_shop.modules.category.repository.CategoryRepository;
import com.prj.furni_shop.modules.product.dto.request.PagingDto;
import com.prj.furni_shop.modules.product.dto.request.ProductDetailFilterDto;
import com.prj.furni_shop.modules.product.dto.request.ProductFilterDto;
import com.prj.furni_shop.modules.product.dto.response.AvailableAttributesResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductDetailInfoResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductResponse;
import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import com.prj.furni_shop.modules.product.entity.ProductImage;
import com.prj.furni_shop.modules.product.mapper.ProductMapper;
import com.prj.furni_shop.modules.product.repository.ProductDetailRepository;
import com.prj.furni_shop.modules.product.repository.ProductImageRepository;
import com.prj.furni_shop.modules.product.repository.ProductRepository;
import com.prj.furni_shop.modules.product.repository.SaleRepository;
import com.prj.furni_shop.modules.review.entity.Review;
import com.prj.furni_shop.modules.review.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProductService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductDetailRepository productDetailRepository;
    ProductImageRepository productImageRepository;
    SaleRepository saleRepository;
    ReviewRepository reviewRepository;

    public PaginationWrapper<ProductResponse> getActiveProductsForUser(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Product> productPages = productRepository.findByEnable(1, pageable);

        List<ProductResponse> productResponses = productPages.getContent().stream()
            .map(product -> {
                ProductResponse response = productMapper.toProductResponse(product);
                response.setProductImages(getProductImagesLink(product.getProductId()));
                response.setPercent(getProductSale(product.getProductId()));
                response.setAverageRating(getAverageRating(product.getProductId()));
                return response;
            })
            .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
            .totalCount(productPages.getTotalElements())
            .totalPages((int) Math.ceil((double) productPages.getTotalElements() / pageSize))
            .hasNext(productPages.hasNext())
            .hasPrevious(productPages.hasPrevious())
            .build();

        return new PaginationWrapper<>(productResponses, paginationInfo);
    }

    public PaginationWrapper<ProductResponse> getActiveProductsForUserByCategoryId(int categoryId, int page, int pageSize, String sortBy, String direction) {

        if(!categoryRepository.existsById(categoryId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<Integer> categoryIds = new ArrayList<>();

        if (category.getParentId() != null) {
            categoryIds.add(categoryId);
        } else {
            List<Category> subCategories = categoryRepository.findByParentId(category.getCategoryId());
            categoryIds = subCategories.stream()
                            .map(Category::getCategoryId)
                            .collect(Collectors.toList());

            categoryIds.add(category.getCategoryId());
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> productPages = productRepository.findByEnableAndCategoryIdIn(1, categoryIds, pageable);

        List<ProductResponse> productResponses = productPages.getContent().stream()
                .map(product -> {
                    ProductResponse response = productMapper.toProductResponse(product);
                    response.setProductImages(getProductImagesLink(product.getProductId()));
                    response.setPercent(getProductSale(product.getProductId()));
                    response.setAverageRating(getAverageRating(product.getProductId()));
                    return response;
                })
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(productPages.getTotalElements())
                .totalPages((int) Math.ceil((double) productPages.getTotalElements() / pageSize))
                .hasNext(productPages.hasNext())
                .hasPrevious(productPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(productResponses, paginationInfo);
    }

    public PaginationWrapper<ProductResponse> filterProducts(ProductFilterDto request, PagingDto paging) {
        List<Product> products = productRepository.getFilteredProducts(request);
        List<Product> pagedProducts = getPagedProducts(products, paging);

        List<ProductResponse> productResponses = pagedProducts.stream()
                .map(product -> {
                    ProductResponse response = productMapper.toProductResponse(product);
                    response.setProductImages(getProductImagesLink(product.getProductId()));
                    response.setPercent(getProductSale(product.getProductId()));
                    response.setAverageRating(getAverageRating(product.getProductId()));
                    return response;
                })
                .collect(Collectors.toList());

        long totalCount = products.size();
        int totalPages = (int) Math.ceil((double) totalCount / paging.getPageSize());
        boolean hasNext = paging.getPage() < totalPages;
        boolean hasPrevious = paging.getPage() > 1;

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();

        return new PaginationWrapper<>(productResponses, paginationInfo);
    }

    private List<Product> getPagedProducts(List<Product> products, PagingDto paging) {
        if (paging.getPage() != null && paging.getPageSize() != null) {
            int page = paging.getPage();
            int pageSize = paging.getPageSize();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, products.size());

            if (start >= products.size()) {
                return new ArrayList<>();
            }
            return products.subList(start, end);
        }
        return products;
    }

    private Double getAverageRating(int productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return Math.round(averageRating * 10.0) / 10.0;
    }

    private List<String> getProductImagesLink(int productId) {
        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
        List<String> productImagesLink = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        return productImages.isEmpty() ? null : productImagesLink;
    }

    private Double getProductSale(int productId) {
        return saleRepository.existsByProductId(productId) ? saleRepository.findByProductId(productId).getPercent() : null;
    }

    public AvailableAttributesResponse getProductDetailsByProductId(int productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        ProductResponse productResponse =  productMapper.toProductResponse(product);
        productResponse.setProductImages(getProductImagesLink(productId));
        productResponse.setPercent(getProductSale(productId));

        List<ProductDetail> productDetails = productDetailRepository.findByProductId(productId);
        if (productDetails.isEmpty()) throw new AppException(ErrorCode.NOT_EXISTED);

        Set<SizeResponse> sizes = productDetails.stream()
                .map(productDetail -> new SizeResponse(productDetail.getSizeId(),
                        productDetail.getSize().getName()))
                .collect(Collectors.toSet());

        Set<ColorResponse> colors = productDetails.stream()
                .map(productDetail -> new ColorResponse(productDetail.getColorId(),
                        productDetail.getColor().getName(),
                        productDetail.getColor().getColorCode()))
                .collect(Collectors.toSet());

        Set<MaterialResponse> materials = productDetails.stream()
                .map(productDetail -> new MaterialResponse(productDetail.getMaterialId(),
                        productDetail.getMaterial().getName()))
                .collect(Collectors.toSet());

        return new AvailableAttributesResponse(productResponse, List.copyOf(sizes), List.copyOf(colors), List.copyOf(materials));
    }

    public ProductDetailInfoResponse getTotalQuantityAndPrice(ProductDetailFilterDto request) {
        return productDetailRepository.getTotalQuantityAndPrice(
                request.getProductId(), request.getSizeId(), request.getColorId(), request.getMaterialId()
        );
    }
}
