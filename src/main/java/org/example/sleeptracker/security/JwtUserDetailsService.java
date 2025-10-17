package org.example.sleeptracker.security;

import lombok.RequiredArgsConstructor;
import org.example.sleeptracker.models.User;
import org.example.sleeptracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username).orElse(null);

        return user != null ? JwtUserFactory.create(user) : getUsername(username);
    }

    private UserDetails getUsername(String username){
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + username + " not found"));

        return JwtUserFactory.create(user);
    }
}
