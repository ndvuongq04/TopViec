package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.ServicePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    List<ServicePackage> findByIsActiveTrueOrderBySortOrderAsc();

    @Query("SELECT sp FROM ServicePackage sp WHERE " +
           "(:keyword IS NULL OR LOWER(sp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(sp.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ServicePackage> searchAll(@Param("keyword") String keyword, Pageable pageable);
}
