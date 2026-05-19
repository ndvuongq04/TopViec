package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LevelRepository extends JpaRepository<Level, Long> {

    @Query("SELECT l FROM Level l WHERE " +
            "(:keyword IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY l.rank ASC")
    Page<Level> searchLevels(@Param("keyword") String keyword, Pageable pageable);
}