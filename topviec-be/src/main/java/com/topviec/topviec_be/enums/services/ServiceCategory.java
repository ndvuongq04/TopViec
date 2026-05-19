package com.topviec.topviec_be.enums.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceCategory {
    JOB_POSTING("Tin tuyển dụng"),
    CANDIDATE("Hồ sơ ứng viên"),
    BRANDING("Thương hiệu"),
    ADDON_PACKAGE("Gói dịch vụ thêm");

    private final String value;

    ServiceCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ServiceCategory fromValue(String value) {
        for (ServiceCategory category : ServiceCategory.values()) {
            if (category.name().equalsIgnoreCase(value) || category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown ServiceCategory: " + value);
    }
}
