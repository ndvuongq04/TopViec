package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ViolationSource {
    ADMIN("admin"),
    SYSTEM("system"),
    COMPLAINT("complaint");

    private final String value;

    ViolationSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ViolationSource fromValue(String value) {
        for (ViolationSource source : ViolationSource.values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown ViolationSource: " + value);
    }
}
