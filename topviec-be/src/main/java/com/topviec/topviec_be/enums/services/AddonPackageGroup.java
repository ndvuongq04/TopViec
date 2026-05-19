package com.topviec.topviec_be.enums.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AddonPackageGroup {
    JOB_POSTING("Nhóm tin tuyển dụng"),
    CANDIDATE("Nhóm hồ sơ"),
    BRANDING("Nhóm thương hiệu"),
    ADDON_PACKAGE_GROUP("Nhóm gói dịch vụ thêm");

    private final String value;

    AddonPackageGroup(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AddonPackageGroup fromValue(String value) {
        for (AddonPackageGroup group : AddonPackageGroup.values()) {
            if (group.name().equalsIgnoreCase(value) || group.value.equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Unknown AddonPackageGroup: " + value);
    }
}
