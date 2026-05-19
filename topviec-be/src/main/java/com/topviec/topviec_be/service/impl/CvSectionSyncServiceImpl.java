package com.topviec.topviec_be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.entity.CvCertification;
import com.topviec.topviec_be.entity.CvEducation;
import com.topviec.topviec_be.entity.CvExperience;
import com.topviec.topviec_be.entity.CvLanguage;
import com.topviec.topviec_be.entity.CvSkill;
import com.topviec.topviec_be.entity.Cvs;
import com.topviec.topviec_be.entity.Skill;
import com.topviec.topviec_be.repository.CvCertificationRepository;
import com.topviec.topviec_be.repository.CvEducationRepository;
import com.topviec.topviec_be.repository.CvExperienceRepository;
import com.topviec.topviec_be.repository.CvLanguageRepository;
import com.topviec.topviec_be.repository.CvSkillRepository;
import com.topviec.topviec_be.repository.SkillRepository;
import com.topviec.topviec_be.service.CvSectionSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvSectionSyncServiceImpl implements CvSectionSyncService {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM"),
            DateTimeFormatter.ofPattern("yyyy"));

    private final CvEducationRepository cvEducationRepository;
    private final CvExperienceRepository cvExperienceRepository;
    private final CvSkillRepository cvSkillRepository;
    private final CvCertificationRepository cvCertificationRepository;
    private final CvLanguageRepository cvLanguageRepository;
    private final SkillRepository skillRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public boolean syncSectionsIfChanged(Cvs cv, CvOnlineExtraDataDTO extraData) {
        if (cv == null || cv.getId() == null) {
            return false;
        }

        CvOnlineExtraDataDTO safeData = extraData == null ? CvOnlineExtraDataDTO.empty() : extraData;
        String sectionHash = calculateSectionHash(safeData);
        if (sectionHash.equals(cv.getCvSectionHash())) {
            return false;
        }

        deleteSections(cv.getId());
        saveSections(cv.getId(), safeData);
        cv.setCvSectionHash(sectionHash);
        return true;
    }

    @Override
    public String calculateSectionHash(CvOnlineExtraDataDTO extraData) {
        return hashSections(extraData == null ? CvOnlineExtraDataDTO.empty() : extraData);
    }

    @Override
    @Transactional
    public void syncExperiences(Long cvId, List<CvOnlineExtraDataDTO.ExperienceItem> items) {
        cvExperienceRepository.deleteByCvId(cvId);
        cvExperienceRepository.saveAll(mapExperiences(cvId, items));
    }

    @Override
    @Transactional
    public void syncEducations(Long cvId, List<CvOnlineExtraDataDTO.EducationItem> items) {
        cvEducationRepository.deleteByCvId(cvId);
        cvEducationRepository.saveAll(mapEducations(cvId, items));
    }

    @Override
    @Transactional
    public void syncSkills(Long cvId, List<CvOnlineExtraDataDTO.SkillItem> items) {
        cvSkillRepository.deleteByCvId(cvId);
        cvSkillRepository.saveAll(mapSkills(cvId, items));
    }

    @Override
    @Transactional
    public void syncCertifications(Long cvId, List<CvOnlineExtraDataDTO.CertificationItem> items) {
        cvCertificationRepository.deleteByCvId(cvId);
        cvCertificationRepository.saveAll(mapCertifications(cvId, items));
    }

    @Override
    @Transactional
    public void syncLanguages(Long cvId, List<CvOnlineExtraDataDTO.LanguageItem> items) {
        cvLanguageRepository.deleteByCvId(cvId);
        cvLanguageRepository.saveAll(mapLanguages(cvId, items));
    }

    @Override
    @Transactional
    public void deleteSections(Long cvId) {
        if (cvId == null) {
            return;
        }
        cvEducationRepository.deleteByCvId(cvId);
        cvExperienceRepository.deleteByCvId(cvId);
        cvSkillRepository.deleteByCvId(cvId);
        cvCertificationRepository.deleteByCvId(cvId);
        cvLanguageRepository.deleteByCvId(cvId);
    }

    private void saveSections(Long cvId, CvOnlineExtraDataDTO data) {
        cvExperienceRepository.saveAll(mapExperiences(cvId, data.getExperiences()));
        cvEducationRepository.saveAll(mapEducations(cvId, data.getEducations()));
        cvSkillRepository.saveAll(mapSkills(cvId, data.getSkills()));
        cvCertificationRepository.saveAll(mapCertifications(cvId, data.getCertifications()));
        cvLanguageRepository.saveAll(mapLanguages(cvId, data.getLanguages()));
    }

    private List<CvExperience> mapExperiences(Long cvId, List<CvOnlineExtraDataDTO.ExperienceItem> items) {
        List<CvExperience> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (int i = 0; i < items.size(); i++) {
            CvOnlineExtraDataDTO.ExperienceItem item = items.get(i);
            if (item == null || isBlank(item.getCompany(), item.getJobTitle(), item.getStartDate(), item.getEndDate(),
                    item.getDescription())) {
                continue;
            }

            result.add(CvExperience.builder()
                    .cvId(cvId)
                    .companyName(trimToNull(item.getCompany()))
                    .position(trimToNull(item.getJobTitle()))
                    .startDate(parseDateTime(item.getStartDate()))
                    .endDate(Boolean.TRUE.equals(item.getIsCurrent()) ? null : parseDateTime(item.getEndDate()))
                    .isCurrent(item.getIsCurrent())
                    .description(trimToNull(item.getDescription()))
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    private List<CvEducation> mapEducations(Long cvId, List<CvOnlineExtraDataDTO.EducationItem> items) {
        List<CvEducation> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (int i = 0; i < items.size(); i++) {
            CvOnlineExtraDataDTO.EducationItem item = items.get(i);
            if (item == null || isBlank(item.getSchool(), item.getDegree(), item.getFieldOfStudy(),
                    item.getStartDate(), item.getEndDate(), item.getDescription())) {
                continue;
            }

            result.add(CvEducation.builder()
                    .cvId(cvId)
                    .schoolName(trimToNull(item.getSchool()))
                    .degree(trimToNull(item.getDegree()))
                    .major(trimToNull(item.getFieldOfStudy()))
                    .startYear(parseYear(item.getStartDate()))
                    .endYear(parseYear(item.getEndDate()))
                    .gpa(null)
                    .description(trimToNull(item.getDescription()))
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    private List<CvSkill> mapSkills(Long cvId, List<CvOnlineExtraDataDTO.SkillItem> items) {
        List<CvSkill> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        Map<String, Skill> skillsByLowerName = loadSkillsByLowerName(items);
        for (int i = 0; i < items.size(); i++) {
            CvOnlineExtraDataDTO.SkillItem item = items.get(i);
            if (item == null || isBlank(item.getName(), item.getLevel(), item.getDescription())) {
                continue;
            }

            String skillName = trimToNull(item.getName());
            Skill matchedSkill = skillName == null ? null : skillsByLowerName.get(normalizeKey(skillName));
            result.add(CvSkill.builder()
                    .cvId(cvId)
                    .skillId(matchedSkill != null ? matchedSkill.getId() : null)
                    .skillNameCustom(matchedSkill == null ? skillName : null)
                    .proficiency(parseProficiency(item.getLevel()))
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    private List<CvCertification> mapCertifications(Long cvId, List<CvOnlineExtraDataDTO.CertificationItem> items) {
        List<CvCertification> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (int i = 0; i < items.size(); i++) {
            CvOnlineExtraDataDTO.CertificationItem item = items.get(i);
            if (item == null || isBlank(item.getName(), item.getIssuer(), item.getIssuedAt(), item.getExpiresAt(),
                    item.getCredentialUrl(), item.getDescription())) {
                continue;
            }

            result.add(CvCertification.builder()
                    .cvId(cvId)
                    .name(trimToNull(item.getName()))
                    .issuer(trimToNull(item.getIssuer()))
                    .issuedDate(parseDateTime(item.getIssuedAt()))
                    .expiredDate(parseDateTime(item.getExpiresAt()))
                    .credentialUrl(trimToNull(item.getCredentialUrl()))
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    private List<CvLanguage> mapLanguages(Long cvId, List<CvOnlineExtraDataDTO.LanguageItem> items) {
        List<CvLanguage> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (int i = 0; i < items.size(); i++) {
            CvOnlineExtraDataDTO.LanguageItem item = items.get(i);
            if (item == null || isBlank(item.getName(), item.getLevel(), item.getCertificate())) {
                continue;
            }

            result.add(CvLanguage.builder()
                    .cvId(cvId)
                    .language(trimToNull(item.getName()))
                    .proficiency(trimToNull(item.getLevel()))
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    private Map<String, Skill> loadSkillsByLowerName(List<CvOnlineExtraDataDTO.SkillItem> items) {
        Set<String> names = items.stream()
                .filter(Objects::nonNull)
                .map(CvOnlineExtraDataDTO.SkillItem::getName)
                .map(this::trimToNull)
                .filter(Objects::nonNull)
                .map(this::normalizeKey)
                .collect(Collectors.toSet());
        if (names.isEmpty()) {
            return Map.of();
        }
        return skillRepository.findActiveByLowerNameIn(names).stream()
                .collect(Collectors.toMap(skill -> normalizeKey(skill.getName()), Function.identity(), (a, b) -> a));
    }

    private String hashSections(CvOnlineExtraDataDTO data) {
        try {
            return sha256Hex(objectMapper.writeValueAsString(toHashModel(data)));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Khong the tao hash cv_section", ex);
        }
    }

    private Map<String, Object> toHashModel(CvOnlineExtraDataDTO data) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("experiences", normalizeList(data.getExperiences()));
        model.put("educations", normalizeList(data.getEducations()));
        model.put("skills", normalizeList(data.getSkills()));
        model.put("certifications", normalizeList(data.getCertifications()));
        model.put("languages", normalizeList(data.getLanguages()));
        return model;
    }

    private List<Object> normalizeList(List<?> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream().map(this::normalizeObject).toList();
    }

    private Object normalizeObject(Object value) {
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(value, LinkedHashMap.class);
    }

    private LocalDateTime parseDateTime(String value) {
        String text = trimToNull(value);
        if (text == null || isCurrentText(text)) {
            return null;
        }

        // FE hiện gửi date dạng text để render CV. BE cố parse các format phổ biến,
        // nếu không parse được thì trả null để lưu CV không bị fail.
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            LocalDateTime parsed = parseWithFormatter(text, formatter);
            if (parsed != null) {
                return parsed;
            }
        }
        return null;
    }

    private LocalDateTime parseWithFormatter(String text, DateTimeFormatter formatter) {
        try {
            if (formatter == DateTimeFormatter.ISO_LOCAL_DATE || formatter.toString().contains("DayOfMonth")) {
                return LocalDate.parse(text, formatter).atStartOfDay();
            }
            if (formatter.toString().contains("MonthOfYear")) {
                return YearMonth.parse(text, formatter).atDay(1).atStartOfDay();
            }
            return Year.parse(text, formatter).atDay(1).atStartOfDay();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Integer parseYear(String value) {
        String text = trimToNull(value);
        if (text == null || isCurrentText(text)) {
            return null;
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(19|20)\\d{2}").matcher(text);
        if (!matcher.find()) {
            return null;
        }
        return Integer.parseInt(matcher.group());
    }

    private Integer parseProficiency(String value) {
        String text = trimToNull(value);
        if (text == null) {
            return null;
        }

        try {
            int numeric = Integer.parseInt(text);
            return numeric >= 1 && numeric <= 5 ? numeric : null;
        } catch (NumberFormatException ignored) {
            String normalized = text.toLowerCase(Locale.ROOT);
            if (normalized.contains("expert") || normalized.contains("chuyen gia")) {
                return 5;
            }
            if (normalized.contains("advanced") || normalized.contains("nang cao")) {
                return 4;
            }
            if (normalized.contains("intermediate") || normalized.contains("trung cap")) {
                return 3;
            }
            if (normalized.contains("basic") || normalized.contains("co ban")) {
                return 1;
            }
            return null;
        }
    }

    private boolean isCurrentText(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.contains("hien tai")
                || normalized.contains("hiện tại")
                || normalized.contains("present")
                || normalized.contains("current");
    }

    private boolean isBlank(String... values) {
        for (String value : values) {
            if (trimToNull(value) != null) {
                return false;
            }
        }
        return true;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte item : bytes) {
                builder.append(String.format("%02x", item & 0xff));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not supported", ex);
        }
    }
}
