package com.ssafy.double_bean.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration defaultRegistry = registry.addMapping("/**")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type", "Access-Control-Allow-Origin")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Access-Control-Allow-Headers").maxAge(3600);

        if (activeProfile.equals("dev")) {
            defaultRegistry.allowedOriginPatterns("http://localhost:3030/");
        } else if (activeProfile.equals("prod")) {
            defaultRegistry.allowedOriginPatterns("http://localhost:3030/", "http://home.pcjs156.net/");
        }
    }
}
