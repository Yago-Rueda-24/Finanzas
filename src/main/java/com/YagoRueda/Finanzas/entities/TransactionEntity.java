package com.YagoRueda.Finanzas.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "usuario_id")
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

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", created_at=" + created_at +
                '}';
    }
}
