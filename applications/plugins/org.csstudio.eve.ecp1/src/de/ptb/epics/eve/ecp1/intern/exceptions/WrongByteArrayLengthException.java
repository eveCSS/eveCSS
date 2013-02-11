package de.ptb.epics.eve.ecp1.intern.exceptions;

public class WrongByteArrayLengthException extends
		AbstractRestoreECP1CommandException {

	public WrongByteArrayLengthException(final byte[] byteArray,
			final int expectedLength) {
		super(byteArray, "Wrong length of byte array. Got " + byteArray.length
				+ " and expected " + expectedLength + ".");
	}
}