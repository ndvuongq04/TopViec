package com.topviec.topviec_be.enums.candidateProfile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobSeekingStatus {
    ACTIVE("active"),
    PASSIVE("passive"),
    NOT_LOOKING("not_looking");

    private final String value;

    JobSeekingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JobSeekingStatus fromValue(String value) {
        for (JobSeekingStatus status : JobSeekingStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown JobSeekingStatus: " + value);
    }
}