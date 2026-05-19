package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.ComplaintEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintEvidenceRepository extends JpaRepository<ComplaintEvidence, Long> {

    List<ComplaintEvidence> findByComplaintId(Long complaintId);

    List<ComplaintEvidence> findByComplaintIdIn(List<Long> complaintIds);
}
