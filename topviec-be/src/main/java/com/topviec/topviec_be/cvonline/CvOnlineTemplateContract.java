package com.topviec.topviec_be.cvonline;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CvOnlineTemplateContract {

    public static final Set<String> ROOT_TEXT_PLACEHOLDERS = Set.of(
            "fullName",
            "headline",
            "email",
            "phone",
            "address",
            "city",
            "country",
            "website",
            "linkedin",
            "github",
            "careerObjective");

    public static final Map<String, Set<String>> SECTION_ITEM_PLACEHOLDERS;

    public static final List<String> SUPPORTED_PDF_CSS_RULES = List.of(
            "Prefer block layout, table layout, and inline-block for stable PDF output.",
            "Use explicit spacing, typography, and border declarations.",
            "Avoid deep flexbox and CSS grid in PDF-oriented templates.",
            "Avoid sticky/fixed positioning and complex transform/filter effects.");

    static {
        Map<String, Set<String>> sections = new LinkedHashMap<>();
        sections.put("experiences", Set.of(
                "jobTitle",
                "company",
                "location",
                "startDate",
                "endDate",
                "isCurrent",
                "description"));
        sections.put("educations", Set.of(
                "school",
                "degree",
                "fieldOfStudy",
                "startDate",
                "endDate",
                "description"));
        sections.put("skills", Set.of(
                "name",
                "level",
                "description"));
        sections.put("certifications", Set.of(
                "name",
                "issuer",
                "issuedAt",
                "expiresAt",
                "credentialId",
                "credentialUrl",
                "description"));
        sections.put("languages", Set.of(
                "name",
                "level",
                "certificate"));
        sections.put("projects", Set.of(
                "name",
                "role",
                "organization",
                "startDate",
                "endDate",
                "projectUrl",
                "description"));
        sections.put("hobbies", Set.of(
                "name",
                "description"));
        sections.put("awards", Set.of(
                "title",
                "issuer",
                "awardedAt",
                "description"));
        sections.put("customSections", Set.of(
                "sectionTitle",
                "itemTitle",
                "itemSubtitle",
                "itemMeta",
                "description"));
        SECTION_ITEM_PLACEHOLDERS = Collections.unmodifiableMap(sections);
    }

    private CvOnlineTemplateContract() {
    }

    public static boolean isSupportedRootPlaceholder(String placeholder) {
        return ROOT_TEXT_PLACEHOLDERS.contains(placeholder);
    }

    public static boolean isSupportedSection(String sectionName) {
        return SECTION_ITEM_PLACEHOLDERS.containsKey(sectionName);
    }

    public static boolean isSupportedSectionItemPlaceholder(String sectionName, String placeholder) {
        return SECTION_ITEM_PLACEHOLDERS.getOrDefault(sectionName, Set.of()).contains(placeholder);
    }

    public static Set<String> getSupportedSections() {
        return SECTION_ITEM_PLACEHOLDERS.keySet();
    }

    public static Set<String> getAllSupportedPlaceholderNames() {
        Set<String> placeholders = new LinkedHashSet<>(ROOT_TEXT_PLACEHOLDERS);
        SECTION_ITEM_PLACEHOLDERS.values().forEach(placeholders::addAll);
        return Collections.unmodifiableSet(placeholders);
    }
}
