package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public String saluda(HttpServletRequest request){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        return "Holas"+user.getUsername();
    }
}
