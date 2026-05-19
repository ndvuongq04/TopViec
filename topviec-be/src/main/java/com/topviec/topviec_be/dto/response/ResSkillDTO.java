package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResSkillDTO {

    private Long id;
    private String name;
    private String category;
    private List<String> aliases;
    private Integer usageCount;
    private Boolean isActive;
}