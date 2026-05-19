package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.SubscriptionRenewalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRenewalLogRepository extends JpaRepository<SubscriptionRenewalLog, Long> {
}
