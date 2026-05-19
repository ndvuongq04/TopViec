package com.topviec.topviec_be.enums.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EvidenceFileType {
    IMAGE("image"),
    PDF("pdf"),
    VIDEO("video");

    private final String value;

    EvidenceFileType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EvidenceFileType fromValue(String value) {
        for (EvidenceFileType type : EvidenceFileType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EvidenceFileType: " + value);
    }
}
