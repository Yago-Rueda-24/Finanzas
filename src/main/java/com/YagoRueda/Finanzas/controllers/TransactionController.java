package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }



    @PostMapping()
    public HttpResponse<?> saluda(HttpServletRequest request, @RequestBody TransactionDTO dto){

        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        transactionService.create(user,dto);

        return null;
    }
}
