package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.models.Goals;
import org.example.sleeptracker.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GoalsDto {
    private BigDecimal expectedSleepHours;
    private Integer expectedSleepScore;
    private String username;
    private LocalDateTime createdAt;

    public static GoalsDto fromGoalsToDto(Goals goals, User user) {
        return GoalsDto.builder()
                .expectedSleepHours(goals.getExpectedSleepHours())
                .expectedSleepScore(goals.getExpectedSleepScore())
                .username(user.getUsername())
                .createdAt(goals.getCreatedAt())
                .build();
    }
}
