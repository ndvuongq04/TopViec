package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvExperienceRepository extends JpaRepository<CvExperience, Long> {

    List<CvExperience> findByCvIdOrderBySortOrderAsc(Long cvId);

    @Modifying
    @Query("DELETE FROM CvExperience e WHERE e.cvId = :cvId")
    void deleteByCvId(@Param("cvId") Long cvId);
}
