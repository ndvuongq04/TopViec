package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResRecruiterWithAssignmentCountDTO {

    private Long userId;
    private String email;
    private String roleName;
    private String jobTitle;
    private String status;
    private long assignedJobCount;
    private Boolean isCurrentAssignee; // true nếu NTD đang quản lý tin cụ thể (khi truyền jobPostId)
}
