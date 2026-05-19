package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResTalentPoolInviteInfoDTO;
import com.topviec.topviec_be.service.TalentPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoint — UV không cần đăng nhập để xem thông tin lời mời từ email.
 * Base URL: /talent-pool-invite
 */
@RestController
@RequestMapping("/talent-pool-invite")
@RequiredArgsConstructor
public class TalentPoolInvitePublicController {

    private final TalentPoolService talentPoolService;

    /**
     * GET /talent-pool-invite/verify?token=...
     * Xác thực token trong link email mời ứng tuyển, trả về applicationId + thông tin tin tuyển dụng.
     * Frontend dùng kết quả này để hiển thị trang mời và sau đó gọi accept-invite / decline-invite.
     */
    @GetMapping("/verify")
    public ResponseEntity<ResTalentPoolInviteInfoDTO> verifyInviteToken(@RequestParam String token) {
        return ResponseEntity.ok(talentPoolService.verifyInviteToken(token));
    }
}
