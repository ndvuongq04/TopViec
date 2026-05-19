package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResApplicationDTO {

    private Long id;
    private Long jobPostId;
    private Long candidateUserId;
    private Long cvId;
    private String status;
    private String applyMethod;
    private String withdrawalReason;
    private LocalDateTime withdrawnAt;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    private LocalDateTime expiredAt;
    private LocalDateTime hiredAt;
    private LocalDateTime viewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thông tin job gọn
    private JobInfo jobPosting;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JobInfo {
        private Long id;
        private String title;
        private String slug;
        private String status;
        private LocalDateTime deadline;
        private CompanyInfo company;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String logoUrl;
        private Boolean isBrandVerified;
    }
}