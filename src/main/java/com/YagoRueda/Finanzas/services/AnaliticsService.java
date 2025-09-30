package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class AnaliticsService {

    private final TransactionRepository transactionRepository;

    public AnaliticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


}
