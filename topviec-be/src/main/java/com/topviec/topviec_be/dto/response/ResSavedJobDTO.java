package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResSavedJobDTO {

    private Long id;
    private Long jobPostId;
    private LocalDateTime savedAt;

    // Thông tin job
    private ResJobPostingSummary jobPosting;
}