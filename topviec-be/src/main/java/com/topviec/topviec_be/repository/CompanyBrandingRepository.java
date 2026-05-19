package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CompanyBranding;
import com.topviec.topviec_be.enums.services.BrandingAddonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompanyBrandingRepository extends JpaRepository<CompanyBranding, Long> {

    /** Kiểm tra công ty đang có dịch vụ branding theo service code active không */
    @Query("SELECT COUNT(cb) FROM CompanyBranding cb " +
           "JOIN cb.addonService asv " +
           "JOIN asv.service s " +
           "WHERE cb.companyId = :companyId " +
           "AND s.code = :serviceCode " +
           "AND cb.status = 'ACTIVE' " +
           "AND cb.expiredAt > :now")
    long countActiveForCompany(@Param("companyId") Long companyId,
                               @Param("serviceCode") String serviceCode,
                               @Param("now") LocalDateTime now);

    /** Tìm các record của 1 service đã hết hạn để scheduler xử lý */
    List<CompanyBranding> findByServiceCodeAndStatusAndExpiredAtBefore(
            String serviceCode, BrandingAddonStatus status, LocalDateTime now);
}
