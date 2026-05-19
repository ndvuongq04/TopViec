package com.topviec.topviec_be.enums.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TalentPoolCategory {
    HOT_CANDIDATE("hot_candidate"),
    WATCHING("watching"),
    FUTURE("future"),
    REJECTED_BUT_PROMISING("rejected_but_promising");

    private final String value;

    TalentPoolCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TalentPoolCategory fromValue(String value) {
        for (TalentPoolCategory category : TalentPoolCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown TalentPoolCategory: " + value);
    }
}