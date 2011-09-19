package de.ptb.epics.eve.ecp1.intern.exceptions;

public class BrokenByteArrayException extends AbstractRestoreECP1CommandException {
	public BrokenByteArrayException( final byte[] byteArray, final String message ) {
		super( byteArray, message );
	}
}
