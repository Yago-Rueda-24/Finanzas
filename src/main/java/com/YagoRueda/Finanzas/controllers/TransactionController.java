package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.ErrorCsvException;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.exceptions.UnauthorizedOperationException;
import com.YagoRueda.Finanzas.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            transactionService.processCsv(user, file);
            return ResponseEntity.ok("Archivo procesado correctamente");
        } catch (ErrorCsvException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando archivo:\n " +  e.getMessage()+"\n" +e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando archivo: " + e.getMessage());
        }
    }


    @PostMapping()
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody TransactionDTO dto) {

        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.create(user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }

    @PutMapping()
    public ResponseEntity<?> modify(HttpServletRequest request, @RequestBody TransactionDTO dto, @RequestParam long id) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.modify(user, dto, id);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @DeleteMapping()
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam long id) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        try {
            transactionService.delete(user, id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Elemento borrado con exito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
