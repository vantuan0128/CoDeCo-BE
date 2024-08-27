package com.prj.furni_shop.modules.user.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.user.dto.request.AddressDto;
import com.prj.furni_shop.modules.user.dto.response.AddressResponse;
import com.prj.furni_shop.modules.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Địa chỉ người dùng", description = "APIs địa chỉ người dùng")
public class AddressController {
    AddressService addressService;

    @Operation(summary = "Lấy tất cả địa chỉ của người dùng hiện tại", description = "Lấy danh sách tất cả các địa chỉ")
    @GetMapping()
    public ApiResponse<PaginationWrapper<AddressResponse>> getAddresses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "addressId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<AddressResponse>>builder()
                .result(addressService.getAddresses(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Lấy địa chỉ của tôi", description = "Lấy danh sách các địa chỉ của người dùng hiện tại")
    @GetMapping("/myAddress/me")
    public ApiResponse<PaginationWrapper<AddressResponse>> getMyAddress(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "addressId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<AddressResponse>>builder()
                .result(addressService.getMyAddress(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Thêm địa chỉ mới", description = "Thêm một địa chỉ mới cho người dùng")
    @PostMapping()
    public ApiResponse<AddressResponse> addNewAddress(
            @Valid
            @RequestBody AddressDto request
    ) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.addNewAddress(request))
                .build();
    }

    @Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật thông tin của một địa chỉ cụ thể theo ID")
    @PutMapping("/update-address/{addressId}")
    public ApiResponse<AddressResponse> updateAddress(
            @Valid
            @RequestBody AddressDto request,
            @PathVariable int addressId
    ) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(addressId, request))
                .build();
    }

    @Operation(summary = "Đặt địa chỉ mặc định", description = "Đặt một địa chỉ cụ thể làm địa chỉ mặc định theo ID")
    @PutMapping("/set-default-address/{addressId}")
    public ApiResponse<String> setDefaultAddress(
            @PathVariable int addressId
    ) {
        return ApiResponse.<String>builder()
                .result(addressService.setDefaultAddress(addressId))
                .build();
    }

    @Operation(summary = "Xóa địa chỉ người dùng", description = "Xóa một địa chỉ của người dùng theo ID")
    @DeleteMapping("/delete-user-address/{addressId}")
    public ApiResponse<String> deleteUserAddress(
            @PathVariable int addressId
    ){
        return ApiResponse.<String>builder()
                .result(addressService.deleteUserAddress(addressId))
                .build();
    }
}
