package com.prj.furni_shop.modules.category.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.category.dto.request.MaterialDto;
import com.prj.furni_shop.modules.category.dto.response.MaterialResponse;
import com.prj.furni_shop.modules.category.entity.Material;
import com.prj.furni_shop.modules.category.mapper.MaterialMapper;
import com.prj.furni_shop.modules.category.repository.MaterialRepository;
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
public class MaterialService {
    MaterialRepository materialRepository;
    MaterialMapper materialMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public MaterialResponse createMaterial(MaterialDto request) {
        if(materialRepository.existsByName(request.getName())) throw new AppException(ErrorCode.EXISTED);
        Material material = materialMapper.toMaterial(request);
        return materialMapper.toMaterialResponse(materialRepository.save(material));
    }

    public PaginationWrapper<MaterialResponse> getAllMaterials(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Material> materialPages = materialRepository.findAll(pageable);

        List<MaterialResponse> materialResponses = materialPages.getContent().stream()
                .map(materialMapper::toMaterialResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(materialPages.getTotalElements())
                .totalPages((int) Math.ceil((double) materialPages.getTotalElements() / pageSize))
                .hasNext(materialPages.hasNext())
                .hasPrevious(materialPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(materialResponses, paginationInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MaterialResponse updateMaterial(int materialId, MaterialDto request) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        materialMapper.updateMaterial(material, request);
        return materialMapper.toMaterialResponse(materialRepository.save(material));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMaterial(int materialId){
        if(!materialRepository.existsById(materialId)) throw new AppException(ErrorCode.NOT_EXISTED);
        materialRepository.deleteById(materialId);
        return "Material deleted successfully.";
    }
}
