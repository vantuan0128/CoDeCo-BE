package com.prj.furni_shop.modules.product.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.entity.Color;
import com.prj.furni_shop.modules.category.entity.Material;
import com.prj.furni_shop.modules.category.entity.Size;
import com.prj.furni_shop.modules.category.repository.ColorRepository;
import com.prj.furni_shop.modules.category.repository.MaterialRepository;
import com.prj.furni_shop.modules.category.repository.SizeRepository;
import com.prj.furni_shop.modules.product.dto.request.ProductDetailDto;
import com.prj.furni_shop.modules.product.dto.request.ProductDto;
import com.prj.furni_shop.modules.product.dto.request.ProductImageUploadDto;
import com.prj.furni_shop.modules.product.dto.response.ImageResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductDetailResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductImageResonse;
import com.prj.furni_shop.modules.product.dto.response.ProductResponse;
import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import com.prj.furni_shop.modules.product.entity.ProductImage;
import com.prj.furni_shop.modules.product.mapper.ProductDetailMapper;
import com.prj.furni_shop.modules.product.mapper.ProductMapper;
import com.prj.furni_shop.modules.product.repository.ProductDetailRepository;
import com.prj.furni_shop.modules.product.repository.ProductImageRepository;
import com.prj.furni_shop.modules.product.repository.ProductRepository;
import com.prj.furni_shop.providers.cloudinary.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    ProductDetailRepository productDetailRepository;
    ProductDetailMapper productDetailMapper;

    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    MaterialRepository materialRepository;
    ProductImageRepository productImageRepository;

    CloudinaryService cloudinaryService;

    public PaginationWrapper<ProductResponse> getAllProductsForAdmin(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> productPages = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = productPages.getContent().stream()
                .map(product -> {
                    ProductResponse response = productMapper.toProductResponse(product);
                    response.setProductImages(getProductImagesLink(product.getProductId()));
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

    public List<String> getProductImagesLink(int productId) {
        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
        List<String> productImagesLink = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        return productImages.isEmpty() ? null : productImagesLink;
    }

    public ProductResponse createProduct(ProductDto request) {
        int result = productRepository.createProduct(request.getName(), request.getDescription(), request.getCategoryId());

        if (result == -3) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        } else if (result == -2) {
            // Product existed
            throw new AppException(ErrorCode.EXISTED);
        } else if (result == -1) {
            // Category not existed
            throw new AppException(ErrorCode.NOT_EXISTED);
        } else {
            Product product = productRepository.findById(result)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            return productMapper.toProductResponse(product);
        }
    }

    @Transactional
    public void uploadImages(ProductImageUploadDto request) throws IOException {
        int productId = request.getProductId();
        List<MultipartFile> images = request.getImages();

        if(!productRepository.existsById(productId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        if (images == null || images.isEmpty()) {
            return;
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                continue;
            }

            String imageUrl = cloudinaryService.uploadFile(image);

            var productImage = ProductImage.builder()
                    .productId(productId)
                    .imageUrl(imageUrl)
                    .build();

            productImageRepository.save(productImage);
        }
    }

    @Transactional
    public String deleteProductImage(int productImageId) throws IOException {
        var productImage = productImageRepository.findById(productImageId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        String publicId = cloudinaryService.extractPublicIdFromUrl(productImage.getImageUrl());

        cloudinaryService.deleteFile(publicId);

        productImageRepository.deleteById(productImageId);

        return "Delete successfully";
    }

    public ProductImageResonse getProductImagesByProductId(int productId) {
        if(!productRepository.existsById(productId)) throw new AppException(ErrorCode.NOT_EXISTED);

        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
        List<ImageResponse> imageUrls = productImages.stream()
                .map(productImage ->
                     ImageResponse.builder()
                            .productImageId(productImage.getProductImageId())
                            .imageUrl(productImage.getImageUrl())
                            .build()
                )
                .collect(Collectors.toList());

        return ProductImageResonse.builder()
                .productId(productId)
                .imageResponses(imageUrls)
                .build();
    }

    public ProductResponse updateProduct(int productId, ProductDto request) {
        int result = productRepository.updateProduct(productId, request.getName(), request.getDescription(), request.getCategoryId());

        if (result == -3) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        } else if (result == -2) {
            // Product not existed
            throw new AppException(ErrorCode.NOT_EXISTED);
        } else if (result == -1) {
            // Category not existed
            throw new AppException(ErrorCode.NOT_EXISTED);
        } else {
            Product product = productRepository.findById(result)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            return productMapper.toProductResponse(product);
        }
    }

    public String deleteProduct(int productId) {
        int result = productRepository.deleteProduct(productId);

        if (result == -2) {
            throw new AppException(ErrorCode.PRODUCT_HAS_PRODUCTDETAILS);
        } else if (result == -1) {
            throw new AppException(ErrorCode.NOT_EXISTED);
        } else {
            return "Product deleted successfully";
        }
    }

    public String disableProduct(int productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        product.setEnable(product.getEnable() == 1 ? 0 : 1);
        productRepository.save(product);
        return "Success";
    }

    public PaginationWrapper<ProductDetailResponse> getAllProductDetails(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<ProductDetail> productDetailPages = productDetailRepository.findAll(pageable);

        List<ProductDetailResponse> productDetailResponses = productDetailPages.getContent().stream()
                .map(productDetail ->
                    ProductDetailResponse.builder()
                            .productDetailId(productDetail.getProductDetailId())
                            .productId(productDetail.getProductId())
                            .price(productDetail.getPrice())
                            .quantity(productDetail.getQuantity())
                            .sizeName(productDetail.getSize().getName())
                            .colorName(productDetail.getColor().getName())
                            .materialName(productDetail.getMaterial().getName())
                            .build()
                )
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(productDetailPages.getTotalElements())
                .totalPages((int) Math.ceil((double) productDetailPages.getTotalElements() / pageSize))
                .hasNext(productDetailPages.hasNext())
                .hasPrevious(productDetailPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(productDetailResponses, paginationInfo);
    }

    public ProductDetailResponse createProductDetail(ProductDetailDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (request.getPrice() <= 0|| request.getQuantity() < 0) throw new AppException(ErrorCode.INVALID_INPUT_DATA);

        Optional<ProductDetail> existingProductDetail = productDetailRepository.findByProductAndSizeAndColorAndMaterial(
                request.getProductId(), request.getSizeId(), request.getColorId(), request.getMaterialId());

        if (existingProductDetail.isPresent()) {
            throw new AppException(ErrorCode.EXISTED);
        }

        ProductDetail productDetail = productDetailMapper.toProductDetail(request);
        productDetailRepository.save(productDetail);

        updateProductPriceRange(productDetail.getProductId());

        return ProductDetailResponse.builder()
                .productDetailId(productDetail.getProductDetailId())
                .productId(product.getProductId())
                .price(productDetail.getPrice())
                .quantity(productDetail.getQuantity())
                .sizeName(size.getName())
                .colorName(color.getName())
                .materialName(material.getName())
                .build();
    }

    private void updateProductPriceRange(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<ProductDetail> productDetails = product.getProductDetails();

        if (productDetails.isEmpty()) {
            product.setMinPrice(null);
            product.setMaxPrice(null);
        } else {
            IntSummaryStatistics priceStats = productDetails.stream()
                    .mapToInt(ProductDetail::getPrice)
                    .summaryStatistics();
            product.setMinPrice(priceStats.getMin());
            product.setMaxPrice(priceStats.getMax());
        }

        productRepository.save(product);
    }

    public String updateProductDetail(int productDetailId, ProductDetailDto request) {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (!productRepository.existsById(request.getProductId()) || !colorRepository.existsById(request.getColorId())
                || !sizeRepository.existsById(request.getSizeId()) || !materialRepository.existsById(request.getMaterialId()))
            throw new AppException(ErrorCode.NOT_EXISTED);

        Optional<ProductDetail> existingProductDetail = productDetailRepository.findByProductAndSizeAndColorAndMaterial(
                request.getProductId(), request.getSizeId(), request.getColorId(), request.getMaterialId());

        if (existingProductDetail.isPresent()) {
            throw new AppException(ErrorCode.EXISTED);
        }

        if (request.getPrice() <= 0|| request.getQuantity() < 0) throw new AppException(ErrorCode.INVALID_INPUT_DATA);

        productDetailMapper.updateProductDetail(productDetail, request);
        productDetailRepository.save(productDetail);

        updateProductPriceRange(productDetail.getProductId());

        return "Success";
    }

    public List<ProductDetailResponse> getAllProductDetailsByProductId(int productId) {
        if(!productRepository.existsById(productId))
                throw new AppException(ErrorCode.NOT_EXISTED);
        List<ProductDetail> productDetailList = productDetailRepository.findAllByProductId(productId);

        return productDetailList.stream()
                .map(productDetail ->
                     ProductDetailResponse.builder()
                            .productDetailId(productDetail.getProductDetailId())
                            .productId(productDetail.getProductId())
                            .price(productDetail.getPrice())
                            .quantity(productDetail.getQuantity())
                            .sizeName(productDetail.getSize().getName())
                            .colorName(productDetail.getColor().getName())
                            .materialName(productDetail.getMaterial().getName())
                            .build()
                )
                .collect(Collectors.toList());
    }

    public String deleteProductDetail(int productDetailId) {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        int productId = productDetail.getProductId();

        productDetailRepository.delete(productDetail);

        updateProductPriceRange(productDetail.getProductId());

        boolean hasOtherDetails = productDetailRepository.existsByProductId(productId);
        if (!hasOtherDetails) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            product.setEnable(0);
            productRepository.save(product);
        }

        return "Deleted successfully";
    }

}
