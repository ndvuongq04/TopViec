package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplaintPriority {
    URGENT("urgent"),
    IMPORTANT("important"),
    NORMAL("normal");

    private final String value;

    ComplaintPriority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ComplaintPriority fromValue(String value) {
        for (ComplaintPriority priority : ComplaintPriority.values()) {
            if (priority.value.equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown ComplaintPriority: " + value);
    }
}
