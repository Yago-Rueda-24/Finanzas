package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.exceptions.UnauthorizedOperationException;
import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    private Map<String, String> checkDTO(TransactionDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            errors.put("description", "La descripción no puede estar vacía");
        }

        if(dto.getAmount() == 0){
            errors.put("amount","La cantidad debe ser un número positivo o negativo distinto de 0");
        }

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            errors.put("category", "La categoría no puede estar vacía");
        }

        if (dto.getDate() == null) {
            errors.put("date", "La fecha no puede ser nula");
        }

        return errors;

    }

    public TransactionEntity create(UserEntity owner, TransactionDTO transaction) throws InputTransactionException {

        Map<String, String> errors = checkDTO(transaction);
        if (!errors.isEmpty()) {
            InputTransactionException e = new InputTransactionException("Error en los datos");
            e.setErrors(errors);
            throw e;
        }

        TransactionEntity entity = new TransactionEntity();
        entity.setUser(owner);
        entity.setCreated_at(Instant.now());

        entity.setDate(transaction.getDate());
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setCategory(transaction.getCategory());

        return transactionRepository.save(entity);

    }

    public TransactionEntity modify(UserEntity owner, TransactionDTO transaction, long id) throws InputTransactionException, IllegalArgumentException, UnauthorizedOperationException {

        Map<String, String> errors = checkDTO(transaction);
        if (!errors.isEmpty()) {
            InputTransactionException e = new InputTransactionException("Error en los datos");
            e.setErrors(errors);
            throw e;
        }


        Optional<TransactionEntity> optionalentity = transactionRepository.findById(id);
        if(optionalentity.isEmpty()){
            throw new IllegalArgumentException("ID invalido, transacción no encontrada");
        }
        TransactionEntity entity = optionalentity.get();
        UserEntity dbUser= entity.getUser();

        if (!dbUser.equals(owner)) {
            throw new UnauthorizedOperationException("No puede modificar un recurso del que no eres propietario");
        }

        entity.setDate(transaction.getDate());
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setCategory(transaction.getCategory());

        return transactionRepository.save(entity);
    }

    public void delete(UserEntity owner, long id) throws  IllegalArgumentException, UnauthorizedOperationException {

        Optional<TransactionEntity> optionalentity = transactionRepository.findById(id);
        if(optionalentity.isEmpty()){
            throw new IllegalArgumentException("ID invalido, transacción no encontrada");
        }

        TransactionEntity entity = optionalentity.get();
        UserEntity dbUser= entity.getUser();

        if (!dbUser.equals(owner)) {
            throw new UnauthorizedOperationException("No puede modificar un recurso del que no eres propietario");
        }

        transactionRepository.deleteById(id);

    }

}
