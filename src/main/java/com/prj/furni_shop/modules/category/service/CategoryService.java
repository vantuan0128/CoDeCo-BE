package com.prj.furni_shop.modules.category.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.dto.request.CategoryDto;
import com.prj.furni_shop.modules.category.dto.response.CategoryResponse;
import com.prj.furni_shop.modules.category.dto.response.SubCategoryResponse;
import com.prj.furni_shop.modules.category.entity.Category;
import com.prj.furni_shop.modules.category.mapper.CategoryMapper;
import com.prj.furni_shop.modules.category.repository.CategoryRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryDto request) {
        if(categoryRepository.existsByName(request.getName())) throw new AppException(ErrorCode.EXISTED);
        Category category = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public PaginationWrapper<CategoryResponse> getAllCategories(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Category> categoryPages = categoryRepository.findAll(pageable);

        List<CategoryResponse> categoryResponses = categoryPages.getContent().stream()
                .map(category -> {
                    List<Category> subCategories = categoryRepository.findByParentId(category.getCategoryId());

                    List<SubCategoryResponse> subCategoryResponses = subCategories.stream()
                            .map(subCategory -> new SubCategoryResponse(subCategory.getCategoryId(), subCategory.getName(), subCategory.getParentId()))
                            .collect(Collectors.toList());

                    CategoryResponse categoryResponse = CategoryResponse.builder()
                            .categoryId(category.getCategoryId())
                            .name(category.getName())
                            .subCategoriesInfo(subCategoryResponses)
                            .build();
                    return categoryResponse;
                })
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(categoryPages.getTotalElements())
                .totalPages((int) Math.ceil((double) categoryPages.getTotalElements() / pageSize))
                .hasNext(categoryPages.hasNext())
                .hasPrevious(categoryPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(categoryResponses, paginationInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(int categoryId, CategoryDto request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        categoryMapper.updateCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteCategory(int categoryId){
        if(!categoryRepository.existsById(categoryId)) throw new AppException(ErrorCode.NOT_EXISTED);

        List<Category> subCategories = categoryRepository.findByParentId(Integer.valueOf(categoryId));

        if (!subCategories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_SUBCATEGORIES);
        }

        categoryRepository.deleteById(categoryId);
        return "Category deleted successfully.";
    }
}
