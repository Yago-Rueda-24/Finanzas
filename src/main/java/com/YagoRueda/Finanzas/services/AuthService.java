package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.UserDTO;
import com.YagoRueda.Finanzas.entities.ApiKeyEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.repositories.ApiKeyRepository;
import com.YagoRueda.Finanzas.repositories.UserRepository;
import com.YagoRueda.Finanzas.utils.ApiKeyGenerator;
import com.YagoRueda.Finanzas.utils.HashUtil;
import jakarta.transaction.Transactional;
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
    private final HashUtil hashser;

    public AuthService(UserRepository userRepository, ApiKeyRepository apiKeyRepository, HashUtil hashser) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.hashser = hashser;
    }

    public UserEntity register(UserDTO dto) throws IllegalArgumentException {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El username ya esta en uso");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(hashser.BCryptHash(dto.getPassword()));
        user.setCreated_at(Instant.now());
        return userRepository.save(user);
    }

    @Transactional
    public String generateToken(UserDTO dto) throws IllegalArgumentException {
        if (!userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Usuario o passwords incorrectos");
        }
        UserEntity user = userRepository.findByUsername(dto.getUsername());
        boolean password_correct = hashser.BCryptMatch(dto.getPassword(), user.getPassword());
        if (!password_correct) {
            throw new IllegalArgumentException("Usuario o passwords incorrectos");
        }

        apiKeyRepository.invalidateUserKeys(user);

        String api_key_no_hash = ApiKeyGenerator.generateApiKey();
        ApiKeyEntity apiKey = new ApiKeyEntity();
        apiKey.setApiKey(hashser.sha256Hash(api_key_no_hash));
        apiKey.setUser(user);
        apiKey.setValidated(true);
        apiKey.setCreatedAT(Instant.now());
        apiKeyRepository.save(apiKey);
        return api_key_no_hash;

    }

    public UserEntity validateApiKey(String apiKey) {

        String hashed_apiKey = hashser.sha256Hash(apiKey);

        if (!apiKeyRepository.existsByApiKey(hashed_apiKey)) {
            return null;
        }
        ApiKeyEntity userapiKey = apiKeyRepository.findByApiKey(hashed_apiKey);
        if (!userapiKey.isValidated()) {
            return null;
        }

        UserEntity user = apiKeyRepository.findUserByApiKey(hashed_apiKey);
        System.out.println(user.getUsername());
        return user;


    }
}
