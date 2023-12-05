package flo.no.kanji.web.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Global Exception object returned whenever an exception is thrown within the application
 * @author Florian
 *
 */
@Getter
@AllArgsConstructor
public class ApiExceptionWrapper {

	/** Exception timestamp **/
	private String timestamp;
	
	/** Returned HTTP status related to the exception**/
	private int status;
	
	/** Error class **/
	private String error;
	
	/** Error message **/
	private String message;
}
