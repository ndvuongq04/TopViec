package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.ComplaintAppeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintAppealRepository extends JpaRepository<ComplaintAppeal, Long> {

    List<ComplaintAppeal> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    Optional<ComplaintAppeal> findByComplaintId(Long complaintId);

    Optional<ComplaintAppeal> findByComplaintIdAndEmployerId(Long complaintId, Long employerId);

    boolean existsByComplaintIdAndEmployerIdAndStatusIn(Long complaintId, Long employerId, List<String> statuses);

    long countByStatus(String status);
}
