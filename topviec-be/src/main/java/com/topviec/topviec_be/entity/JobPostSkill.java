package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_post_skills", uniqueConstraints = @UniqueConstraint(columnNames = { "job_post_id", "skill_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    // Mức độ thành thạo tối thiểu theo thang 1-5. NULL = không yêu cầu mức cụ thể
    @Column(name = "proficiency_min")
    private Integer proficiencyMin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", insertable = false, updatable = false)
    private JobPosting jobPosting;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    // private Skill skill;
}