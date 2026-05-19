package com.topviec.topviec_be.cvonline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.exception.AppException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CvOnlineRenderModelMapper {

    private final ObjectMapper objectMapper;

    public CvOnlineRenderModelMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CvOnlineExtraDataDTO parseExtraData(String extraDataJson) {
        if (extraDataJson == null || extraDataJson.isBlank()) {
            return CvOnlineExtraDataDTO.empty();
        }

        try {
            return objectMapper.readValue(extraDataJson, CvOnlineExtraDataDTO.class);
        } catch (JsonProcessingException ex) {
            throw AppException.badRequest("Dữ liệu CV online không đúng schema JSON");
        }
    }

    public Map<String, Object> toRenderModel(String extraDataJson) {
        return toRenderModel(parseExtraData(extraDataJson));
    }

    public Map<String, Object> toRenderModel(CvOnlineExtraDataDTO extraData) {
        CvOnlineExtraDataDTO safeData = extraData == null ? CvOnlineExtraDataDTO.empty() : extraData;
        CvOnlineExtraDataDTO.PersonalInfo personalInfo = safeData.getPersonalInfo() == null
                ? new CvOnlineExtraDataDTO.PersonalInfo()
                : safeData.getPersonalInfo();

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("fullName", safe(personalInfo.getFullName()));
        model.put("headline", safe(personalInfo.getHeadline()));
        model.put("email", safe(personalInfo.getEmail()));
        model.put("phone", safe(personalInfo.getPhone()));
        model.put("address", safe(personalInfo.getAddress()));
        model.put("city", safe(personalInfo.getCity()));
        model.put("country", safe(personalInfo.getCountry()));
        model.put("website", safe(personalInfo.getWebsite()));
        model.put("linkedin", safe(personalInfo.getLinkedin()));
        model.put("github", safe(personalInfo.getGithub()));
        model.put("careerObjective", safe(safeData.getCareerObjective()));

        model.put("experiences", mapExperiences(safeData.getExperiences()));
        model.put("educations", mapEducations(safeData.getEducations()));
        model.put("skills", mapSkills(safeData.getSkills()));
        model.put("certifications", mapCertifications(safeData.getCertifications()));
        model.put("languages", mapLanguages(safeData.getLanguages()));
        model.put("projects", mapProjects(safeData.getProjects()));
        model.put("hobbies", mapHobbies(safeData.getHobbies()));
        model.put("awards", mapAwards(safeData.getAwards()));
        model.put("customSections", mapCustomSections(safeData.getCustomSections()));
        return model;
    }

    private List<Map<String, Object>> mapExperiences(List<CvOnlineExtraDataDTO.ExperienceItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.ExperienceItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("jobTitle", safe(item.getJobTitle()));
            mapped.put("company", safe(item.getCompany()));
            mapped.put("location", safe(item.getLocation()));
            mapped.put("startDate", safe(item.getStartDate()));
            mapped.put("endDate", safe(item.getEndDate()));
            mapped.put("isCurrent", item.getIsCurrent() != null && item.getIsCurrent());
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapEducations(List<CvOnlineExtraDataDTO.EducationItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.EducationItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("school", safe(item.getSchool()));
            mapped.put("degree", safe(item.getDegree()));
            mapped.put("fieldOfStudy", safe(item.getFieldOfStudy()));
            mapped.put("startDate", safe(item.getStartDate()));
            mapped.put("endDate", safe(item.getEndDate()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapSkills(List<CvOnlineExtraDataDTO.SkillItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.SkillItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("name", safe(item.getName()));
            mapped.put("level", safe(item.getLevel()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapCertifications(List<CvOnlineExtraDataDTO.CertificationItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.CertificationItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("name", safe(item.getName()));
            mapped.put("issuer", safe(item.getIssuer()));
            mapped.put("issuedAt", safe(item.getIssuedAt()));
            mapped.put("expiresAt", safe(item.getExpiresAt()));
            mapped.put("credentialId", safe(item.getCredentialId()));
            mapped.put("credentialUrl", safe(item.getCredentialUrl()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapLanguages(List<CvOnlineExtraDataDTO.LanguageItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.LanguageItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("name", safe(item.getName()));
            mapped.put("level", safe(item.getLevel()));
            mapped.put("certificate", safe(item.getCertificate()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapProjects(List<CvOnlineExtraDataDTO.ProjectItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.ProjectItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("name", safe(item.getName()));
            mapped.put("role", safe(item.getRole()));
            mapped.put("organization", safe(item.getOrganization()));
            mapped.put("startDate", safe(item.getStartDate()));
            mapped.put("endDate", safe(item.getEndDate()));
            mapped.put("projectUrl", safe(item.getProjectUrl()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapHobbies(List<CvOnlineExtraDataDTO.HobbyItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.HobbyItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("name", safe(item.getName()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapAwards(List<CvOnlineExtraDataDTO.AwardItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.AwardItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("title", safe(item.getTitle()));
            mapped.put("issuer", safe(item.getIssuer()));
            mapped.put("awardedAt", safe(item.getAwardedAt()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private List<Map<String, Object>> mapCustomSections(List<CvOnlineExtraDataDTO.CustomSectionItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (CvOnlineExtraDataDTO.CustomSectionItem item : items) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            mapped.put("sectionTitle", safe(item.getSectionTitle()));
            mapped.put("itemTitle", safe(item.getItemTitle()));
            mapped.put("itemSubtitle", safe(item.getItemSubtitle()));
            mapped.put("itemMeta", safe(item.getItemMeta()));
            mapped.put("description", safe(item.getDescription()));
            result.add(mapped);
        }
        return result;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
