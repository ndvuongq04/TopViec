package com.topviec.topviec_be.enums.companyMember;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberRole {
    OWNER("owner"),
    MANAGER("manager"),
    RECRUITER("recruiter"),
    VIEWER("viewer");

    private final String value;

    MemberRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MemberRole fromValue(String value) {
        for (MemberRole role : MemberRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown MemberRole: " + value);
    }
}