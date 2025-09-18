package com.YagoRueda.Finanzas.repositories;

import com.YagoRueda.Finanzas.entities.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity,Long> {

    boolean existsByApiKey(String apikey);
}
