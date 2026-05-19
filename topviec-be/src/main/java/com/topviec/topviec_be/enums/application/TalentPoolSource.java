package com.topviec.topviec_be.enums.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TalentPoolSource {
    SEARCH_IN_DB("Tìm kiếm UV trong DB"),
    REVIEW_CV("Duyệt CV"),
    INTERVIEW("PV UV");

    private final String value;

    TalentPoolSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TalentPoolSource fromValue(String value) {
        for (TalentPoolSource source : TalentPoolSource.values()) {
            if (source.value.equalsIgnoreCase(value) || source.name().equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown TalentPoolSource: " + value);
    }
}
