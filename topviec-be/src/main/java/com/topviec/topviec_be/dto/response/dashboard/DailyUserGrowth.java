package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUserGrowth {
    private LocalDate date;
    private long candidateCount;
    private long employerCount;
}
