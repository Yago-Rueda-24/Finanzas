package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }



    @PostMapping()
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody TransactionDTO dto){

        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.create(user,dto);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }
    @PutMapping()
    public ResponseEntity<?> modify(HttpServletRequest request, @RequestBody TransactionDTO dto, @RequestParam long id){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.modify(user,dto,id);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
