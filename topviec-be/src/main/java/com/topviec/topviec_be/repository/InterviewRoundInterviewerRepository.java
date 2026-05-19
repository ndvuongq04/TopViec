package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.InterviewRoundInterviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRoundInterviewerRepository extends JpaRepository<InterviewRoundInterviewer, Long> {

    List<InterviewRoundInterviewer> findByRoundId(Long roundId);

    @Modifying
    void deleteByRoundId(Long roundId);
}
