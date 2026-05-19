package com.topviec.topviec_be.enums.candidateProfile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PreferredWorkType {
    FULL_TIME("full_time"),
    PART_TIME("part_time"),
    REMOTE("remote"),
    HYBRID("hybrid");

    private final String value;

    PreferredWorkType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PreferredWorkType fromValue(String value) {
        for (PreferredWorkType type : PreferredWorkType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PreferredWorkType: " + value);
    }
}