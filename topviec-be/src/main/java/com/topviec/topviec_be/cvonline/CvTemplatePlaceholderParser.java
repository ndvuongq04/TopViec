package com.topviec.topviec_be.cvonline;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CvTemplatePlaceholderParser {

    private static final Pattern TAG_PATTERN = Pattern.compile("\\{\\{\\s*([#/])?\\s*([a-zA-Z][a-zA-Z0-9]*)\\s*\\}\\}");

    private CvTemplatePlaceholderParser() {
    }

    public static TemplateValidationResult validate(String templateContent) {
        Objects.requireNonNull(templateContent, "templateContent must not be null");

        Set<String> rootPlaceholders = new LinkedHashSet<>();
        Set<String> openedSections = new LinkedHashSet<>();
        List<String> errors = new ArrayList<>();
        ArrayDeque<String> sectionStack = new ArrayDeque<>();

        Matcher matcher = TAG_PATTERN.matcher(templateContent);
        while (matcher.find()) {
            String marker = matcher.group(1);
            String token = matcher.group(2);

            if (marker == null) {
                if (sectionStack.isEmpty()) {
                    if (CvOnlineTemplateContract.isSupportedRootPlaceholder(token)) {
                        rootPlaceholders.add(token);
                    } else {
                        errors.add("Unsupported root placeholder: " + token);
                    }
                    continue;
                }

                String currentSection = sectionStack.peek();
                if (!CvOnlineTemplateContract.isSupportedSectionItemPlaceholder(currentSection, token)) {
                    errors.add("Unsupported placeholder '" + token + "' in section '" + currentSection + "'");
                }
                continue;
            }

            if ("#".equals(marker)) {
                if (!sectionStack.isEmpty()) {
                    errors.add("Nested section blocks are not supported: " + token);
                    continue;
                }
                if (!CvOnlineTemplateContract.isSupportedSection(token)) {
                    errors.add("Unsupported section block: " + token);
                    continue;
                }
                sectionStack.push(token);
                openedSections.add(token);
                continue;
            }

            if (sectionStack.isEmpty()) {
                errors.add("Closing unopened section: " + token);
                continue;
            }

            String currentSection = sectionStack.pop();
            if (!currentSection.equals(token)) {
                errors.add("Mismatched closing section: expected " + currentSection + " but found " + token);
            }
        }

        while (!sectionStack.isEmpty()) {
            errors.add("Unclosed section block: " + sectionStack.pop());
        }

        return new TemplateValidationResult(rootPlaceholders, openedSections, errors);
    }

    public record TemplateValidationResult(
            Set<String> rootPlaceholders,
            Set<String> sections,
            List<String> errors) {

        public boolean isValid() {
            return errors == null || errors.isEmpty();
        }
    }
}
