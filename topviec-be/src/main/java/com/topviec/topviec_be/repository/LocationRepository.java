package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE " +
            "(:keyword IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY l.sortOrder ASC")
    Page<Location> searchLocations(@Param("keyword") String keyword, Pageable pageable);
}