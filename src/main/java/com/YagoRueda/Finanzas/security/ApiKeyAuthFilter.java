package com.YagoRueda.Finanzas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Configuration
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "Authorization";
    private static final String EXPECTED_API_KEY = "$2a$12$C2Qs7Z4pvDwnya5HpbKiUe.dREN9K2g7aftl3A6oHQkSgH1.W92y."; // puedes cargarla de application.properties

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Excluir endpoints bajo /auth/**
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Validar API Key en header
        String authHeader = request.getHeader(API_KEY_HEADER);

        if (authHeader == null || !authHeader.equals(EXPECTED_API_KEY)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API Key inválida o ausente");
            return;
        }

        // 3. Continuar si pasa validación
        filterChain.doFilter(request, response);
    }
}
