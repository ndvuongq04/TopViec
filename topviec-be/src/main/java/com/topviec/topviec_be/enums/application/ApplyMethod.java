package com.topviec.topviec_be.enums.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplyMethod {
    NORMAL("normal"),
    QUICK("quick"),
    BULK("bulk"),
    INVITED("invited"); // Được mời từ talent pool

    private final String value;

    ApplyMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ApplyMethod fromValue(String value) {
        for (ApplyMethod method : ApplyMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown ApplyMethod: " + value);
    }
}