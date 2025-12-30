package org.example.sleeptracker.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sleeptracker.dto.KpiAdminDashboardDto;
import org.example.sleeptracker.dto.UserDto;
import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.User;
import org.example.sleeptracker.repository.UserRepository;
import org.example.sleeptracker.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public Page<UserDto> findAllUsers(String username, Pageable pageable) {
        User admin = userRepository
                .findAllByRoleAndUsername(RoleEnum.ADMIN, username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> allUsers = userRepository.findAllByRole(RoleEnum.USER);

        List<UserDto> userDtos = allUsers.stream()
                .map(UserDto::fromUser)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userDtos.size());

        List<UserDto> pagedContent =
                start >= userDtos.size() ? List.of() : userDtos.subList(start, end);

        return new PageImpl<>(pagedContent, pageable, userDtos.size());
    }


    @Override
    public KpiAdminDashboardDto getKpiStatistics(String username) {
        User admin = userRepository.findAllByRoleAndUsername(RoleEnum.ADMIN, username).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        long totalActiveUsers = userRepository.countByEnabledAndRole(true, RoleEnum.USER);
        long totalInactiveUsers = userRepository.countByEnabledAndRole(false, RoleEnum.USER);
        long totalNewLastMonth = userRepository.countByRoleAndCreatedAtAfter(RoleEnum.USER, LocalDateTime.now().minusMonths(1));
        long totalUsers = userRepository.countByRole(RoleEnum.USER);

        return KpiAdminDashboardDto.builder()
                .activeUsers(totalActiveUsers)
                .inactiveUsers(totalInactiveUsers)
                .newUsersLastMonth(totalNewLastMonth)
                .totalUsers(totalUsers)
                .build();
    }

    @Override
    public List<UserDto> getUsersByLastYear(String username) {
        User admin = userRepository.findAllByRoleAndUsername(RoleEnum.ADMIN, username).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        List<User> allUsers = userRepository.findAllByRoleAndCreatedAtIsAfter(RoleEnum.USER, LocalDateTime.now().minusYears(1));
        return allUsers.stream()
                .map(UserDto::fromUserStatistic)
                .toList();
    }
}
