package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LastSleepSessionScore {
    private Integer score;
}
