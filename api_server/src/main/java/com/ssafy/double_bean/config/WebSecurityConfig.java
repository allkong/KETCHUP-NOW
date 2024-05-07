package com.ssafy.double_bean.config;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ssafy.double_bean.auth.service.AuthService;
import com.ssafy.double_bean.filter.JwtFilter;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final AuthService authService;
    private final AuthenticatedUser authenticatedUser;

    private final String[] swaggerPaths = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/**"
    };

    private final String[] anonymousAllowedPaths = {
            "/api/v1/users/sign-up", "/api/v1/auth/login", "/api/v1/auth/token", "/api/v1/attractions/**"
    };

    public WebSecurityConfig(AuthService authService, AuthenticatedUser authenticatedUser) {
        this.authService = authService;
        this.authenticatedUser = authenticatedUser;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                // 인증이 필요한 URL로 요청이 오면, Request scope인 AuthenticatedUser bean을 초기화
                .addFilterBefore(new JwtFilter(this, authService, authenticatedUser), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(swaggerPaths).permitAll()
                        .requestMatchers(anonymousAllowedPaths).permitAll()
                        .anyRequest().authenticated())
                // Token 기반 요청이므로 불필요
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return security.build();
    }

    public boolean isAnonymousAllowed(String uri) {
        Stream<String> swaggerPathStream = Arrays.stream(swaggerPaths);
        Stream<String> businessPathStream = Arrays.stream(anonymousAllowedPaths);
        return Stream.concat(swaggerPathStream, businessPathStream).anyMatch(path -> path.equals(uri));
    }
}
