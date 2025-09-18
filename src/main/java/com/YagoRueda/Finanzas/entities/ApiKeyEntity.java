package com.YagoRueda.Finanzas.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
