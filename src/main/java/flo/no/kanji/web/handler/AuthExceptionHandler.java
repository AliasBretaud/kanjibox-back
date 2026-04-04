package flo.no.kanji.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

@ControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handling authentication Exceptions
     *
     * @param ex Exception
     * @return 401 UNAUTHORIZED or 403 FORBIDDEN status with returned error
     */
    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<Object> handleAuthenticationException(Exception ex) {
        var status = ex instanceof AccessDeniedException ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
        var body = new ApiExceptionWrapper(
                Instant.now().toString(),
                status.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
}
