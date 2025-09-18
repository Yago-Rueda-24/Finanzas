package com.YagoRueda.Finanzas.repositories;

import com.YagoRueda.Finanzas.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    boolean existsByUsername(String username);
    UserEntity findByUsername(String username);
}
