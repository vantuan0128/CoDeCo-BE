package com.prj.furni_shop.modules.voucher.entity;

import com.prj.furni_shop.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userVoucherId;

    @Column(name = "user_id")
    int userId;

    @Column(name = "voucher_id")
    int voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false, insertable = false, updatable = false)
    Voucher voucher;

    Boolean isUsed;

}
