package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResJobPostSkillDTO {

    private Long id;
    private String skillName;
    private Long skillId;
    private Boolean isRequired;
    private Integer proficiencyMin;
}
