package de.ptb.epics.eve.ecp1.intern.exceptions;

import de.ptb.epics.eve.ecp1.intern.IECP1Command;

public class WrongVersionException extends AbstractRestoreECP1CommandException {

	public WrongVersionException( final byte[] byteArray, final char version ) {
		super( byteArray, "Wrong version. Got " + Integer.toHexString( version ) +  " and expected " + Integer.toHexString( IECP1Command.VERSION ) + "." );
	}
	
}
