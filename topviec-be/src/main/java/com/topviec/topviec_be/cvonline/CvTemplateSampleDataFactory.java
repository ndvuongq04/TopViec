package com.topviec.topviec_be.cvonline;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CvTemplateSampleDataFactory {

    public CvOnlineExtraDataDTO createSampleData() {
        return CvOnlineExtraDataDTO.builder()
                .personalInfo(CvOnlineExtraDataDTO.PersonalInfo.builder()
                        .fullName("Nguyen Van A")
                        .headline("Senior Frontend Developer")
                        .email("nguyenvana@example.com")
                        .phone("0909 123 456")
                        .address("12 Nguyen Hue")
                        .city("Ho Chi Minh")
                        .country("Viet Nam")
                        .website("https://portfolio.example.com")
                        .linkedin("https://linkedin.com/in/nguyenvana")
                        .github("https://github.com/nguyenvana")
                        .build())
                .careerObjective("Mong muốn xây dựng sản phẩm ổn định, có trải nghiệm người dùng tốt và dễ mở rộng về lâu dài.")
                .experiences(List.of(
                        CvOnlineExtraDataDTO.ExperienceItem.builder()
                                .jobTitle("Senior Frontend Developer")
                                .company("TopViec")
                                .location("Ho Chi Minh")
                                .startDate("01/2023")
                                .endDate("Hien tai")
                                .isCurrent(true)
                                .description("Phụ trách phát triển hệ thống CV online, tối ưu hiệu năng và chuẩn hóa component.")
                                .build()))
                .educations(List.of(
                        CvOnlineExtraDataDTO.EducationItem.builder()
                                .school("Dai hoc Bach Khoa")
                                .degree("Ky su")
                                .fieldOfStudy("Cong nghe thong tin")
                                .startDate("09/2016")
                                .endDate("06/2021")
                                .description("Tập trung vào phát triển phần mềm và hệ thống web.")
                                .build()))
                .skills(List.of(
                        CvOnlineExtraDataDTO.SkillItem.builder()
                                .name("Vue 3")
                                .level("Advanced")
                                .description("Xây dựng SPA, state management, tối ưu render.")
                                .build(),
                        CvOnlineExtraDataDTO.SkillItem.builder()
                                .name("TypeScript")
                                .level("Advanced")
                                .description("Thiết kế type-safe contract giữa FE và BE.")
                                .build()))
                .certifications(List.of(
                        CvOnlineExtraDataDTO.CertificationItem.builder()
                                .name("AWS Cloud Practitioner")
                                .issuer("AWS")
                                .issuedAt("2024")
                                .description("Nắm vững kiến thức cơ bản về cloud và triển khai hệ thống.")
                                .build()))
                .languages(List.of(
                        CvOnlineExtraDataDTO.LanguageItem.builder()
                                .name("English")
                                .level("Professional Working Proficiency")
                                .certificate("IELTS 7.0")
                                .build()))
                .projects(List.of(
                        CvOnlineExtraDataDTO.ProjectItem.builder()
                                .name("CV Online Platform")
                                .role("Technical Lead")
                                .organization("TopViec")
                                .startDate("02/2025")
                                .endDate("05/2026")
                                .projectUrl("https://topviec.example.com")
                                .description("Thiết kế và triển khai hệ thống CV online từ editor đến PDF export.")
                                .build()))
                .hobbies(List.of(
                        CvOnlineExtraDataDTO.HobbyItem.builder()
                                .name("Writing Technical Blogs")
                                .description("Chia sẻ kinh nghiệm xây dựng hệ thống web và tối ưu frontend.")
                                .build()))
                .awards(List.of(
                        CvOnlineExtraDataDTO.AwardItem.builder()
                                .title("Top Performer")
                                .issuer("TopViec Engineering")
                                .awardedAt("2025")
                                .description("Được ghi nhận vì dẫn dắt thành công dự án CV online.")
                                .build()))
                .customSections(List.of(
                        CvOnlineExtraDataDTO.CustomSectionItem.builder()
                                .sectionTitle("Community")
                                .itemTitle("Frontend Mentor")
                                .itemSubtitle("Internal Guild")
                                .itemMeta("2024 - 2026")
                                .description("Hướng dẫn thành viên mới về Vue 3, TypeScript và code review.")
                                .build()))
                .build();
    }
}
