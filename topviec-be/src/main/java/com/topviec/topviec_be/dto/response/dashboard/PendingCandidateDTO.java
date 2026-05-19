package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCandidateDTO {
    private Long applicationId;
    private String candidateName;
    private String jobTitle;
    private Long jobPostId;
    private String status;
    private LocalDateTime createdAt;
}
