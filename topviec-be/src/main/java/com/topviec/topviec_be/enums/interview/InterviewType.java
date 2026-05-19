package com.topviec.topviec_be.enums.interview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterviewType {

    ONSITE("onsite"),
    ONLINE("online"),
    PHONE("phone");

    private final String value;

    InterviewType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InterviewType fromValue(String value) {
        for (InterviewType type : InterviewType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InterviewType: " + value);
    }
}
