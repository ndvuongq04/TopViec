package com.topviec.topviec_be.enums.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RejectionReason {
    SPAM("spam"),
    FRAUDULENT("fraudulent"),
    WRONG_INFO("wrong_info"),
    INCOMPLETE("incomplete"),
    OTHER("other");

    private final String value;

    RejectionReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RejectionReason fromValue(String value) {
        for (RejectionReason reason : RejectionReason.values()) {
            if (reason.value.equalsIgnoreCase(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown RejectionReason: " + value);
    }
}