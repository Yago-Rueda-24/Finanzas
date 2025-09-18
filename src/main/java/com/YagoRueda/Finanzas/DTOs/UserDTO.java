package com.YagoRueda.Finanzas.DTOs;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class UserDTO {

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
}
