package com.prj.furni_shop.modules.category.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.dto.request.ColorDto;
import com.prj.furni_shop.modules.category.dto.response.ColorResponse;
import com.prj.furni_shop.modules.category.entity.Color;
import com.prj.furni_shop.modules.category.mapper.ColorMapper;
import com.prj.furni_shop.modules.category.repository.ColorRepository;
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
public class ColorService {
    ColorRepository colorRepository;
    ColorMapper colorMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ColorResponse createColor(ColorDto request) {
        if(colorRepository.existsByName(request.getName())) throw new AppException(ErrorCode.EXISTED);
        Color Color = colorMapper.toColor(request);
        return colorMapper.toColorResponse(colorRepository.save(Color));
    }

    public PaginationWrapper<ColorResponse> getAllColors(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Color> colorPages = colorRepository.findAll(pageable);

        List<ColorResponse> colorResponses = colorPages.getContent().stream()
                .map(colorMapper::toColorResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(colorPages.getTotalElements())
                .totalPages((int) Math.ceil((double) colorPages.getTotalElements() / pageSize))
                .hasNext(colorPages.hasNext())
                .hasPrevious(colorPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(colorResponses, paginationInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ColorResponse updateColor(int colorId, ColorDto request) {
        Color Color = colorRepository.findById(colorId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        colorMapper.updateColor(Color, request);
        return colorMapper.toColorResponse(colorRepository.save(Color));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteColor(int colorId){
        if(!colorRepository.existsById(colorId)) throw new AppException(ErrorCode.NOT_EXISTED);
        colorRepository.deleteById(colorId);
        return "Color deleted successfully.";
    }
}
