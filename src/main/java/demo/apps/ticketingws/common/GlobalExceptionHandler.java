package demo.apps.ticketingws.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ValidationException;

/**
 * @author jsa000y
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(
            javax.validation.ValidationException ex) {
        log.error("Received ValidationException", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(ex.getMessage());
        errorResponse.setErrorCode("400");
        return ResponseEntity.status(400).body(errorResponse);
    }
}
