package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvSkillRepository extends JpaRepository<CvSkill, Long> {

    List<CvSkill> findByCvIdOrderBySortOrderAsc(Long cvId);

    @Modifying
    @Query("DELETE FROM CvSkill s WHERE s.cvId = :cvId")
    void deleteByCvId(@Param("cvId") Long cvId);
}
