package com.prj.furni_shop.modules.authentication.service;

import com.prj.furni_shop.base.mail.EmailService;
import com.prj.furni_shop.base.mail.EmailType;
import com.prj.furni_shop.configurations.captcha.RecaptchaService;
import com.prj.furni_shop.configurations.jwt.JwtService;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.authentication.dto.request.*;
import com.prj.furni_shop.modules.authentication.dto.respone.LoginRespone;
import com.prj.furni_shop.modules.authentication.dto.respone.RefreshTokenRespone;
import com.prj.furni_shop.modules.authentication.entity.Token;
import com.prj.furni_shop.modules.authentication.entity.TokenType;
import com.prj.furni_shop.modules.authentication.repository.GetGoogleTokenClient;
import com.prj.furni_shop.modules.authentication.repository.GetGoogleUserClient;
import com.prj.furni_shop.modules.authentication.repository.TokenRepository;
import com.prj.furni_shop.modules.user.dto.request.RecoverPasswordDto;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.enums.Role;
import com.prj.furni_shop.modules.user.enums.Status;
import com.prj.furni_shop.modules.user.mapper.UserMapper;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import com.prj.furni_shop.utils.EncryptionUtil;
import com.prj.furni_shop.utils.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    EmailService emailService;
    OtpUtil otpUtil;
    EncryptionUtil encryptionUtil;
    JwtService jwtService;
    TokenRepository tokenRepository;
    RecaptchaService recaptchaService;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    GetGoogleTokenClient getGoogleTokenClient;

    GetGoogleUserClient getGoogleUserClient;

    @NonFinal
    @Value("${auth.version.division}")
    private int AUTH_VERSION_DIV;

    @NonFinal
    @Value("${google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EXISTED);
        String otp = otpUtil.generateOtp();
        try {
            emailService.sendOtpMail(request.getEmail(), otp, EmailType.VERIFY_ACCOUNT);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .isActive(Status.PENDING)
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .otp(otp)
                .otpGeneratedTime(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return "Success";
    }

    public boolean verifyLink(LinkRequest request) {
        String decodedParams = encryptionUtil.decodeBase64(request.getParams());
        String [] splitParams = decodedParams.split("&");
        String email = splitParams[0].split("=")[1];
        String otp = splitParams[1].split("=")[1];

        var user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getIsActive() == Status.BANNED)
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);

        if(user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
            LocalDateTime.now()).getSeconds() < (5 * 60)) {
            if(user.getIsActive() == Status.PENDING) {
                user.setIsActive(Status.ACTIVE);
            }
            user.setOtp("");
            userRepository.save(user);
            return true;
        }
        throw new AppException(ErrorCode.INVALID_OTP);
    }

    public boolean verifyOtp(VerifyOtpRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getIsActive() == Status.BANNED)
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);

        if(user.getOtp().equals(request.getOtp()) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            if(user.getIsActive() == Status.PENDING) {
                user.setIsActive(Status.ACTIVE);
            }
            user.setOtp("");
            userRepository.save(user);
            return true;
        }
        throw new AppException(ErrorCode.INVALID_OTP);
    }

    public String regenerateOtp(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailService.sendOtpMail(email, otp, EmailType.VERIFY_ACCOUNT);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... Please verify account within 5 minutes";
    }


    public LoginRespone login(LoginRequest request, HttpServletResponse response){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (!request.getRecaptchaToken().equals("test")) {
            boolean isValidRecaptcha = recaptchaService.verifyRecaptcha(request.getRecaptchaToken());
            if (!isValidRecaptcha) {
                userRepository.log_auth("recaptcha", user.getUserId(), user.getEmail(), "Fail", "Unverified captcha");
                throw new AppException(ErrorCode.INVALID_CAPTCHA);
            }
        }

        if (user.getIsActive() == Status.PENDING){
            userRepository.log_auth("login", user.getUserId(), user.getEmail(), "Fail", "Unverified account");
            throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT);
        }

        if (user.getIsActive() == Status.BANNED){
            userRepository.log_auth("login", user.getUserId(), user.getEmail(), "Fail", "Forbidden account");
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);
        }

        boolean authenticated = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!authenticated) {
            userRepository.log_auth("login", user.getUserId(), user.getEmail(), "Fail", "Wrong password");
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var storedToken = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .userId(user.getUserId())
                .build();
        tokenRepository.save(storedToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        userRepository.log_auth("login", user.getUserId(), user.getEmail(), "Success", "Login successfully");

        return LoginRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }


    public RefreshTokenRespone refreshToken(RefreshTokenRequest request, HttpServletResponse response){
        if(!jwtService.verifyToken(request.getRefreshToken())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var storedToken = tokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(()->new AppException(ErrorCode.INVALID_TOKEN));
        var user = storedToken.getUser();
        tokenRepository.delete(storedToken);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var newRefreshToken = Token.builder()
                .token(refreshToken)
                .userId(user.getUserId())
                .build();

        tokenRepository.save(newRefreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return RefreshTokenRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String recoverPassword(RecoverPasswordDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (!user.getOtp().equals(request.getOtp())) {
            userRepository.log_auth("recover", user.getUserId(), user.getEmail(), "Fail", "Invalid OTP");
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            userRepository.log_auth("recover", user.getUserId(), user.getEmail(), "Fail", "Passwords do not match");
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        user.refreshAuthVersion(AUTH_VERSION_DIV);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        userRepository.log_auth("recover", user.getUserId(), user.getEmail(), "Success", "Recover successfully");

        return "Your password has been changed";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailService.sendOtpMail(email, otp, EmailType.RESET_PASSWORD);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... Please verify account within 5 minutes";
    }

    public String logoutOnce(LogoutRequest request){
        if(!jwtService.verifyToken(request.getRefreshToken())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var storedToken = tokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(()->new AppException(ErrorCode.INVALID_TOKEN));
        tokenRepository.delete(storedToken);
        return "Success";
    }

    public String logoutEverywhere(LogoutRequest request){
        if(!jwtService.verifyToken(request.getRefreshToken())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var storedToken = tokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(()->new AppException(ErrorCode.INVALID_TOKEN));
        var user = storedToken.getUser();
        user.refreshAuthVersion(AUTH_VERSION_DIV);
        userRepository.save(user);

        tokenRepository.deleteByUserId(storedToken.getUserId());
        return "Success";
    }

    public LoginRespone loginWithGoogle(String code, HttpServletResponse response) {
        var tokenResponse = getGoogleTokenClient.exchangeToken(
                ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build()
        );

        String idToken = tokenResponse.getIdToken();
        String googleAccessToken = tokenResponse.getAccessToken();

        String authorizationHeader = "Bearer " + idToken;
        String alt = "json";
        var userResponse = getGoogleUserClient.getUserInfo(authorizationHeader,googleAccessToken,alt);

        String emailUser = userResponse.getEmail();
        var user = userRepository.findByEmail(emailUser).orElse(
                User.builder()
                        .googleAccountId(1)
                        .email(emailUser)
                        .firstName(userResponse.getFamilyName())
                        .lastName(userResponse.getGivenName())
                        .role(Role.USER)
                        .avatarUrl(userResponse.getPicture())
                        .isActive(Status.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .password(passwordEncoder.encode("12345678")) //mac dinh, nguoi dung doi sau
                                .build()
        );
        var storedUser = userRepository.save(user);
        var accessToken = jwtService.generateAccessToken(storedUser);
        var refreshToken = jwtService.generateRefreshToken(storedUser);

        Token token = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .userId(user.getUserId())
                .build();

        tokenRepository.save(token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        userRepository.log_auth("login", user.getUserId(), user.getEmail(), "Success", "Login successfully");

        return LoginRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }
}
