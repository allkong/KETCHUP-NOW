package com.ssafy.double_bean.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.double_bean.auth.service.AuthService;
import com.ssafy.double_bean.config.WebSecurityConfig;
import com.ssafy.double_bean.exception.ErrorCode;
import com.ssafy.double_bean.exception.ErrorResponse;
import com.ssafy.double_bean.exception.HttpResponseException;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final WebSecurityConfig webSecurityConfig;
    private final AuthService authService;
    private final AuthenticatedUser requestedUser;

    public JwtFilter(WebSecurityConfig webSecurityConfig, AuthService authService, AuthenticatedUser requestedUser) {
        this.webSecurityConfig = webSecurityConfig;
        this.authService = authService;
        this.requestedUser = requestedUser;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = authService.resolveToken(request);
        String requestUri = request.getRequestURI();

        try {
            if (!webSecurityConfig.isAnonymousAllowed(requestUri) && StringUtils.hasText(jwt)) {
                // Request scoped Bean으로 간편하게 사용자 정보에 접근하기 위해 사용
                UUID uuid = authService.validateAndGetUuid(jwt, AuthService.TokenType.ACCESS);
                if (uuid != null) {
                    requestedUser.setUuid(uuid);
                    // UsernamePasswordAuthenticationFilter를 통과하기 위해 반드시 필요
                    Authentication authentication = authService.getAuthentication(jwt, uuid.toString());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (HttpResponseException e) {
            ErrorCode errorCode = e.getErrorCode();
            response.setStatus(errorCode.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(errorCode.getDetailCode(), errorCode.getDetailMessage())));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("", "Something went wrong while parsing token.")));
            return;
        }

        chain.doFilter(request, response);
    }
}
