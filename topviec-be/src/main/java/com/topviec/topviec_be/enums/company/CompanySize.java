package com.topviec.topviec_be.enums.company;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanySize {
    SMALL("1-50"),
    MEDIUM("51-200"),
    LARGE("201-500"),
    ENTERPRISE("500+");

    private final String value;

    CompanySize(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CompanySize fromValue(String value) {
        for (CompanySize size : CompanySize.values()) {
            if (size.value.equalsIgnoreCase(value)) {
                return size;
            }
        }
        throw new IllegalArgumentException("Unknown CompanySize: " + value);
    }
}