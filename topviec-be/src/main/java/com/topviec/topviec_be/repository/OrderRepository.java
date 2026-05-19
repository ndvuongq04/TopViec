package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.enums.services.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<Order> findByOrderCode(String orderCode);

    /** Doanh thu trung bình trên mỗi đơn hàng đã thanh toán */
    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal findAverageRevenueByStatus(@Param("status") OrderStatus status);

    /** Đếm số công ty (distinct) có ít nhất 1 đơn hàng đã thanh toán */
    @Query("SELECT COUNT(DISTINCT o.companyId) FROM Order o WHERE o.status = :status")
    long countDistinctCompanyByStatus(@Param("status") OrderStatus status);

    /** Đếm số đơn hàng theo trạng thái */
    long countByStatus(OrderStatus status);

    /** Tổng giá trị đơn hàng theo trạng thái */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.status = :status
            AND o.paidAt >= :startDate
            AND o.paidAt < :endDate
            """)
    BigDecimal sumPaidRevenueInDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT FUNCTION('DATE_FORMAT', o.paidAt, '%Y-%m'), SUM(o.totalAmount)
            FROM Order o
            WHERE o.status = :status
            AND o.paidAt >= :startDate
            AND o.paidAt < :endDate
            GROUP BY FUNCTION('DATE_FORMAT', o.paidAt, '%Y-%m')
            ORDER BY FUNCTION('DATE_FORMAT', o.paidAt, '%Y-%m')
            """)
    List<Object[]> sumRevenueGroupByMonth(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT o FROM Order o
            WHERE o.status IN :statuses
            ORDER BY o.createdAt DESC
            """)
    List<Order> findActionableOrders(
            @Param("statuses") List<OrderStatus> statuses,
            Pageable pageable);
}
