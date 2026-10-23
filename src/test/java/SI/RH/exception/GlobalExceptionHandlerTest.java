package SI.RH.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFoundExceptionsToNotFoundResponse() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new NotFoundException("Poste", 12));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NOT_FOUND", response.getBody().code());
        assertEquals("Poste introuvable avec l'identifiant : 12", response.getBody().message());
    }

    @Test
    void mapsBusinessExceptionsToUnprocessableEntity() {
        ResponseEntity<ErrorResponse> response = handler.handleBusiness(new BusinessException("Transition interdite"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().code());
        assertEquals("Transition interdite", response.getBody().message());
    }

    @Test
    void mapsLegacyBusinessRuleExceptionsToConflict() {
        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(new BusinessRuleException("Transition interdite"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().code());
    }

    @Test
    void mapsInvalidArgumentsToBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(new IllegalArgumentException("Identifiant invalide"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", response.getBody().code());
        assertEquals("Identifiant invalide", response.getBody().message());
    }

    @Test
    void doesNotExposeUnexpectedExceptionDetails() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(new RuntimeException("internal detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Une erreur interne est survenue", response.getBody().message());
    }

    @Test
    void doesNotExposeDatabaseDetailsForIntegrityConflicts() {
        ResponseEntity<ErrorResponse> response = handler.handleConflict(new DataIntegrityViolationException("database detail"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflit avec les données existantes", response.getBody().message());
    }
}
