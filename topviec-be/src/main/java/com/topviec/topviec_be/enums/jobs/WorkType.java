package com.topviec.topviec_be.enums.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkType {
    FULL_TIME("full_time"),
    PART_TIME("part_time"),
    REMOTE("remote"),
    HYBRID("hybrid"),
    FREELANCE("freelance");

    private final String value;

    WorkType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WorkType fromValue(String value) {
        for (WorkType type : WorkType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown WorkType: " + value);
    }
}