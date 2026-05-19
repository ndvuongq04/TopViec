package com.topviec.topviec_be.enums.companyMember;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberStatus {
    PENDING("pending"),
    ACTIVE("active"),
    DEACTIVATED("deactivated");

    private final String value;

    MemberStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MemberStatus fromValue(String value) {
        for (MemberStatus status : MemberStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown MemberStatus: " + value);
    }
}
