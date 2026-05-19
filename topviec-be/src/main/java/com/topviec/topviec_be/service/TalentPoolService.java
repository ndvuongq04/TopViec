package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAddToTalentPoolDTO;
import com.topviec.topviec_be.dto.request.ReqInviteFromTalentPoolDTO;
import com.topviec.topviec_be.dto.response.ResApplicationDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolCandidateDetailDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolInviteInfoDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import java.util.Set;

public interface TalentPoolService {
    ResTalentPoolDTO addToTalentPool(Long userId, Long companyId, ReqAddToTalentPoolDTO request);

    ResultPaginationDTO getTalentPool(Long companyId, String source, String search, Pageable pageable);

    ResTalentPoolCandidateDetailDTO getTalentPoolCandidateDetail(Long companyId, Long talentPoolId);

    /** NTD xem chi tiết UV chưa lưu vào talent pool (theo candidateUserId) */
    ResTalentPoolCandidateDetailDTO getCandidateDetail(Long companyId, Long candidateUserId);

    void removeFromTalentPool(Long companyId, Long talentPoolId);

    void updateNote(Long companyId, Long talentPoolId, String note);

    ResApplicationDTO invite(Long employerUserId, Long companyId, Long talentPoolId, ReqInviteFromTalentPoolDTO request);

    ResTalentPoolInviteInfoDTO verifyInviteToken(String token);

    /** NTD tìm kiếm UV trong DB theo địa chỉ mong muốn để thêm vào talent pool */
    ResultPaginationDTO searchCandidates(Long companyId, Integer locationId, Pageable pageable);
}
