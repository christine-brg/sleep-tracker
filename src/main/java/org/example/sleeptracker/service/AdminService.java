package org.example.sleeptracker.service;

import org.example.sleeptracker.dto.KpiAdminDashboardDto;
import org.example.sleeptracker.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<UserDto> findAllUsers(String username, Pageable pageable);

    KpiAdminDashboardDto getKpiStatistics(String username);

    List<UserDto> getUsersByLastYear(String username);

}
