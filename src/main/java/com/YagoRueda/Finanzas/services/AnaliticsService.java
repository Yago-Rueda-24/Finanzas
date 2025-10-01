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

@Service
public class AnaliticsService {

    private final TransactionRepository transactionRepository;

    public AnaliticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void monthlyBalance(UserEntity user , String date){
            int year = 0;
            int month = 0;

            try{

                //Comparaciones sobre el String que se recibe
                if(date==null || date.isEmpty()){
                    throw new IllegalArgumentException("Fecha vacia o nula");
                }
                String[] separated = date.trim().split("/");
                if (separated.length != 2) {
                    throw new IllegalArgumentException("Formato de fecha inválido, debe ser MM/YYYY");
                }

                String monthStr = separated[0].trim();

                if(monthStr.isEmpty()){
                   throw new IllegalArgumentException("Mes mal formateado");
                }
                month = Integer.parseInt(monthStr);
                if (month < 1 || month> 12){
                    throw new IllegalArgumentException("El valor de mes debe estar entre 1 y 12");
                }

                String yearStr = separated[1].trim();

                if(yearStr.isEmpty()){
                    throw new IllegalArgumentException("Año mal formateado");
                }
                year = Integer.parseInt(separated[1]);
                if (year<1930){
                    throw new IllegalArgumentException("El año deber ser superior a 1930");
                }


            }catch (NumberFormatException e){
                throw new IllegalArgumentException(e.getMessage());
            }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<TransactionEntity> transactions =transactionRepository.findByUserAndDateBetween(user,startDate,endDate);
        transactions.forEach(System.out::println);
    }


}
