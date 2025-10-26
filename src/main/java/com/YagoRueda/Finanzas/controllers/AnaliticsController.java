package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.BalanceDTO;
import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.services.AnaliticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/analitics")
@Tag(name = "Analisis", description = "Distintos tipos de analisis sobre las transacciones")
public class AnaliticsController {

    private final AnaliticsService analiticsService;

    public AnaliticsController(AnaliticsService analiticsService) {
        this.analiticsService = analiticsService;
    }

    @Operation(summary = "Calcula el gasto total, ingreso total y balance de las transacciones de un mes",
            description = "Devuelve el balance mensual del usuario autenticado. "
                    + "El parámetro `date` debe tener el formato `mm/yyyy`, por ejemplo `09/2025`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valores calculados correctamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al calcular los valores",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{ error: mensaje de error}")))
            }
    )
    @GetMapping("/balance")
    public ResponseEntity<?> monthlyBalance(HttpServletRequest request, @Parameter(
            description = "Fecha en formato mm/yyyy",
            required = true,
            example = "09/2025"
    ) @RequestParam String date) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        try {
            BalanceDTO dto = analiticsService.monthlyBalance(user, date);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }

    }


    @Operation(summary = "Calcula el gasto medio para cada dia de la semana",
            description = "Devuelve una lista con el gasto medio para cada dia de la semana (L;M;X;J;V;S;D) para un determinado mes. "
                    + "El parámetro `date` debe tener el formato `mm/yyyy`, por ejemplo `09/2025`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valores calculados correctamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al calcular los valores",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{ error: mensaje de error}")))
            }
    )
    @GetMapping("/averageExpensePerDayOfWeeK")
    public ResponseEntity<?> averageExpensePerDayOfWeeK(HttpServletRequest request, @Parameter(
            description = "Fecha en formato mm/yyyy",
            required = true,
            example = "09/2025"
    ) @RequestParam String date) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            BalanceDTO dto = analiticsService.monthlyExpensePerDayofWeek(user, date);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }

    }

    @GetMapping("/topIncome")
    public ResponseEntity<?> topIncome(HttpServletRequest request, @RequestParam String date , @RequestParam int num) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            BalanceDTO dto = analiticsService.monthlyTopIncome(user, date,num);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/topExpense")
    public ResponseEntity<?> topExpense(HttpServletRequest request, @RequestParam String date , @RequestParam int num) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            BalanceDTO dto = analiticsService.monthlyTopExpense(user, date,num);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
