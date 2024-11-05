package org.example.serverb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "Babebu";

    private Map<String, String> getSecretKeyHash(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String time = request.getHeader("Time");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(token) && StringUtils.hasText(time)) {
            Map<String, String> tokenTimeMap = new HashMap<>();
            tokenTimeMap.put("token", token);
            tokenTimeMap.put("time", time);
            return tokenTimeMap;
        }
        return null;
    }

    private String generateHash(String url, String time) throws Exception {
        String data = url + time + SECRET_KEY;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, String> jwt = getSecretKeyHash(request);

            if (jwt != null) {
                String token = jwt.get("token");
                String time = jwt.get("time");
//                System.out.println(token);
//                System.out.println(time);
//                System.out.println(new Date().getTime());
//                System.out.println(new Date().getTime()-Long.parseLong(time));

                // Check if the request is within 1 minute
                if (new Date().getTime() - Long.parseLong(time) < 60000) {
                    // Generate the hash to compare with the token
                    String url = request.getRequestURI();
//                    System.out.println("url"+url);
                    String expectedHash = generateHash(url, time);
//                    System.out.println(expectedHash);

                    if (expectedHash.equals(token)) {
                        System.out.println("Token is valid");

                        // Continue to the next filter
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
            }
            // If we reach here, the token is invalid or missing, so deny the request
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Invalid or missing token");
        } catch (Exception ex) {
            log.error("Failed to set user authentication", ex);
        }

    }
}

