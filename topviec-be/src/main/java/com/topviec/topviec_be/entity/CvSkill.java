package com.topviec.topviec_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cv_skills", indexes = {
        @Index(name = "idx_cv_skills_cv_id", columnList = "cv_id"),
        @Index(name = "idx_cv_skills_skill_id", columnList = "skill_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skill_name_custom")
    private String skillNameCustom;

    @Column(name = "proficiency")
    private Integer proficiency;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
