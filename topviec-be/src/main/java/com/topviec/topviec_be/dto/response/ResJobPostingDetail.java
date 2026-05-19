package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResJobPostingDetail {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private String requirements;
    private String benefits;

    private CompanyDTO company;
    private IndustryDTO industry;
    private LevelDTO level;

    private Integer experienceYearsMin;
    private Integer experienceYearsMax;

    private Long salaryMin;
    private Long salaryMax;
    private Boolean salaryNegotiable;

    private String workType;
    private Integer headcount;
    private LocalDateTime deadline;
    private String status;

    private Boolean isFeatured;
    private Boolean isUrgent;
    private Boolean isHot;

    private Integer viewCount;
    private Integer editCount;
    private Integer applicationCount;   // Tổng số hồ sơ đã nộp vào tin

    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<LocationDTO> locations;
    private List<ResJobPostSkillDTO> skills;

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