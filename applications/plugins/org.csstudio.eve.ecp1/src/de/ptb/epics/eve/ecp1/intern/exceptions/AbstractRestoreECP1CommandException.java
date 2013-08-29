package de.ptb.epics.eve.ecp1.intern.exceptions;

import java.util.Arrays;

/**
 * 
 * @author ?
 */
public abstract class AbstractRestoreECP1CommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] byteArray;

	public AbstractRestoreECP1CommandException(final byte[] byteArray,
			final String message) {
		super(message);
		if (byteArray == null) {
			this.byteArray = new byte[0];
		} else {
			this.byteArray = Arrays.copyOf(byteArray, byteArray.length);
		}
	}

	public byte[] getByteArray() {
		return this.byteArray;
	}
}