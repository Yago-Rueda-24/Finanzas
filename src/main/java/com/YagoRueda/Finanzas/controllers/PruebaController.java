package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prueba")
public class PruebaController {

    @GetMapping("/saluda")
    public String saluda(HttpServletRequest request){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        return "Holas"+user.getUsername();
    }
}
