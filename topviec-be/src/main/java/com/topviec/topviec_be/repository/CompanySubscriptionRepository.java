package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CompanySubscription;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, Long> {

        List<CompanySubscription> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId,
                        SubscriptionStatus status);

        Optional<CompanySubscription> findFirstByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId,
                        SubscriptionStatus status);

        Optional<CompanySubscription> findFirstByCompanyIdOrderByCreatedAtDesc(Long companyId);

        Optional<CompanySubscription> findByOrderId(Long orderId);

        Page<CompanySubscription> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);

        long countByCompanyIdAndStatus(Long companyId, SubscriptionStatus status);

        /**
         * Tìm subscription ACTIVE sắp hết hạn + chưa gửi nhắc nhở — dùng cho scheduler
         */
        @Query("SELECT cs FROM CompanySubscription cs WHERE cs.status = :status " +
                        "AND cs.expiredAt BETWEEN :from AND :to " +
                        "AND cs.reminderSentAt IS NULL")
        List<CompanySubscription> findByStatusAndExpiredAtBetween(
                        @Param("status") SubscriptionStatus status,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        /**
         * Tìm subscription vẫn ACTIVE nhưng đã quá hạn — dùng cho scheduler auto-expire
         */
        @Query("SELECT cs FROM CompanySubscription cs WHERE cs.status = :status AND cs.expiredAt <= :now")
        List<CompanySubscription> findExpiredActiveSubscriptions(
                        @Param("status") SubscriptionStatus status,
                        @Param("now") LocalDateTime now);
}
