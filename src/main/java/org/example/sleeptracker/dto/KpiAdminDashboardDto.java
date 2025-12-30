package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiAdminDashboardDto {

    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersLastMonth;
    private Long totalUsers;

}
