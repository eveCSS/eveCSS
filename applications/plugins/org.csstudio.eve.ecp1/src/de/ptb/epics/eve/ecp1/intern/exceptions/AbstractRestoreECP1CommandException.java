package de.ptb.epics.eve.ecp1.intern.exceptions;

public abstract class AbstractRestoreECP1CommandException extends Exception {

	private byte[] byteArray;
	
	public AbstractRestoreECP1CommandException( final byte[] byteArray, final String message ) {
		super( message );
		this.byteArray = byteArray;
	}
	
	public byte[] getByteArray() {
		return this.byteArray;
	}
	
}
