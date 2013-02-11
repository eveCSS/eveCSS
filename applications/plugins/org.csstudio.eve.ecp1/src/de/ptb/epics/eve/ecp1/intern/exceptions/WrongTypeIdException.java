package de.ptb.epics.eve.ecp1.intern.exceptions;

public class WrongTypeIdException extends AbstractRestoreECP1CommandException {

	public WrongTypeIdException(final byte[] byteArray, final char got,
			final char expected) {
		super(byteArray, "Wrong typeid. Got " + Integer.toHexString(got)
				+ " and expected " + Integer.toHexString(expected) + ".");
	}
}