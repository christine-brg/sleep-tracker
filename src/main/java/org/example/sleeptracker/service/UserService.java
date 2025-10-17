package org.example.sleeptracker.service;

import org.example.sleeptracker.dto.*;
import org.example.sleeptracker.models.Goals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {

    UserDto updateUserInfo(String username, UpdateUserRequest updatedUser);

    UserDto getUserInfo(String username1, String username);

    UserDto changePassword(String username, PasswordChangeRequest request);

    GoalsDto updateGoal(String username, UpdateGoalsRequest newGoal);

    DeviceDto changeDevice(String username, DeviceDto newDevice);

    Page<SleepSessionsDto> getRecentSessions(String username, LocalDate fromDate, Pageable pageable);

    Page<SleepSessionsDto> getAllSessions(String username, Pageable pageable);

    SleepSessionsDto saveSleepSession(String username, SleepSessionDto request);

    SleepHoursAvgResponse getAverageSleepHoursForUser(String username);

    LastSleepSessionScore getLatestSleepScore(String  username);

    GoalsDto getGoals(String username);
}
