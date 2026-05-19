package com.topviec.topviec_be.enums.interview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterviewResultStatus {

    PASS("pass"),
    FAIL("fail"),
    PENDING("pending"),
    NO_SHOW("no_show");

    private final String value;

    InterviewResultStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InterviewResultStatus fromValue(String value) {
        for (InterviewResultStatus status : InterviewResultStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown InterviewResultStatus: " + value);
    }
}
