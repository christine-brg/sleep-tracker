package org.example.sleeptracker.repository;

import org.example.sleeptracker.dto.UserDto;
import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findAllByRoleAndUsername(RoleEnum role, String username);

    Page<UserDto> findAllByRole(RoleEnum role, Pageable pageable);

    long countByRole(RoleEnum role);

    long countByEnabledAndRole(boolean enabled, RoleEnum role);

    long countByRoleAndCreatedAtAfter(RoleEnum role, LocalDateTime createdAtAfter);

    List<User> findAllByRoleAndCreatedAtAfter(RoleEnum role, LocalDateTime createdAtAfter);

    List<User> findAllByRoleAndCreatedAtIsAfter(RoleEnum role, LocalDateTime createdAtAfter);

    boolean existsByEmail(String email);

    List<User> findAllByRole(RoleEnum role);
}
