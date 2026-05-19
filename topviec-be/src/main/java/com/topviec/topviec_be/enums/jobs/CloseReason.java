package com.topviec.topviec_be.enums.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CloseReason {
    FILLED("filled"),
    CANCELLED("cancelled"),
    OTHER("other");

    private final String value;

    CloseReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CloseReason fromValue(String value) {
        for (CloseReason reason : CloseReason.values()) {
            if (reason.value.equalsIgnoreCase(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown CloseReason: " + value);
    }
}