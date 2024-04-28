package flo.no.kanji.web.handler;

import flo.no.kanji.business.exception.ExternalServiceError;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;

/**
 * Global controller for exceptions handling
 *
 * @author Florian
 */
@ControllerAdvice
@Slf4j
public class ExceptionHelper {

    public static ApiExceptionWrapper buildApiException(final HttpStatus status, final Exception ex) {
        return new ApiExceptionWrapper(new Timestamp(System.currentTimeMillis()).toString(),
                status.value(), ex.getClass().getName(), ex.getMessage());
    }

    /**
     * Handling User inputs relating exception
     *
     * @param ex Generated exception during the process of entity creation/update
     * @return 400 BAD_REQUEST status with returned error
     */
    @ExceptionHandler({
            InvalidInputException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
    })
    public ResponseEntity<Object> handleInvalidInputException(Exception ex) {
        log.error("Invalid Input Exception: ", ex);
        var status = HttpStatus.BAD_REQUEST;
        var apiException = buildApiException(status, ex);
        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handling not found object exceptions
     *
     * @param ex Generated exception while retrieving object
     * @return 404 NOT_FOUND status with returned error
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException ex) {
        var status = HttpStatus.NOT_FOUND;
        var apiException = buildApiException(status, ex);
        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handling general Exceptions
     *
     * @param ex Exception
     * @return 500 SERVICE_UNAVAILABLE status with returned error
     */
    @ExceptionHandler(ExternalServiceError.class)
    public ResponseEntity<Object> handleExternalServiceException(Exception ex) {
        log.error("External service Exception: ", ex);
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        var apiException = buildApiException(status, ex);
        return new ResponseEntity<>(apiException, status);
    }

}
