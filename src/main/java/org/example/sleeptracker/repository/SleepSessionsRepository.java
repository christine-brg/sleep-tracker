package org.example.sleeptracker.repository;

import org.example.sleeptracker.dto.SleepSessionsDto;
import org.example.sleeptracker.models.SleepSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SleepSessionsRepository extends JpaRepository<SleepSessions, Long> {
    Page<SleepSessionsDto> findByUserIdAndDateAfter(Long userId, LocalDate dateAfter, Pageable attr1);

    Page<SleepSessionsDto> findByUserId(Long userId, Pageable attr1);

    @Query("SELECT AVG(s.totalHours) FROM SleepSessions s WHERE s.user.id = :userId")
    Double findAverageSleepHoursByUserId(@Param("userId") Long userId);

    @Query("SELECT s.sleepScore FROM SleepSessions s WHERE s.user.id = :userId ORDER BY s.date DESC, s.endHour DESC LIMIT 1")
    Optional<Integer> findLatestSleepScoreByUserId(@Param("userId") Long userId);
}
