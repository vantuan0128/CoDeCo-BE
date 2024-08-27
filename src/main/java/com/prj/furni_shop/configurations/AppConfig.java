package com.prj.furni_shop.configurations;

import com.prj.furni_shop.auditing.AppAuditAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {
    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new AppAuditAware();
    }
}
