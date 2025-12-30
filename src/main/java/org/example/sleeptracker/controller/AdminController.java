package org.example.sleeptracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sleeptracker.dto.KpiAdminDashboardDto;
import org.example.sleeptracker.dto.SleepSessionsDto;
import org.example.sleeptracker.dto.UserDto;
import org.example.sleeptracker.service.AdminService;
import org.example.sleeptracker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            Authentication user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "email") String sortColumn,
            @RequestParam(defaultValue = "DESC") String sortOrder
    ) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortColumn));

        Page<UserDto> users = adminService.findAllUsers(user.getName(), pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KpiAdminDashboardDto> getAllUsers(Authentication user) {
        return ResponseEntity.ok(adminService.getKpiStatistics(user.getName()));
    }

    @GetMapping("/users-year")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsersbyLastYear(Authentication user) {
        return ResponseEntity.ok(adminService.getUsersByLastYear(user.getName()));
    }
}
