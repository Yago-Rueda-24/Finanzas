package com.YagoRueda.Finanzas.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HashUtil {

    private PasswordEncoder encoder;

    @Value("${security.bcrypt.strength:12}")
    private int rounds;

    @PostConstruct
    public void init(){
        this.encoder = new BCryptPasswordEncoder(rounds);
    }

    /**
     * Algoritmo de hasheo no determinista. Usa un salt aletorio a la hora de hashear lo que provoca
     * que para una misma entrada no genere la misma salida, se debe usar match() para comprobar 2 cadenas hasheadas asi
     * @param toHash
     * @return Cadena hasheada
     */
    public String BCryptHash(String toHash) {
        return encoder.encode(toHash);
    }

    /**
     * Comprobación de cadenas en texto plano con cadenas hash generadas por un algoritmo no determinista
     * @param plaintext
     * @param hash
     * @return {@code True} si la cadena en texto plano es la misma que la del hash {@code False} en caso contrario
     */
    public boolean BCryptMatch(String plaintext, String hash) {
        return encoder.matches(plaintext, hash);
    }

    /**
     * Algoritmo de hash determinista que siempre genera para una entrada especifica siempre genera la misma salida
     * @param tohash
     * @return
     */
    public String sha256Hash(String tohash) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(tohash.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

}
