package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplaintStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    WAITING_EMPLOYER("waiting_employer"),
    RESOLVED("resolved"),
    REJECTED("rejected"),
    AUTO_CLOSED("auto_closed");

    private final String value;

    ComplaintStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ComplaintStatus fromValue(String value) {
        for (ComplaintStatus status : ComplaintStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ComplaintStatus: " + value);
    }
}
