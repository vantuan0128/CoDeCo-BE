package com.prj.furni_shop.modules.voucher.repository;

import com.prj.furni_shop.modules.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Optional<Voucher> findByCode(String voucherCode);

    boolean existsByCode(String voucherCode);
}
