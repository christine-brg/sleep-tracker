package org.example.sleeptracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException ex) {
            handleError(response, HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (AccessDeniedException ex) {
            handleError(response, HttpStatus.FORBIDDEN, "Access denied");
        } catch (AuthenticationException ex) {
            handleError(response, HttpStatus.UNAUTHORIZED, "Authentication failed");
        } catch (Exception ex) {
            handleError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", response.encodeRedirectURL(response.getHeader("Location")));

        objectMapper.writeValue(response.getWriter(), body);
    }
}
