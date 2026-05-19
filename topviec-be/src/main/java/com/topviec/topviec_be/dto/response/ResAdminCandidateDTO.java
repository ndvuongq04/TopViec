package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminCandidateDTO {
    private Long id; // User ID
    private String fullName;
    private String email;
    private String phoneDisplay;
    private String status; // UserStatus string
    private String avatarUrl;
    private String jobSeekingStatus;
    private LocalDateTime createdAt;
}
