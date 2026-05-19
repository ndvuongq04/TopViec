package com.topviec.topviec_be.enums.interview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OfferResult {

    ACCEPTED("accepted"),
    DECLINED("declined");

    private final String value;

    OfferResult(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OfferResult fromValue(String value) {
        for (OfferResult result : OfferResult.values()) {
            if (result.value.equalsIgnoreCase(value)) {
                return result;
            }
        }
        throw new IllegalArgumentException("Unknown OfferResult: " + value);
    }
}
