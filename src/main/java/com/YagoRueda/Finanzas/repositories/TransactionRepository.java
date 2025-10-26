package com.YagoRueda.Finanzas.repositories;

import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {

    List<TransactionEntity> findByUserAndDateBetween(UserEntity user, LocalDate start, LocalDate end);

    @Query(" SELECT t FROM TransactionEntity t WHERE t.user = :user AND t.date BETWEEN :start AND :end ORDER BY t.amount DESC")
    List<TransactionEntity> findTopIncome(@Param("user") UserEntity user, @Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);
    @Query(" SELECT t FROM TransactionEntity t WHERE t.user = :user AND t.date BETWEEN :start AND :end ORDER BY t.amount ASC")
    List<TransactionEntity> findTopExpense(@Param("user") UserEntity user, @Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);

}
