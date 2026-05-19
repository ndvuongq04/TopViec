package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddonServiceRepository extends JpaRepository<AddonService, Long> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    List<AddonService> findByIsActiveTrue();

    @Query("SELECT a FROM AddonService a JOIN a.service s WHERE a.isActive = true AND s.category = :category")
    List<AddonService> findByIsActiveTrueAndServiceCategory(@Param("category") ServiceCategory category);

    @Query("SELECT a FROM AddonService a JOIN a.service s WHERE " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:keyword IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AddonService> searchAll(@Param("category") ServiceCategory category,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    List<AddonService> findByServiceId(Long serviceId);
}
