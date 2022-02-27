package flo.no.kanji.business.exception;

public class ItemNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6214183095201528801L;

	public ItemNotFoundException(final String msg) {
		super(msg);
	}
}
