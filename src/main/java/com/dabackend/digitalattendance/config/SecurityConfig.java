package com.dabackend.digitalattendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Bean exposes this method's return value as a managed Spring component.
    // This is the standard password encoder for securely hashing passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This bean defines all the security rules for our API.
    // Inside the SecurityConfig.java file

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ... (cors, csrf, sessionManagement are the same)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ADD THIS LINE TO DISABLE THE DEFAULT LOGIN POPUP
                .httpBasic(httpBasic -> httpBasic.disable())
                // Define authorization rules for different API endpoints.
                .authorizeHttpRequests(auth -> auth
                        // Permit all requests to our authentication endpoints (e.g., /register, /login).
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Any other request that comes into the application must be authenticated.
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // This bean configures CORS (Cross-Origin Resource Sharing).
    // It's crucial for allowing our frontend (running on localhost:3000)
    // to make API calls to our backend (running on localhost:8080).
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from our frontend's origin.
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // Allow all standard HTTP methods.
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow specific headers that the frontend might send.
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths in our API ("/**").
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}