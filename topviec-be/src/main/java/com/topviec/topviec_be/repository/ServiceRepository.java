package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
       boolean existsByCode(String code);

       boolean existsByCodeAndIdNot(String code, Long id);

       Optional<Services> findByCode(String code);

       List<Services> findByIsActiveTrue();

       List<Services> findByIsActiveTrueAndCategory(ServiceCategory category);

       @Query("SELECT s FROM Services s WHERE " +
                     "(:category IS NULL OR s.category = :category) AND " +
                     "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                     "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<Services> searchAll(@Param("category") ServiceCategory category,
                     @Param("keyword") String keyword,
                     Pageable pageable);
}
