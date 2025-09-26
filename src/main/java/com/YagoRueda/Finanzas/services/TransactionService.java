package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.ErrorCsvException;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.exceptions.UnauthorizedOperationException;
import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.apache.catalina.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        if (dto.getAmount() == 0) {
            errors.put("amount", "La cantidad debe ser un número positivo o negativo distinto de 0");
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
        if (optionalentity.isEmpty()) {
            throw new IllegalArgumentException("ID invalido, transacción no encontrada");
        }
        TransactionEntity entity = optionalentity.get();
        UserEntity dbUser = entity.getUser();

        if (!dbUser.equals(owner)) {
            throw new UnauthorizedOperationException("No puede modificar un recurso del que no eres propietario");
        }

        entity.setDate(transaction.getDate());
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setCategory(transaction.getCategory());

        return transactionRepository.save(entity);
    }

    public void delete(UserEntity owner, long id) throws IllegalArgumentException, UnauthorizedOperationException {

        Optional<TransactionEntity> optionalentity = transactionRepository.findById(id);
        if (optionalentity.isEmpty()) {
            throw new IllegalArgumentException("ID invalido, transacción no encontrada");
        }

        TransactionEntity entity = optionalentity.get();
        UserEntity dbUser = entity.getUser();

        if (!dbUser.equals(owner)) {
            throw new UnauthorizedOperationException("No puede modificar un recurso del que no eres propietario");
        }

        transactionRepository.deleteById(id);

    }

    public List<Long> validateCsv(MultipartFile file) throws IllegalArgumentException, IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacio");
        }
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("El archivo debe tener extensión .csv");

        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<Long> errors = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (CSVRecord record : records) {
                String fechaStr = record.get("fecha");
                LocalDate fecha = LocalDate.parse(fechaStr, formatter);

                // Validar descripción
                String descripcion = record.get("descripcion");
                if (descripcion == null || descripcion.isBlank()) {
                    errors.add(record.getRecordNumber());
                }

                // Validar monto
                String montoStr = record.get("monto").replace(",", ".");
                if (Float.parseFloat(montoStr) == 0) {
                    errors.add(record.getRecordNumber());
                }


                // Validar categoría
                String categoria = record.get("categoria");
                if (categoria == null || categoria.isBlank()) {
                    errors.add(record.getRecordNumber());
                }
            }
            return errors;
        }
    }

    public void processCsv(UserEntity owner, MultipartFile file) throws IOException, IllegalArgumentException, ErrorCsvException {

        List<Long> errors = validateCsv(file);
        if (!errors.isEmpty()) {
            ErrorCsvException e = new ErrorCsvException("Errores parseando las lineas del csv");
            e.setErrors(errors);
            throw e;
        }


        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<TransactionEntity> transactions = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (CSVRecord record : records) {
                TransactionEntity tx = new TransactionEntity();
                tx.setDate(LocalDate.parse(record.get("fecha"), formatter));

                tx.setDescription(record.get("descripcion"));
                float amount = Float.parseFloat(record.get("monto").replace(',', '.'));
                tx.setAmount(amount);
                tx.setCategory(record.get("categoria"));
                tx.setUser(owner);
                tx.setCreated_at(Instant.now());
                transactions.add(tx);
            }

            transactionRepository.saveAll(transactions);
        }
    }
}
