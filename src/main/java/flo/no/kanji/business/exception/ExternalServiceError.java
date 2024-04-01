package flo.no.kanji.business.exception;

/***
 * Exception raised when the input payload is invalid for creating/updating kanji/words objects
 * @author Florian
 */
public class ExternalServiceError extends RuntimeException {

    /**
     * Default constructor
     * @param msg
     * 			Exception error message
     */
    public ExternalServiceError(final String msg, Throwable cause) {
        super(msg, cause);
    }
}
