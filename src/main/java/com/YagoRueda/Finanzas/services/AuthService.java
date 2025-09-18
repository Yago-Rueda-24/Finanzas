package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.UserDTO;
import com.YagoRueda.Finanzas.entities.ApiKeyEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.repositories.ApiKeyRepository;
import com.YagoRueda.Finanzas.repositories.UserRepository;
import com.YagoRueda.Finanzas.utils.ApiKeyGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Servicio encargado de la operaciones de autenticación y autorizacion de la API
 * De momento crear usuarios y sirve API_keys
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;

    public AuthService(UserRepository userRepository, ApiKeyRepository apiKeyRepository) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    public UserEntity register(UserDTO dto) throws IllegalArgumentException {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El username ya esta en uso");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setCreated_at(Instant.now());
        return userRepository.save(user);
    }

    public ApiKeyEntity generateToken(UserDTO dto) throws IllegalArgumentException {
        System.out.println("1");
        if (!userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Usuario o passwords incorrectos");
        }
        UserEntity user = userRepository.findByUsername(dto.getUsername());
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("Usuario o passwords incorrectos");
        }
        System.out.println("2");
        ApiKeyEntity apiKey = new ApiKeyEntity();
        apiKey.setApiKey(ApiKeyGenerator.generateApiKey());
        apiKey.setUser(user);
        System.out.println("3");
        return apiKeyRepository.save(apiKey);

    }
}
