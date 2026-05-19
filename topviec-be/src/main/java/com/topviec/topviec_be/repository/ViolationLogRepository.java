package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {

    List<ViolationLog> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    List<ViolationLog> findByComplaintId(Long complaintId);
}
