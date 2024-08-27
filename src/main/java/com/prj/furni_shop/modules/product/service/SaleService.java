package com.prj.furni_shop.modules.product.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.product.dto.request.SaleDto;
import com.prj.furni_shop.modules.product.dto.response.SaleResponse;
import com.prj.furni_shop.modules.product.entity.Sale;
import com.prj.furni_shop.modules.product.mapper.SaleMapper;
import com.prj.furni_shop.modules.product.repository.ProductRepository;
import com.prj.furni_shop.modules.product.repository.SaleRepository;
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
@PreAuthorize("hasRole('ADMIN')")
public class SaleService {

    SaleMapper saleMapper;
    SaleRepository saleRepository;
    ProductRepository productRepository;

    public PaginationWrapper<SaleResponse> getAllSales(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Sale> salePages = saleRepository.findAll(pageable);

        List<SaleResponse> saleResponses = salePages.getContent().stream()
                .map(saleMapper::toSaleResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(salePages.getTotalElements())
                .totalPages((int) Math.ceil((double) salePages.getTotalElements() / pageSize))
                .hasNext(salePages.hasNext())
                .hasPrevious(salePages.hasPrevious())
                .build();

        return new PaginationWrapper<>(saleResponses, paginationInfo);
    }

    public SaleResponse getOneSale(int saleId) {
        var sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        return saleMapper.toSaleResponse(sale);
    }

    public SaleResponse createSale(SaleDto request) {
        if(request.getPercent() <= 0 || request.getPercent() >= 100 || request.getStartDate().isAfter(request.getEndDate()))
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);

        if(!productRepository.existsById(request.getProductId()))
            throw new AppException(ErrorCode.NOT_EXISTED);

        if(saleRepository.existsByProductId(request.getProductId()))
            throw new AppException(ErrorCode.EXISTED);

        Sale sale = saleMapper.toSale(request);
        sale.setProductId(request.getProductId());

        return saleMapper.toSaleResponse(saleRepository.save(sale));
    }

    public SaleResponse updateSale(int saleId, SaleDto request) {
        if(request.getPercent() <= 0 || request.getPercent() >= 100 || request.getStartDate().isAfter(request.getEndDate()))
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);

        if(!productRepository.existsById(request.getProductId()))
            throw new AppException(ErrorCode.NOT_EXISTED);

        var sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if(saleRepository.existsByProductId(request.getProductId())
                && sale.getProductId() != request.getProductId())
            throw new AppException(ErrorCode.EXISTED);

        saleMapper.updateSale(sale, request);

        return saleMapper.toSaleResponse(saleRepository.save(sale));
    }

    public String deleteSale(int saleId) {
        if(!saleRepository.existsById(saleId))
            throw new AppException(ErrorCode.NOT_EXISTED);

        saleRepository.deleteById(saleId);

        return "Delete successfuly";
    }

}
