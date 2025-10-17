package org.example.sleeptracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sleeptracker.dto.*;
import org.example.sleeptracker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username, Authentication auth) {
        log.info("GET user with usernme {}", username);
        return ResponseEntity.ok(userService.getUserInfo(username, auth.getName()));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UpdateUserRequest request, Authentication auth) {
        log.info("UPDATE profile for user : {}", auth.getName());
        return ResponseEntity.ok(userService.updateUserInfo(auth.getName(), request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDto> changePassword(@RequestBody PasswordChangeRequest request, Authentication auth) {
        log.info("CHANGE password for user : {}", auth.getName());
        return ResponseEntity.ok(userService.changePassword(auth.getName(), request));
    }

    @PostMapping("/update-goals")
    public ResponseEntity<GoalsDto> updateGoals(@RequestBody UpdateGoalsRequest request, Authentication auth) {
        log.info("UPDATE goals for user : {}", auth.getName());
        return ResponseEntity.ok(userService.updateGoal(auth.getName(), request));
    }

    @PostMapping("/change-device")
    public ResponseEntity<DeviceDto> changeDevice(@RequestBody DeviceDto dto, Authentication auth) {
        log.info("CHANGE device for user : {}", auth.getName());
        return ResponseEntity.ok(userService.changeDevice(auth.getName(), dto));
    }

    @GetMapping("/sleep-sessions")
    public ResponseEntity<Page<SleepSessionsDto>> getSleepSessions(
            Authentication user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "date") String sortColumn,
            @RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date
    ) {
        LocalDate fromDate = (date != null) ? date : LocalDate.now().minusDays(8);

        Sort.Direction direction = sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortColumn));

        Page<SleepSessionsDto> sessions = userService.getRecentSessions(user.getName(), fromDate, pageable);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/all-sleep-sessions")
    public ResponseEntity<Page<SleepSessionsDto>> getSleepSessions(
            Authentication user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "date") String sortColumn,
            @RequestParam(defaultValue = "DESC") String sortOrder
    ) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortColumn));

        // Call service method that fetches all sessions for user with pagination (no date filter)
        Page<SleepSessionsDto> sessions = userService.getAllSessions(user.getName(), pageable);

        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/sleep-sessions")
    public  ResponseEntity<SleepSessionsDto> addSleepSession(@RequestBody SleepSessionDto dto, Authentication auth) {
        log.info("ADD Sleep session for user : {}", auth.getName());
        return ResponseEntity.ok(userService.saveSleepSession(auth.getName(), dto));
    }

    @GetMapping("/sleep-avg")
    public ResponseEntity<SleepHoursAvgResponse>  getSleepAvg(Authentication auth) {
        log.info("GET SleepAvg for user : {}", auth.getName());
        return ResponseEntity.ok(userService.getAverageSleepHoursForUser(auth.getName()));
    }

    @GetMapping("/last-score")
    public ResponseEntity<LastSleepSessionScore> getLastSleepSessionScore(Authentication auth) {
        log.info("GET LastSleepSessionScore for user : {}", auth.getName());
        return ResponseEntity.ok(userService.getLatestSleepScore(auth.getName()));
    }

    @GetMapping("/goals")
    public ResponseEntity<GoalsDto> getGoals(Authentication auth) {
        log.info("GET Goals for user : {}", auth.getName());
        return ResponseEntity.ok(userService.getGoals(auth.getName()));
    }


}
