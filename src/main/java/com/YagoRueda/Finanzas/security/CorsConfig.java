package com.YagoRueda.Finanzas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Solo el endpoint de registro permite CORS
                registry.addMapping("/auth/**")
                        .allowedOrigins("http://localhost:8080", "https://miapp.com")
                        .allowedMethods("POST")
                        .allowCredentials(false );
            }
        };
    }
}
