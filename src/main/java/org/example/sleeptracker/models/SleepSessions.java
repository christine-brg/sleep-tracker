package org.example.sleeptracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sleep_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SleepSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_session_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @Column(name = "sleep_score")
    private Integer sleepScore;

    @Column(name = "total_hours")
    private BigDecimal totalHours;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

