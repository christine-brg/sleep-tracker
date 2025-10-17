package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpdateGoalsRequest {

    private BigDecimal expectedSleepHours;
    private Integer expectedSleepScore;

}
