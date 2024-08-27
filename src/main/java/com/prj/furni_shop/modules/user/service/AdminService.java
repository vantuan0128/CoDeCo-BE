package com.prj.furni_shop.modules.user.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.order.repository.OrderRepository;
import com.prj.furni_shop.modules.user.dto.request.UserCreationDto;
import com.prj.furni_shop.modules.user.dto.request.UserUpdateDto;
import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.enums.Status;
import com.prj.furni_shop.modules.user.mapper.UserMapper;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {
    UserRepository userRepository;
    OrderRepository orderRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationDto request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EXISTED);

        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(Status.ACTIVE);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(int userId, UserUpdateDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        userMapper.updateUser(user, req);
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public PaginationWrapper<UserResponse> getUsers(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<User> userPages = userRepository.findAll(pageable);

        List<UserResponse> userResponses = userPages.getContent().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(userPages.getTotalElements())
                .totalPages((int) Math.ceil((double) userPages.getTotalElements() / pageSize))
                .hasNext(userPages.hasNext())
                .hasPrevious(userPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(userResponses, paginationInfo);
    }

    public UserResponse getUser(int userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED)));
    }

    public String deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        userRepository.deleteById(userId);

        return "User has been deleted";
    }

    public String banUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        String message;

        if (user.getIsActive() == Status.ACTIVE) {
            user.setIsActive(Status.BANNED);
            message = "User has been banned";
        } else {
            user.setIsActive(Status.ACTIVE);
            message = "User has been unlocked";
        }

        userRepository.save(user);

        return message;
    }

    @Transactional
    public void bulkDeleteUser(List<Integer> userIds) {
        for (int userId : userIds) {
            userRepository.deleteById(userId);
        }
    }

    public Map<String,Long> statsUser() {
        Map<String,Long> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalPendingUsers", userRepository.countByIsActive(Status.PENDING));
        stats.put("totalActiveUsers", userRepository.countByIsActive(Status.ACTIVE));
        stats.put("totalBannedUsers", userRepository.countByIsActive(Status.BANNED));

        return stats;
    }



}
