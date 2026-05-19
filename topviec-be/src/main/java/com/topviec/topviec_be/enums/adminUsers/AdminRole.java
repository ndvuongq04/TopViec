package com.topviec.topviec_be.enums.adminUsers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AdminRole {
    SUPER_ADMIN("super_admin"),
    CONTENT_MODERATOR("content_moderator"),
    SUPPORT_ADMIN("support_admin"),
    FINANCE_ADMIN("finance_admin");

    private final String value;

    AdminRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AdminRole fromValue(String value) {
        for (AdminRole role : AdminRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown AdminRole: " + value);
    }
}