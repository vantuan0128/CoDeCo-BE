package com.prj.furni_shop.modules.voucher.repository;

import com.prj.furni_shop.modules.voucher.entity.UserVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher,Integer> {
    Optional<UserVoucher> findByUserIdAndVoucherId(int userId, int voucherId);

    List<UserVoucher> findByUserId(int userId);

    Page<UserVoucher> findByUserId(int userId, Pageable pageable);

    @Query("SELECT uv FROM UserVoucher uv WHERE uv.userId = :userId AND uv.isUsed = false AND uv.voucher.endDate >= CURRENT_TIMESTAMP")
    Page<UserVoucher> findUnusedVouchersByUserId(int userId, Pageable pageable);

    @Query("SELECT uv FROM UserVoucher uv WHERE uv.userId = :userId AND uv.isUsed = true")
    Page<UserVoucher> findUsedVouchersByUserId(int userId, Pageable pageable);

    @Query("SELECT uv FROM UserVoucher uv WHERE uv.userId = :userId AND uv.voucher.endDate < CURRENT_TIMESTAMP")
    Page<UserVoucher> findExpiredVouchersByUserId(int userId, Pageable pageable);
}
