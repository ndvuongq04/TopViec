package com.topviec.topviec_be.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCompanyPlanDTO {

    private CurrentPackageDTO currentPackage;
    private List<CurrentAddonDTO> currentAddons;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentPackageDTO {
        private Long subscriptionId;
        private Long servicePackageId;
        private String packageName;
        private String packageCode;
        private String billingCycle;
        private String status;
        private LocalDateTime startedAt;
        private LocalDateTime expiredAt;
        private Long orderId;
        private String orderCode;
        private List<UsageDTO> usages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentAddonDTO {
        private Long addonId;
        private Long addonServiceId;
        private String addonName;
        private String addonCode;
        private String serviceCategory;
        private String serviceCategoryName;
        private String status;
        private Integer total;
        private Integer used;
        private LocalDateTime startedAt;
        private LocalDateTime expiredAt;
        private Long orderId;
        private String orderCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageDTO {
        private String featureCode;
        private String featureName;
        private Integer total;
        private Integer used;
    }
}
