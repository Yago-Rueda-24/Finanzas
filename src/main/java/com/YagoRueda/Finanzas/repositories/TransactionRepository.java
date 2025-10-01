package com.YagoRueda.Finanzas.repositories;

import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {

    List<TransactionEntity> findByUserAndDateBetween(UserEntity user, LocalDate start, LocalDate end);

}
