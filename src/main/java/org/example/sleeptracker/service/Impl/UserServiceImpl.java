package org.example.sleeptracker.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sleeptracker.dto.*;
import org.example.sleeptracker.exceptions.PasswordMismatchException;
import org.example.sleeptracker.exceptions.ResourceNotFoundException;
import org.example.sleeptracker.exceptions.UnauthorizedUserException;
import org.example.sleeptracker.models.Device;
import org.example.sleeptracker.models.Goals;
import org.example.sleeptracker.models.SleepSessions;
import org.example.sleeptracker.models.User;
import org.example.sleeptracker.repository.DeviceRepository;
import org.example.sleeptracker.repository.GoalsRepository;
import org.example.sleeptracker.repository.SleepSessionsRepository;
import org.example.sleeptracker.repository.UserRepository;
import org.example.sleeptracker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GoalsRepository goalRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final SleepSessionsRepository sleepSessionsRepository;

    @Transactional
    @Override
    public UserDto updateUserInfo(String username, UpdateUserRequest updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("Updating user info for user {}", username);

        if(updatedUser.getEmail() != null && updatedUser.getEmail() != user.getEmail()) {
            user.setEmail(updatedUser.getEmail());
        }
        if(updatedUser.getUsername() != null && updatedUser.getUsername() != user.getUsername()) {
            user.setUsername(updatedUser.getUsername());
        }
        if(updatedUser.getPhoneNumber() != null && updatedUser.getPhoneNumber() != user.getPhoneNumber()) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        return UserDto.fromUser(userRepository.save(user));
    }

    @Override
    public UserDto getUserInfo(String frontUsername, String username) {
        User user = userRepository.findByUsername(frontUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User authUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if( !user.equals(authUser) ){
            throw new UnauthorizedUserException("Cannot access user data.");
        }
        log.info("Retrieving user info for user {}", username);
        return UserDto.fromUser(user);
    }

    @Override
    public UserDto changePassword(String username, PasswordChangeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Old password is incorrect");
        }
        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new PasswordMismatchException("New password cannot be old password.");
        }
        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Confirm password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Changing password for user {}", username);

        return UserDto.fromUser(user);
    }

    @Override
    public GoalsDto updateGoal(String username, UpdateGoalsRequest newGoal) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Goals goal = goalRepository.findByUser(user).orElse(new Goals());
        if(newGoal.getExpectedSleepHours() != null){
            goal.setExpectedSleepHours(newGoal.getExpectedSleepHours());
        }
        if(newGoal.getExpectedSleepScore() != null){
            goal.setExpectedSleepScore(newGoal.getExpectedSleepScore());
        }
        goal.setUser(user);
        goal.setCreatedAt(LocalDateTime.now());

        log.info("Updating goal for user {}", username);
        return GoalsDto.fromGoalsToDto(goalRepository.save(goal), user);
    }

    @Override
    public DeviceDto changeDevice(String username, DeviceDto newDevice) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Device device = deviceRepository.findByUser(user).orElse(new Device());
        device.setUser(user);
        device.setDeviceName(newDevice.getDeviceName());
        device.setModel(newDevice.getModel());
        device.setFirmwareVersion(newDevice.getFirmwareVersion());
        device.setRegisteredAt(LocalDateTime.now());

        log.info("Changing device for user {}", username);
        return DeviceDto.fromDevice(deviceRepository.save(device), user);
    }

    @Override
    public Page<SleepSessionsDto> getRecentSessions(String username, LocalDate fromDate, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return sleepSessionsRepository.findByUserIdAndDateAfter(user.getId(), fromDate, pageable);
    }

    @Override
    public Page<SleepSessionsDto> getAllSessions(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return sleepSessionsRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    public SleepSessionsDto saveSleepSession(String username, SleepSessionDto request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SleepSessions session = new SleepSessions();
        session.setUser(user);
        session.setDate(request.getDate());
        session.setStartHour(request.getStartHour());
        session.setEndHour(request.getEndHour());
        session.setSleepScore(request.getSleepScore());
        session.setTotalHours(request.getTotalHours());

        SleepSessions savedSession = sleepSessionsRepository.save(session);

        return SleepSessionsDto.builder()
                .date(savedSession.getDate())
                .startHour(savedSession.getStartHour())
                .endHour(savedSession.getEndHour())
                .sleepScore(savedSession.getSleepScore())
                .totalHours(savedSession.getTotalHours())
                .build();
    }

    @Override
    public SleepHoursAvgResponse getAverageSleepHoursForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        Double avg = sleepSessionsRepository.findAverageSleepHoursByUserId(user.getId());
        return SleepHoursAvgResponse.builder()
                .hours(avg != null ? avg : 0.0)
                .build();
    }

    @Override
    public LastSleepSessionScore getLatestSleepScore(String  username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        SleepSessions sessions = sleepSessionsRepository.findById(user.getId()).orElse(null);
        return LastSleepSessionScore.builder()
                .score(sessions == null ? 0 : sessions.getSleepScore())
                .build();
    }

    @Override
    public GoalsDto getGoals(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        Goals goals = goalRepository.findByUser(user).orElse(new Goals());
        return GoalsDto.builder()
                .expectedSleepHours(goals == null ? BigDecimal.valueOf(0.0) : goals.getExpectedSleepHours())
                .expectedSleepScore(goals == null ? 0 : goals.getExpectedSleepScore())
                .username(username)
                .createdAt(goals == null ? LocalDateTime.now() : goals.getCreatedAt())
                .build();
    }



}
