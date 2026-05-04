package com.liuxuanhui.aicodehelper.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOriginPattern("*");
                config.addAllowedMethod("*");
                config.addAllowedHeader("*");
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/exam/public/**", "/public/**").hasAnyAuthority("student", "teacher", "admin")
                .requestMatchers("/exam/student/**", "/student/**").hasAnyAuthority("student", "admin", "teacher")
                .requestMatchers("/exam/teacher/**", "/teacher/**").hasAnyAuthority("teacher", "admin")
                .requestMatchers("/exam/admin/**", "/admin/**").hasAnyAuthority("admin")
                .requestMatchers(
                    "/exam/util/**", "/exam/common/**",
                    "/util/**", "/common/**",
                    "/api/**", "/ai/**",
                    "/files/**",
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new TokenAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .authenticationEntryPoint(new MyAuthenticationEntryPointHandler())
            )
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
