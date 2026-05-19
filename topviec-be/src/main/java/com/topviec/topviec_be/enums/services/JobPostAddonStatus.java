package com.topviec.topviec_be.enums.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobPostAddonStatus {
    ACTIVE("active"),
    EXPIRED("expired");

    private final String value;

    JobPostAddonStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JobPostAddonStatus fromValue(String value) {
        for (JobPostAddonStatus status : JobPostAddonStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown JobPostAddonStatus: " + value);
    }
}
