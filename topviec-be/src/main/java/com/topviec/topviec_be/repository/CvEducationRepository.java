package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvEducationRepository extends JpaRepository<CvEducation, Long> {

    List<CvEducation> findByCvIdOrderBySortOrderAsc(Long cvId);

    @Modifying
    @Query("DELETE FROM CvEducation e WHERE e.cvId = :cvId")
    void deleteByCvId(@Param("cvId") Long cvId);
}
