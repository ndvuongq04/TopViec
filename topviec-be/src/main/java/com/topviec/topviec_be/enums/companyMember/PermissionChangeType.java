package com.topviec.topviec_be.enums.companyMember;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionChangeType {
    ROLE_CHANGE("role_change"),
    PERMISSION_UPDATE("permission_update"),
    STATUS_CHANGE("status_change");

    private final String value;

    PermissionChangeType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PermissionChangeType fromValue(String value) {
        for (PermissionChangeType type : PermissionChangeType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PermissionChangeType: " + value);
    }
}