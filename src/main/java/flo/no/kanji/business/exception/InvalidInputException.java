package flo.no.kanji.business.exception;

public class InvalidInputException extends RuntimeException {

	private static final long serialVersionUID = 745506968015157154L;

	public InvalidInputException(final String msg) {
		super(msg);
	}
}
