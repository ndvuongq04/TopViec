package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {


    List<InterviewSlot> findByRoundIdOrderByStartTimeAsc(Long roundId);

    List<InterviewSlot> findByRoundIdAndBatchNumberInOrderByBatchNumberAscStartTimeAsc(Long roundId, List<Integer> batchNumbers);

    boolean existsByRoundId(Long id);

    @Modifying
    void deleteByRoundId(Long roundId);
}
