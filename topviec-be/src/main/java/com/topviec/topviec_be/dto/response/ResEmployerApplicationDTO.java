package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.cvs.CvType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResEmployerApplicationDTO {

    private Long id;
    private Long jobPostId;
    private String jobTitle;

    // Thông tin ứng viên (lấy từ CandidateProfile / User)
    private Long candidateUserId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String candidateAvatar;

    // Thông tin CV (lấy từ Cvs)
    private Long cvId;
    private CvType cvType;
    private String cvFileUrl;
    private String cvPdfUrl;
    private Boolean cvPdfDirty;
    private String cvPreviewUrl;

    // Thông tin apply
    private String status;
    private String applyMethod;
    private Integer recruiterRating;
    private String recruiterNote;
    private String recruiterTags;
    private LocalDateTime viewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
