package org.example.sleeptracker.security;

import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static SecurityUser create(User user) {
        return new SecurityUser(
                user.getId(),
                user.getPassword(),
                user.getUsername(),
                mapToGrantedAuthority(user.getRole())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthority(RoleEnum role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
