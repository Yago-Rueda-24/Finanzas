package com.YagoRueda.Finanzas.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
public class ApiKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;
    @Getter
    @Setter
    private String apiKey;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @Getter
    @Setter
    private UserEntity user;
    @Getter
    @Setter
    private boolean validated;
    @Getter
    @Setter
    private Instant createdAT;

}
