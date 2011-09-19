package de.ptb.epics.eve.ecp1.intern.exceptions;

public class WrongLengthException extends AbstractRestoreECP1CommandException {
	
	public WrongLengthException( final byte[] byteArray, final int lengthGot, final int lengthExpected ) {
		super( byteArray, "Wrong length. Got " + lengthGot +  " and expected " + lengthExpected + "." );
	}
	
}
