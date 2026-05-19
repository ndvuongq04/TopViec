package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAdjustViolationScoreDTO;
import com.topviec.topviec_be.dto.request.ReqResetViolationScoreDTO;
import com.topviec.topviec_be.dto.response.ResMyViolationScoreDTO;
import com.topviec.topviec_be.dto.response.ResViolationScoreDTO;

public interface ViolationScoreService {

    /** Admin xem tổng điểm vi phạm + lịch sử của một NTD */
    ResViolationScoreDTO getScore(Long employerId);

    /** Admin reset điểm về 0 (điều kiện: không tái phạm nhóm B trong 6 tháng) */
    ResViolationScoreDTO resetScore(Long adminUserId, Long employerId, ReqResetViolationScoreDTO request);

    /** Admin giảm điểm thủ công khi NTD chủ động khắc phục */
    ResViolationScoreDTO adjustScore(Long adminUserId, Long employerId, ReqAdjustViolationScoreDTO request);

    /** NTD xem điểm vi phạm của chính mình (không bao gồm lịch sử chi tiết) */
    ResMyViolationScoreDTO getMyScore(Long employerUserId);

    /** Cron hàng tháng: tự động reset điểm cho NTD đủ điều kiện không tái phạm nhóm B */
    void autoResetEligibleScores();
}
