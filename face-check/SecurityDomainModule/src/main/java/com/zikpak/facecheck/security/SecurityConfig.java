package com.zikpak.facecheck.security;

import com.zikpak.facecheck.security.filters.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final SqlFilter sqlFilter;
    private final JwtFilter jwtAuthFilter;
    private final XssFilter xssFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final RequestSizeLimitFilter requestSizeLimitFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                // Security Headers через Spring Security (дополнительно к фильтру)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss ->
                                xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )                        .contentTypeOptions(contentType -> {})
                )

                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/v1/actuator/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/error").permitAll()

                                // aws-reports endpoints
                                .requestMatchers(HttpMethod.GET, "/aws-reports/download").permitAll()
                                .requestMatchers(HttpMethod.GET, "/aws-reports/view").permitAll()
                                .requestMatchers("/sales/**").permitAll()
                                .requestMatchers("/aws-reports/**").authenticated()

                                .requestMatchers("/company/**").authenticated()
                                .requestMatchers("/user/**").authenticated()
                                .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)

                // ВАЖНО: Порядок фильтров имеет значение!
                // 1. Security Headers - первым для всех запросов
                .addFilterBefore(securityHeadersFilter, SecurityContextHolderFilter.class)
                // 2. Request Size Limit - проверка размера
                .addFilterAfter(requestSizeLimitFilter, SecurityHeadersFilter.class)
                // 3. XSS Filter
                .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
                // 4. SQL Filter
                .addFilterAfter(sqlFilter, XssFilter.class)
                // 5. JWT Filter - последним перед аутентификацией
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}