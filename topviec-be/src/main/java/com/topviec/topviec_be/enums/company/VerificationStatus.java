package com.topviec.topviec_be.enums.company;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationStatus {
    PENDING("pending"),
    VERIFIED("verified"),
    REJECTED("rejected");

    private final String value;

    VerificationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VerificationStatus fromValue(String value) {
        for (VerificationStatus status : VerificationStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown VerificationStatus: " + value);
    }
}