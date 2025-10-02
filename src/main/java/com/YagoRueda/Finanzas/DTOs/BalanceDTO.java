package com.YagoRueda.Finanzas.DTOs;

import lombok.Getter;
import lombok.Setter;

public class BalanceDTO {

    @Getter
    @Setter
    private float income;

    @Getter
    @Setter
    private float expense;

    @Getter
    @Setter
    private int numTransactions;

    @Getter
    @Setter
    private float balance;

}
