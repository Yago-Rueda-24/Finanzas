package com.YagoRueda.Finanzas.controllers;

import ai.onnxruntime.OrtException;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.utils.TransactionClassifier;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prueba")
@Hidden
public class PruebaController {

    @GetMapping("/saluda")
    public String saluda(HttpServletRequest request){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        return "Holas"+user.getUsername();
    }

    @PostMapping("/modelo")
    public String modelo (@RequestParam String entrada){
        try {
            return TransactionClassifier.clasifyTransaction(entrada);
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }
}
