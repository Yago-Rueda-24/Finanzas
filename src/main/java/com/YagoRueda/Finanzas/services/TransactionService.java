package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    private Map<String,String> checkDTO(TransactionDTO dto){
        Map<String, String> errors = new HashMap<>();

        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            errors.put("description", "La descripción no puede estar vacía");
        }

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            errors.put("category", "La categoría no puede estar vacía");
        }

        if (dto.getDate() == null) {
            errors.put("date", "La fecha no puede ser nula");
        }

        return errors;

    }

    public TransactionEntity create(UserEntity owner, TransactionDTO transaction){

        Map<String, String> errors = checkDTO(transaction);
        if(!errors.isEmpty()){
            throw new IllegalArgumentException("Se han encontrado errores en el dto");
        }

    }

}
