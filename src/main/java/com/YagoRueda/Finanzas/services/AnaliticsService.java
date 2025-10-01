package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

@Service
public class AnaliticsService {

    private final TransactionRepository transactionRepository;

    public AnaliticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void monthlyBalance(UserEntity user, String date) {
        int year = 0;
        int month = 0;

        try {

            //Validaciones sobre el String que se recibe
            if (date == null || date.isEmpty()) {
                throw new IllegalArgumentException("Fecha vacia o nula");
            }
            String[] separated = date.trim().split("/");
            if (separated.length != 2) {
                throw new IllegalArgumentException("Formato de fecha inválido, debe ser MM/YYYY");
            }

            //Validaciones sobre el mes
            String monthStr = separated[0].trim();

            if (monthStr.isEmpty()) {
                throw new IllegalArgumentException("Mes mal formateado");
            }
            month = Integer.parseInt(monthStr);
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("El valor de mes debe estar entre 1 y 12");
            }

            //Validaciones sobre el año
            String yearStr = separated[1].trim();

            if (yearStr.isEmpty()) {
                throw new IllegalArgumentException("Año mal formateado");
            }
            year = Integer.parseInt(separated[1]);
            if (year < 1930) {
                throw new IllegalArgumentException("El año deber ser superior a 1930");
            }


        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        //Obtención de las transacciones del usuario
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<TransactionEntity> transactions = transactionRepository.findByUserAndDateBetween(user, startDate, endDate);

        //Creación de registros para sumar lso ingresos y gastos
        DoubleAdder ingresos = new DoubleAdder();
        DoubleAdder gastos = new DoubleAdder();



        //Calculo del número de hilos de la pool de hilos
        int cores = Runtime.getRuntime().availableProcessors();
        int numHilos = Math.min(transactions.size(), cores);

        //Calculo del tamaño de las sublistas
        int sublistLength = transactions.size() / numHilos;

        //Creación de la pool de hilos
        try (ExecutorService pool = Executors.newFixedThreadPool(numHilos)) {
            for (int i = 0; i < numHilos; i++) {
                int start = i * sublistLength;
                int end = (i == numHilos - 1) ? transactions.size() : start + sublistLength;

               List<TransactionEntity> sublista = transactions.subList(start,end);

                pool.submit(() -> {
                    for (TransactionEntity t : sublista) {
                        float valor = t.getAmount();
                        if (valor >= 0) {
                            ingresos.add(valor);
                        } else {
                            gastos.add(Math.abs(valor));
                        }
                    }
                });


            }

        }
        float balance = (float) (ingresos.sum() - gastos.sum());
        System.out.println(balance);

        transactions.forEach(System.out::println);
    }


}
