package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResJobPostingSummary {

    private Long id;
    private String title;
    private String slug;
    private CompanyDTO company;
    private IndustryDTO industry;
    private LevelDTO level;
    private String workType;
    private String status;
    private Long salaryMin;
    private Long salaryMax;
    private Boolean salaryNegotiable;
    private Boolean isFeatured;
    private Boolean isUrgent;
    private Boolean isHot;
    private Integer viewCount;
    private Integer applicationCount;   // Tổng số hồ sơ đã nộp vào tin
    private Integer interviewRoundsCount; // Tổng số vòng phỏng vấn của tin
    private Integer headcount;          // Số UV cần tuyển
    private Integer hiredCount;         // Số offer thành công (status = hired)
    private LocalDateTime deadline;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;   // null = đang hoạt động, non-null = đã xóa mềm
    private List<LocationDTO> locations;
    private AssignedRecruiterDTO assignedRecruiter; // NTD đang được phân công quản lý tin (null = chưa phân công)

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignedRecruiterDTO {
        private Long userId;
        private String email;
        private String jobTitle; // Chức danh trong công ty
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyDTO {
        private Long id;
        private String name;
        private String slug;
        private String logoUrl;
        private String address;
        private Boolean isTopEmployer;
        private Boolean isBrandVerified;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IndustryDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LevelDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationDTO {
        private Long id;
        private String name;
        private String addressDetail;
        private Boolean isRemote;
    }
}