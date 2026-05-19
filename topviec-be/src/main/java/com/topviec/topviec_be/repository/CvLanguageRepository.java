package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvLanguageRepository extends JpaRepository<CvLanguage, Long> {

    List<CvLanguage> findByCvIdOrderBySortOrderAsc(Long cvId);

    @Modifying
    @Query("DELETE FROM CvLanguage l WHERE l.cvId = :cvId")
    void deleteByCvId(@Param("cvId") Long cvId);
}
