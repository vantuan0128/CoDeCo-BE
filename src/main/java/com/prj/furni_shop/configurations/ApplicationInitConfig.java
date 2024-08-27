package com.prj.furni_shop.configurations;

import com.prj.furni_shop.modules.user.entity.User;
import static com.prj.furni_shop.modules.user.enums.Role.ADMIN;

import com.prj.furni_shop.modules.user.enums.Status;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(!userRepository.existsByEmail("admin@gmail.com")){
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                User user = User.builder()
                        .email("admin@gmail.com")
                        .firstName("admin")
                        .password(passwordEncoder.encode("12345"))
                        .role(ADMIN)
                        .isActive(Status.ACTIVE)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it");
            }
        };
    }
}
