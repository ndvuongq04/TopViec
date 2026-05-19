package com.topviec.topviec_be.enums.users;

public enum UserStatus {
    PENDING("pending"),
    ACTIVE("active"),
    LOCKED_PERM("locked_perm");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserStatus fromValue(String value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UserStatus: " + value);
    }
}