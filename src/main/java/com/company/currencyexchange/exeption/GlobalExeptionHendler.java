package com.company.currencyexchange.exeption;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExeptionHendler {
    /**
     * Ошибки, связанные с некорректными данными.
     * Например: неверный код валюты.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExeptionResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ExeptionResponse error = new ExeptionResponse(
                ex.getMessage(),
                "Bad Request",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Ошибки логики.
     * Например: невозможно удалить валюту.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExeptionResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        ExeptionResponse error = new ExeptionResponse(
                ex.getMessage(),
                "Conflict",
                request.getRequestURI(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Обработка всех остальных ошибок.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExeptionResponse> handleOther(
            Exception ex,
            HttpServletRequest request
    ) {
        ExeptionResponse error = new ExeptionResponse(
                ex.getMessage(),
                "Internal Server Error",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
