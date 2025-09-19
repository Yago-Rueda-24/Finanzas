package com.YagoRueda.Finanzas.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptHashser {

    private PasswordEncoder encoder;

    @Value("${security.bcrypt.strength:12}")
    private int rounds;

    @PostConstruct
    public void init(){
        this.encoder = new BCryptPasswordEncoder(rounds);
    }

    public String hash(String toHash) {
        return encoder.encode(toHash);
    }

    public boolean match(String plaintext, String hash) {
        return encoder.matches(plaintext, hash);
    }

}
