package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.ErrorCsvException;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.exceptions.UnauthorizedOperationException;
import com.YagoRueda.Finanzas.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transacciones", description = "Operaciones sobre transacciones")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            transactionService.processCsv(user, file);
            return ResponseEntity.ok("Archivo procesado correctamente");
        } catch (ErrorCsvException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando archivo:\n " + e.getMessage() + "\n" + e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando archivo: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCsv(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            List<Long> errors = transactionService.validateCsv(file);
            if (errors.isEmpty()) {
                return ResponseEntity.ok("Archivo validado");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error procesando archivo:\n " + "Errores parseando las lineas del csv\n" + errors);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando archivo: " + e.getMessage());
        }
    }


    @Operation(summary = "Crear una transaccion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción creada",
                            content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al crear la transacción")
            })
    @PostMapping()
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody TransactionDTO dto) {

        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.create(user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }

    @PutMapping()
    public ResponseEntity<?> modify(HttpServletRequest request, @RequestBody TransactionDTO dto, @RequestParam long id) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");

        try {
            TransactionEntity transaction = transactionService.modify(user, dto, id);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (InputTransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @DeleteMapping()
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam long id) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        try {
            transactionService.delete(user, id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Elemento borrado con exito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
