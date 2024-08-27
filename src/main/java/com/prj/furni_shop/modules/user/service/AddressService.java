package com.prj.furni_shop.modules.user.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.user.dto.request.AddressDto;
import com.prj.furni_shop.modules.user.dto.response.AddressResponse;
import com.prj.furni_shop.modules.user.entity.Address;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.mapper.AddressMapper;
import com.prj.furni_shop.modules.user.repository.AddressRepository;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressRepository addressRepository;
    UserRepository userRepository;
    AddressMapper addressMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PaginationWrapper<AddressResponse> getAddresses(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Address> addressPages = addressRepository.findAll(pageable);

        List<AddressResponse> addressResponses = addressPages.getContent().stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(addressPages.getTotalElements())
                .totalPages((int) Math.ceil((double) addressPages.getTotalElements() / pageSize))
                .hasNext(addressPages.hasNext())
                .hasPrevious(addressPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(addressResponses, paginationInfo);
    }

    public PaginationWrapper<AddressResponse> getMyAddress(int page, int pageSize, String sortBy, String direction) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Address> addressPages = addressRepository.findAllByUserId(user.getUserId(), pageable);

        List<AddressResponse> addressResponses = addressPages.getContent().stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(addressPages.getTotalElements())
                .totalPages((int) Math.ceil((double) addressPages.getTotalElements() / pageSize))
                .hasNext(addressPages.hasNext())
                .hasPrevious(addressPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(addressResponses, paginationInfo);
    }


    public AddressResponse addNewAddress(AddressDto request) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Address address = addressMapper.toAddress(request);
        address.setUserId(user.getUserId());

        return addressMapper.toAddressResponse(addressRepository.save(address));
    }

    public AddressResponse updateAddress(int addressId, AddressDto request) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (address.getUserId() == userId) {
            addressMapper.updateUserAddress(address, request);
            return addressMapper.toAddressResponse(addressRepository.save(address));
        } else throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public String setDefaultAddress(int addressId) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        Address newDefaultAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (newDefaultAddress.getUserId() != userId)
            throw new AppException(ErrorCode.UNAUTHORIZED);

        Address currentDefaultAddress = addressRepository.findByUserIdAndIsDefault(userId, 1);

        if (currentDefaultAddress != null) {
            currentDefaultAddress.setIsDefault(0);
            addressRepository.save(currentDefaultAddress);
        }

        newDefaultAddress.setIsDefault(1);
        addressRepository.save(newDefaultAddress);

        return "Success";
    }

    public String deleteUserAddress(int addressId){
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (address.getUserId() == userId) {
            addressRepository.deleteById(addressId);
            return "Address deleted successfully.";
        } else throw new AppException(ErrorCode.UNAUTHORIZED);
    }

}