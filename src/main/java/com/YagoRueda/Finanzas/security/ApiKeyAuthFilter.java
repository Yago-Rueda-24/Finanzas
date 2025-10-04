package com.YagoRueda.Finanzas.security;

import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final AuthService authservice ;
    private static final String API_KEY_HEADER = "Authorization";
    private static final String EXPECTED_API_KEY = "$2a$12$C2Qs7Z4pvDwnya5HpbKiUe.dREN9K2g7aftl3A6oHQkSgH1.W92y."; // puedes cargarla de application.properties

    public ApiKeyAuthFilter(AuthService authservice) {
        this.authservice = authservice;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Excluir endpoints bajo /auth/**
        if (path.startsWith("/auth/")            // endpoints de login/register
                || path.equals("/")              // raíz
                || path.equals("/index.html")    // index
                || path.startsWith("/css/")      // CSS
                || path.startsWith("/js/")       // JS
                || path.startsWith("/images/")   // imágenes
                || path.equals("/favicon.ico")) { // icono
            filterChain.doFilter(request, response);
            return;
        }

        // Validar que exista token en el header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token faltante");
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "

        // Delegar validación al servicio
        UserEntity user = authservice.validateApiKey(token);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }

        // Si quieres, puedes guardar el usuario en el request para usarlo en controllers
        request.setAttribute("authenticatedUser", user);

        // Continuar con la petición
        filterChain.doFilter(request, response);
    }
}
