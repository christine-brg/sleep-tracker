package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class SleepSessionDto {

        private LocalDate date;
        private LocalTime startHour;
        private LocalTime endHour;
        private Integer sleepScore;
        private BigDecimal totalHours;

}
