package com.gatherdotech.fleetmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS/CSRF for stateless API
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ---- Swagger / API Docs ----
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // legacy swagger assets (harmless if not present)
                        .requestMatchers("/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // ---- H2 console (dev/test only) ----
                        .requestMatchers("/h2-console/**").permitAll()

                        // ---- Actuator ----
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // ---- API under test (open for local testing) ----
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight
                        .requestMatchers(HttpMethod.POST,   "/api/v1/drivers").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/v1/drivers", "/api/v1/drivers/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/drivers/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/drivers/**").permitAll()

                        .requestMatchers(HttpMethod.GET,    "/api/v1/vehicles", "/api/v1/vehicles/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/api/v1/vehicles").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/vehicles/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**").permitAll()

                        // Allow error/root
                        .requestMatchers("/", "/error").permitAll()

                        // Everything else requires auth
                        .anyRequest().authenticated()
                )

                // Stateless API
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Security headers
                .headers(headers -> headers
                        // H2 console uses frames (same origin only)
                        .frameOptions(frame -> frame.sameOrigin())
                        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )

                // Basic auth for any endpoints not explicitly permitted above
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Permissive CORS for local testing.
     * Tighten for staging/production (restrict origins, headers, and methods).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*")); // dev-only
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Location", "Content-Disposition"));
        cfg.setAllowCredentials(false);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
