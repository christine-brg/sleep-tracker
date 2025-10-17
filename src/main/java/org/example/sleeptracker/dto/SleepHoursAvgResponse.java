package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SleepHoursAvgResponse {
    private Double hours;
}
