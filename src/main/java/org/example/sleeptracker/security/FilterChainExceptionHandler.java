package org.example.sleeptracker.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.sleeptracker.dto.ErrorResponse;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .details(request.getRequestURI())
                    .build();
            response.getWriter().write(convertObjectToJson(errorResponse));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
