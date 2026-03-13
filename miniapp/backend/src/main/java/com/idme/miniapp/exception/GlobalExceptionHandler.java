package com.idme.miniapp.exception;

import com.idme.miniapp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid request parameters"));
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        int status = ex.getStatusCode().value();
        String message = ex.getResponseBodyAsString();
        if (message == null || message.isBlank()) {
            message = ex.getStatusText();
        }
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(status, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, ex.getMessage()));
    }
}
