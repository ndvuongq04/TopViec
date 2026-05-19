package com.topviec.topviec_be.dto.cvonline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvOnlineExtraDataDTO {

    @Valid
    @Builder.Default
    private PersonalInfo personalInfo = new PersonalInfo();

    @Size(max = 3000)
    private String careerObjective;

    @Valid
    @Builder.Default
    private List<ExperienceItem> experiences = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<EducationItem> educations = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<SkillItem> skills = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<CertificationItem> certifications = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<LanguageItem> languages = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<ProjectItem> projects = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<HobbyItem> hobbies = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<AwardItem> awards = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<CustomSectionItem> customSections = new ArrayList<>();

    public static CvOnlineExtraDataDTO empty() {
        return CvOnlineExtraDataDTO.builder().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalInfo {
        @Size(max = 255)
        private String fullName;

        @Size(max = 255)
        private String headline;

        @Size(max = 255)
        private String email;

        @Size(max = 50)
        private String phone;

        @Size(max = 255)
        private String address;

        @Size(max = 255)
        private String city;

        @Size(max = 255)
        private String country;

        @Size(max = 255)
        private String website;

        @Size(max = 255)
        private String linkedin;

        @Size(max = 255)
        private String github;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExperienceItem {
        @Size(max = 255)
        private String jobTitle;

        @Size(max = 255)
        private String company;

        @Size(max = 255)
        private String location;

        @Size(max = 50)
        private String startDate;

        @Size(max = 50)
        private String endDate;

        private Boolean isCurrent;

        @Size(max = 4000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EducationItem {
        @Size(max = 255)
        private String school;

        @Size(max = 255)
        private String degree;

        @Size(max = 255)
        private String fieldOfStudy;

        @Size(max = 50)
        private String startDate;

        @Size(max = 50)
        private String endDate;

        @Size(max = 4000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillItem {
        @Size(max = 255)
        private String name;

        @Size(max = 100)
        private String level;

        @Size(max = 1000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertificationItem {
        @Size(max = 255)
        private String name;

        @Size(max = 255)
        private String issuer;

        @Size(max = 50)
        private String issuedAt;

        @Size(max = 50)
        private String expiresAt;

        @Size(max = 255)
        private String credentialId;

        @Size(max = 255)
        private String credentialUrl;

        @Size(max = 2000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LanguageItem {
        @Size(max = 255)
        private String name;

        @Size(max = 100)
        private String level;

        @Size(max = 255)
        private String certificate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectItem {
        @Size(max = 255)
        private String name;

        @Size(max = 255)
        private String role;

        @Size(max = 255)
        private String organization;

        @Size(max = 50)
        private String startDate;

        @Size(max = 50)
        private String endDate;

        @Size(max = 255)
        private String projectUrl;

        @Size(max = 4000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HobbyItem {
        @Size(max = 255)
        private String name;

        @Size(max = 1000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AwardItem {
        @Size(max = 255)
        private String title;

        @Size(max = 255)
        private String issuer;

        @Size(max = 50)
        private String awardedAt;

        @Size(max = 2000)
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomSectionItem {
        @Size(max = 255)
        private String sectionTitle;

        @Size(max = 255)
        private String itemTitle;

        @Size(max = 255)
        private String itemSubtitle;

        @Size(max = 255)
        private String itemMeta;

        @Size(max = 4000)
        private String description;
    }
}
