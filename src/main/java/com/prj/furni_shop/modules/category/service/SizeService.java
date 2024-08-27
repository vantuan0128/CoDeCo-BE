package com.prj.furni_shop.modules.category.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.dto.request.SizeDto;
import com.prj.furni_shop.modules.category.dto.response.SizeResponse;
import com.prj.furni_shop.modules.category.entity.Size;
import com.prj.furni_shop.modules.category.mapper.SizeMapper;
import com.prj.furni_shop.modules.category.repository.SizeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SizeService {
    SizeRepository sizeRepository;
    SizeMapper sizeMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public SizeResponse createSize(SizeDto request) {
        if(sizeRepository.existsByName(request.getName())) throw new AppException(ErrorCode.EXISTED);
        Size size = sizeMapper.toSize(request);
        return sizeMapper.toSizeResponse(sizeRepository.save(size));
    }

    public PaginationWrapper<SizeResponse> getAllSizes(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Size> sizePages = sizeRepository.findAll(pageable);

        List<SizeResponse> sizeResponses = sizePages.getContent().stream()
                .map(sizeMapper::toSizeResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(sizePages.getTotalElements())
                .totalPages((int) Math.ceil((double) sizePages.getTotalElements() / pageSize))
                .hasNext(sizePages.hasNext())
                .hasPrevious(sizePages.hasPrevious())
                .build();

        return new PaginationWrapper<>(sizeResponses, paginationInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SizeResponse updateSize(int sizeId, SizeDto request) {
        Size size = sizeRepository.findById(sizeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        sizeMapper.updateSize(size, request);
        return sizeMapper.toSizeResponse(sizeRepository.save(size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSize(int sizeId){
        if(!sizeRepository.existsById(sizeId)) throw new AppException(ErrorCode.NOT_EXISTED);
            sizeRepository.deleteById(sizeId);
            return "Size deleted successfully.";
    }
}
