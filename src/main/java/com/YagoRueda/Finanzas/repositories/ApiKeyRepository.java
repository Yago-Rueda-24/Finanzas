package com.YagoRueda.Finanzas.repositories;

import com.YagoRueda.Finanzas.entities.ApiKeyEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity,Long> {

    boolean existsByApiKey(String apikey);
    @Query("SELECT a.user FROM ApiKeyEntity a WHERE a.apiKey = :apiKey")
    UserEntity findByApiKey(@Param("apiKey") String apiKey);
}
