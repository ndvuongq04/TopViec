package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ViolationGroup {
    A("A"),
    B("B");

    private final String value;

    ViolationGroup(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ViolationGroup fromValue(String value) {
        for (ViolationGroup group : ViolationGroup.values()) {
            if (group.value.equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Unknown ViolationGroup: " + value);
    }
}
