package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.SubscriptionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {

    List<SubscriptionUsage> findByCompanySubscriptionId(Long companySubscriptionId);
}
