package com.zikpak.facecheck.security;

import com.zikpak.facecheck.security.filters.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.stream.Collectors;

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

    @Value("${security.require-ssl:false}")
    private boolean requireSsl;

    // берём список доменов из application.yml:
    // security.cors.allowed-origins=https://face-check.org,https://другой.домен
    @Value("${security.cors.allowed-origins}")
    private String allowedOriginsCsv;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                // Security Headers (дополнительно к твоему кастомному фильтру)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss ->
                                xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )
                        .contentTypeOptions(contentType -> {})
                )

                .authorizeHttpRequests(auth -> auth
                        // actuator и error (с учётом context-path /api/v1)
                        .requestMatchers("/api/v1/actuator/**", "/actuator/**", "/error").permitAll()

                        // публичные auth/документация (и с /api/v1, и без — на случай прямых маппингов)
                        .requestMatchers("/api/v1/auth/**", "/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/billing/webhook", "/billing/webhook").permitAll()
                        .requestMatchers("/api/v1/billing/**", "/billing/**").authenticated()



                        // твои правила для aws-reports/sales
                        .requestMatchers(HttpMethod.GET, "/api/v1/aws-reports/download", "/aws-reports/download").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/aws-reports/view", "/aws-reports/view").permitAll()
                        .requestMatchers("/api/v1/sales/**", "/sales/**").permitAll()
                        .requestMatchers("/api/v1/aws-reports/**", "/aws-reports/**").authenticated()
                        .requestMatchers("/api/v1/company/**", "/company/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/company/employees/*/fire").authenticated()

                        // защищённые области
                        .requestMatchers("/api/v1/company/**", "/company/**").authenticated()
                        .requestMatchers("/api/v1/user/**", "/user/**").authenticated()

                        // всё остальное — под аутентификацией
                        .anyRequest().authenticated()
                )

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)

                // Порядок фильтров
                .addFilterBefore(securityHeadersFilter, SecurityContextHolderFilter.class)   // 1
                .addFilterAfter(requestSizeLimitFilter, SecurityHeadersFilter.class)         // 2
                .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)      // 3
                .addFilterAfter(sqlFilter, XssFilter.class)                                  // 4
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // 5

        if (requireSsl) {
            http.requiresChannel(ch -> ch.anyRequest().requiresSecure());
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(allowedOriginsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origins); // строгий whitelist из ENV/YAML
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin","X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
