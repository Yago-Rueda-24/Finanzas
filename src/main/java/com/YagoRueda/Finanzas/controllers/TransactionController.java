package com.YagoRueda.Finanzas.controllers;

import com.YagoRueda.Finanzas.DTOs.TransactionDTO;
import com.YagoRueda.Finanzas.entities.TransactionEntity;
import com.YagoRueda.Finanzas.entities.UserEntity;
import com.YagoRueda.Finanzas.exceptions.ErrorCsvException;
import com.YagoRueda.Finanzas.exceptions.InputTransactionException;
import com.YagoRueda.Finanzas.exceptions.UnauthorizedOperationException;
import com.YagoRueda.Finanzas.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Registra una serie de transacciones anotadas en un CSV",
            description = "Utiliza un archivo CSV de entrada con la información de todas las transacciones que se quiera importar. Se debe usar la plantilla que se puede encontar en la documentación",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción modificada correctamente",
                            content = @Content(mediaType = "application/text", schema = @Schema(example = "Archivo procesado correctamente"))),
                    @ApiResponse(responseCode = "400", description = "Error al crear la transacción",
                            content = @Content(mediaType = "application/text", schema = @Schema(example = "Error procesando archivo: Error")))
            })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(HttpServletRequest request, @Parameter(
            description = "Fichero con las transacciones",
            required = true,
            content = @Content(mediaType = "text/csv")
    ) @RequestParam("file") MultipartFile file) {
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

    @Operation(summary = "Valida una serie de transacciones anotadas en un CSV",
            description = "Utiliza un archivo CSV de entrada con la información de todas las transacciones que se quiera validar. Se debe usar la plantilla que se puede encontar en la documentación",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción modificada correctamente",
                            content = @Content(mediaType = "application/text", schema = @Schema(example = "Archivo validado correctamente"))),
                    @ApiResponse(responseCode = "400", description = "Error al crear la transacción",
                            content = @Content(mediaType = "application/text", schema = @Schema(example = "Error procesando archivo: Error")))
            })
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


    @Operation(summary = "Crea una transaccion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción creada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al crear la transacción",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{campo_con_error: error encontrado}")))
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

    @Operation(summary = "Modifica una transaccion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción modificada correctamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al modificar la transacción"
                            , content = @Content(mediaType = "application/json", schema = @Schema(example = "{campo_con_error: error encontrado}"))),
                    @ApiResponse(responseCode = "401", description = "sin autorización para modificar la transacción")
            })
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

    @Operation(summary = "Elimina una transaccion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción eliminada correctamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Error al eliminar la transacción",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{campo_con_error: error encontrado}"))),
                    @ApiResponse(responseCode = "401", description = "sin autorización para eliminar la transacción")
            })
    @DeleteMapping()
    public ResponseEntity<?> delete(HttpServletRequest request, @Parameter(
            description = "id de la transacción a eliminar",
            required = true,
            content = @Content(mediaType = "text/plain"),
            example = "1"
    ) @RequestParam long id) {
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
