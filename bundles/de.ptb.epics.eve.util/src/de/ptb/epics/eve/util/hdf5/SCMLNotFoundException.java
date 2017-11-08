package de.ptb.epics.eve.util.hdf5;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class SCMLNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String message;
	
	SCMLNotFoundException(String message) {
		this.message = message;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return this.message;
	}
}