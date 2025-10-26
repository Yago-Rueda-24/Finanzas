package com.YagoRueda.Finanzas.services;

import com.YagoRueda.Finanzas.DTOs.BalanceDTO;
import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.repositories.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
public class AnaliticsService {

    private final TransactionRepository transactionRepository;

    public AnaliticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Comprueba que la fecha de entrada introducida como String este en un formato valido, lo parsea a LocalDate y retorna un LocalDate con valor 01/MM/AAAA
     * siendo MM y AAAA el mes y año pasado en el string
     *
     * @param date String que representa un mes con formato MM/AAAA
     * @return LocalDate que representa la el primer dia del mes/Año introducidos en formato 01/MM/AAAA
     * @throws IllegalArgumentException
     */
    private LocalDate validateMonthInputDate(String date) throws IllegalArgumentException {
        int year = 0;
        int month = 0;
        LocalDate startDate;

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

        return startDate = LocalDate.of(year, month, 1);
    }

    private int daysOfWeekInMonth(LocalDate monthYear, DayOfWeek day) {
        int month = monthYear.getMonth().getValue();
        int year = monthYear.getYear();
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDias = yearMonth.lengthOfMonth();

        int contador = 0;

        for (int dia = 1; dia <= totalDias; dia++) {
            LocalDate fecha = LocalDate.of(year, month, dia);
            if (fecha.getDayOfWeek() == day) {
                contador++;
            }
        }

        return contador;
    }

    public BalanceDTO monthlyBalance(UserEntity user, String date) throws IllegalArgumentException {
        LocalDate startDate;

        try {
            startDate = validateMonthInputDate(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }


        //Obtención de las transacciones del usuario

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

                List<TransactionEntity> sublista = transactions.subList(start, end);

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
        BalanceDTO dto = new BalanceDTO();
        dto.setIncome((float) ingresos.sum());
        dto.setExpense((float) gastos.sum());
        dto.setNumTransactions(transactions.size());
        dto.setBalance(balance);

        return dto;
    }

    public BalanceDTO monthlyExpensePerDayofWeek(UserEntity user, String date) throws IllegalArgumentException {

        LocalDate startDate;
        try {
            startDate = validateMonthInputDate(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<TransactionEntity> transactions = transactionRepository.findByUserAndDateBetween(user, startDate, endDate);

        List<Float> weekExpense = Collections.synchronizedList(new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f)));

        int cores = Runtime.getRuntime().availableProcessors();
        int numHilos = Math.min(transactions.size(), cores);
        if (numHilos == 0) numHilos = 1;

        //Calculo del tamaño de las sublistas
        int sublistLength = transactions.size() / numHilos;

        try (ExecutorService executor = Executors.newFixedThreadPool(numHilos)) {
            for (int i = 0; i < numHilos; i++) {

                int start = i * sublistLength;
                int end = (i == numHilos - 1) ? transactions.size() : start + sublistLength;
                executor.submit(() -> {
                    for (int j = start; j < end; j++) {
                        TransactionEntity itTrans = transactions.get(j);
                        if (itTrans.getAmount() < 0) {
                            int day = itTrans.getDate().getDayOfWeek().getValue();
                            synchronized (weekExpense) {
                                weekExpense.set(day - 1, weekExpense.get(day - 1) - itTrans.getAmount());
                            }
                        }
                    }
                });
            }
        }


        BalanceDTO dto = new BalanceDTO();
        for (int i = 0; i < weekExpense.size(); i++) {
            DayOfWeek day = DayOfWeek.values()[i];
            int numdays = daysOfWeekInMonth(startDate, day);
            weekExpense.set(i, weekExpense.get(i) / numdays);
        }

        dto.setAverageExpensePerDayofWeek(weekExpense);

        return dto;
    }

    public BalanceDTO monthlyTopIncome(UserEntity user, String date, int topnum) {
        LocalDate startDate;
        try {
            startDate = validateMonthInputDate(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (topnum < 1) {
            throw new IllegalArgumentException("La cantidad de transacciones en el top debe ser mayor que 0");
        }

        PageRequest limit = PageRequest.of(0, topnum);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<TransactionEntity> transactions = transactionRepository.findTopIncome(user, startDate, endDate, limit);

        List<TransactionDTO> dtoList = transactions.stream()
                .map(TransactionEntity::toDTO)
                .toList();

        BalanceDTO dto = new BalanceDTO();
        dto.setTopIncome(dtoList);
        return dto;


    }

}
