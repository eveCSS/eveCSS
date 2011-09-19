package de.ptb.epics.eve.ecp1.intern;

import java.io.IOException;

public class Translator {

	public IECP1Command byteArrayToCommand( final byte[] byteArray ) {
		return null;
	}
	
	public byte[] commandToByteArray( final IECP1Command command ) throws IOException {
		return command.getByteArray();
	}
	
}
