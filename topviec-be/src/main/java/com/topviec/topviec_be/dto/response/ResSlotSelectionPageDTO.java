package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResSlotSelectionPageDTO {

    private String companyName;
    private String jobTitle;
    private String roundName;
    private Integer roundNumber;
    private LocalDateTime deadline;
    private List<ResInterviewSlotDTO> slots;
}
