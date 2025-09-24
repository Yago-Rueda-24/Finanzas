package com.YagoRueda.Finanzas.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private Instant created_at;


    @Override
    public boolean equals(Object obj) {
        if(this== obj){return  true;}
        if (obj == null || getClass() != obj.getClass()) return false;
        UserEntity user = (UserEntity) obj;
        return this.username.equals(user.getUsername());

    }
}
