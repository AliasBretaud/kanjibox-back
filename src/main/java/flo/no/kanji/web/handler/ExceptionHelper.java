package flo.no.kanji.web.handler;

import java.sql.Timestamp;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionHelper {
	
	@ExceptionHandler(value = {
			InvalidInputException.class,
			ConstraintViolationException.class,
			HttpMessageNotReadableException.class,
	})
	public ResponseEntity<Object> handleInvalidInputException(Exception ex) {
	    log.error("Invalid Input Exception: ", ex);
	    var status = HttpStatus.BAD_REQUEST;
	    var apiException = buildApiException(status, ex);
	    return new ResponseEntity<Object>(apiException, status);
    }
	
	@ExceptionHandler(value = ItemNotFoundException.class)
	public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException ex) {
		var status = HttpStatus.NOT_FOUND;
	    var apiException = buildApiException(status, ex);
	    return new ResponseEntity<Object>(apiException, status);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handleException(Exception ex) {
	    log.error("Exception: ", ex);
	    var status = HttpStatus.INTERNAL_SERVER_ERROR;
	    var apiException = buildApiException(status, ex);
	    return new ResponseEntity<Object>(apiException, status);
    }
	
	private ApiExceptionWrapper buildApiException(final HttpStatus status, final Exception ex) {
		return new ApiExceptionWrapper(new Timestamp(System.currentTimeMillis()).toString(),
	    		status.value(),	ex.getClass().getName(), ex.getMessage());
	}

}
