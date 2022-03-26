package flo.no.kanji.business.exception;

/**
 * Exception raised when the input payload is invalid for creating.updating kanji/words objects
 * @author Florian
 */
public class InvalidInputException extends RuntimeException {

	private static final long serialVersionUID = 745506968015157154L;

	/**
	 * Default constructor
	 * @param msg Exception error message
	 */
	public InvalidInputException(final String msg) {
		super(msg);
	}
}
