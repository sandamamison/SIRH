package SI.RH.controller;

import SI.RH.exception.BusinessRuleException;
import SI.RH.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<Map<String, String>> conflict(BusinessRuleException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", message(exception, "Requête invalide")));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> internalError(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Une erreur interne est survenue."));
    }

    private String message(Exception exception, String fallback) {
        return exception.getMessage() == null ? fallback : exception.getMessage();
    }
}
