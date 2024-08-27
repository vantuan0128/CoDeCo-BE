package com.prj.furni_shop.modules.user.service;

import com.prj.furni_shop.configurations.jwt.JwtService;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.authentication.entity.Token;
import com.prj.furni_shop.modules.authentication.entity.TokenType;
import com.prj.furni_shop.modules.authentication.repository.TokenRepository;
import com.prj.furni_shop.modules.user.dto.request.UpdatePasswordDto;
import com.prj.furni_shop.modules.user.dto.request.UserUpdateDto;
import com.prj.furni_shop.modules.user.dto.response.ChangePasswordResponse;
import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.mapper.UserMapper;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import com.prj.furni_shop.providers.cloudinary.CloudinaryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    CloudinaryService cloudinaryService;
    JwtService jwtService;
    TokenRepository tokenRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${auth.version.division}")
    private int AUTH_VERSION_DIV;

    public UserResponse updateMyInfo(UserUpdateDto req) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        userMapper.updateUser(user, req);
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED)));
    }
  
    @Transactional
    public void deleteMe() {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        userRepository.deleteById(userId);
    }

    public ChangePasswordResponse changePassword(UpdatePasswordDto request, HttpServletResponse response) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getGoogleAccountId() != 0) throw new AppException(ErrorCode.UNAUTHORIZED);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            userRepository.log_auth("changePass", user.getUserId(), user.getEmail(), "Fail", "Password is not correct");
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            userRepository.log_auth("changePass", user.getUserId(), user.getEmail(), "Fail", "Passwords do not match");
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        user.refreshAuthVersion(AUTH_VERSION_DIV);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        tokenRepository.deleteByUserId(user.getUserId());

        var storedToken = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .userId(user.getUserId())
                .build();
        tokenRepository.save(storedToken);

        userRepository.log_auth("changePass", user.getUserId(), user.getEmail(), "Success", "Change successfully");

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ChangePasswordResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String updateAvatar(MultipartFile file) throws IOException {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        String oldAvatarUrl = user.getAvatarUrl();
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
            String publicId = cloudinaryService.extractPublicIdFromUrl(oldAvatarUrl);
            cloudinaryService.deleteFile(publicId);
        }

        String newAvatarUrl = cloudinaryService.uploadFile(file);

        user.setAvatarUrl(newAvatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return newAvatarUrl;
    }

}
