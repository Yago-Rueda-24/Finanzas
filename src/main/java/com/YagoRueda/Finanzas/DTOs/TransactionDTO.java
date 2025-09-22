package com.YagoRueda.Finanzas.DTOs;

import com.YagoRueda.Finanzas.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

public class TransactionDTO {

    @Getter
    @Setter
    private long id;


    @Getter
    @Setter
    private UserEntity user;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private  float amount;

    @Getter
    @Setter
    private  String category;

    @Getter
    @Setter
    private LocalDate date;

    @Getter
    @Setter
    private Instant created_at;
}
