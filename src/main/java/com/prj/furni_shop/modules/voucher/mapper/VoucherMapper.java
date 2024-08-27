package com.prj.furni_shop.modules.voucher.mapper;

import com.prj.furni_shop.modules.voucher.dto.request.VoucherRequest;
import com.prj.furni_shop.modules.voucher.dto.response.VoucherResponse;
import com.prj.furni_shop.modules.voucher.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoucherMapper {
    Voucher toVoucher(VoucherRequest request);

    VoucherResponse toVoucherResponse(Voucher voucher);

    @Mapping(target = "voucherId", ignore = true)
    void updateVoucher(@MappingTarget Voucher voucher, VoucherRequest request);
}