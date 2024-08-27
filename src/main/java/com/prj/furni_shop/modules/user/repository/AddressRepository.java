package com.prj.furni_shop.modules.user.repository;

import com.prj.furni_shop.modules.user.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Integer> {
    Page<Address> findAll(Pageable pageable);
    Page<Address> findAllByUserId(int userId, Pageable pageable);

    Address findByUserIdAndIsDefault(int userId, int isDefault);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM user_address ua WHERE ua.userAddressId = :addressId AND ua.userId = :userId")
//    void deleteByAddressId(@Param("addressId") int addressId, @Param("userId")int userId);
}
