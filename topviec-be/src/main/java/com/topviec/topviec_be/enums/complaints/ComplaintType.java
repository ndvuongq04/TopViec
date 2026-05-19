package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplaintType {
    FRAUDULENT("fraudulent"),
    SPAM("spam"),
    WRONG_INFO("wrong_info"),
    INAPPROPRIATE("inappropriate"),
    PAYMENT_ISSUE("payment_issue"),
    OTHER("other");

    private final String value;

    ComplaintType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ComplaintType fromValue(String value) {
        for (ComplaintType type : ComplaintType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ComplaintType: " + value);
    }
}
