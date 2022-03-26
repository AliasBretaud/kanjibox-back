package flo.no.kanji.business.exception;

/**
 * Exception raised when an object is not found when providing given parameters
 * @author Florian
 */
public class ItemNotFoundException extends RuntimeException {

	/**
	 * Serial version UUID
	 */
	private static final long serialVersionUID = 6214183095201528801L;

	/**
	 * Default constructor
	 * @param msg
	 * 			Exception error message
	 */
	public ItemNotFoundException(final String msg) {
		super(msg);
	}
}
