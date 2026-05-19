package com.topviec.topviec_be.enums.authAccount;

public enum AuthProvider {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    LINKEDIN("linkedin");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthProvider fromValue(String value) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown AuthProvider: " + value);
    }
}