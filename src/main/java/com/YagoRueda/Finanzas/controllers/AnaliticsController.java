package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.services.AnaliticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/analitics")
public class AnaliticsController {

    private final AnaliticsService analiticsService;

    public AnaliticsController(AnaliticsService analiticsService) {
        this.analiticsService = analiticsService;
    }
    @GetMapping("/balance")
    public ResponseEntity<?> monthlyBalance(HttpServletRequest request, @RequestParam String date){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        analiticsService.monthlyBalance(user,date);

        return  ResponseEntity.status(HttpStatus.OK).build();
    }
}
