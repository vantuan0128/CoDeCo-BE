package com.prj.furni_shop.configurations.schedule;

import com.prj.furni_shop.modules.product.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class SaleCleanupTask {

    @Autowired
    private SaleRepository saleRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void removeExpiredSales() {
        LocalDateTime now = LocalDateTime.now();
        saleRepository.deleteByEndDateBefore(now);
    }
}
