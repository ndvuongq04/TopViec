package com.topviec.topviec_be.enums.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EditType {
    DRAFT_EDIT("draft_edit"),
    POST_PUBLISH_EDIT("post_publish_edit"),
    ADMIN_EDIT("admin_edit");

    private final String value;

    EditType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EditType fromValue(String value) {
        for (EditType type : EditType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EditType: " + value);
    }
}