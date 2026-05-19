package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvCertificationRepository extends JpaRepository<CvCertification, Long> {

    List<CvCertification> findByCvIdOrderBySortOrderAsc(Long cvId);

    @Modifying
    @Query("DELETE FROM CvCertification c WHERE c.cvId = :cvId")
    void deleteByCvId(@Param("cvId") Long cvId);
}
