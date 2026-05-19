package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndustryRepository extends JpaRepository<Industry, Long> {

    @Query("SELECT i FROM Industry i WHERE " +
            "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND i.isActive = true " +
            "ORDER BY i.sortOrder ASC")
    Page<Industry> searchIndustries(@Param("keyword") String keyword, Pageable pageable);
}