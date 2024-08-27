package com.prj.furni_shop.modules.order.repository;

import com.prj.furni_shop.modules.order.entity.Order;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    List<Order> findAllByUserId(int userId);

    List<Order> findAllByUserIdAndStatus(int userId, OrderStatus status);

    Optional<Order> findByOrderId(int orderId);

    long count();

    long countByStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM orders o WHERE o.createdAt <= :endDate")
    Long countOrderUntil(@Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(o.totalMoney) FROM orders o WHERE o.createdAt <= :endDate AND o.status = :status")
    Long totalMoneyUntil(@Param("endDate") LocalDateTime endDate, @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.count) FROM orders o WHERE o.createdAt <= :endDate AND o.status = :status")
    Long totalSaleUntil(@Param("endDate") LocalDateTime endDate, @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalMoney), 0) FROM orders o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status" )
    Long totalMoneyForDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.count), 0) FROM orders o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status")
    Long totalOrderForDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, OrderStatus status);

}
