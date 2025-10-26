package com.YagoRueda.Finanzas.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Getter
    @Setter
    private List<Float> AverageExpensePerDayofWeek;

    @Getter
    @Setter
    private List<TransactionDTO> TopIncome;

}
