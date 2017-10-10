package de.ptb.epics.eve.ecp1.types;

import java.io.IOException;

import de.ptb.epics.eve.ecp1.commands.IECP1Command;

/**
 * @author ?
 * @since 1.0
 */
public class Translator {

	/**
	 * Not implemented yet. Future version may convert a byte array into 
	 * a command.
	 * 
	 * @param byteArray unused
	 * @return <code>null</code>
	 */
	public IECP1Command byteArrayToCommand(final byte[] byteArray) {
		return null;
		// TODO implement
	}
	
	/**
	 * Returns the array of bytes contained in the given command.
	 * 
	 * @param command the command containing the bytes
	 * @return the array of bytes contained in the given command
	 * @throws IOException if an I/O error occurs
	 */
	public byte[] commandToByteArray(final IECP1Command command)
			throws IOException {
		return command.getByteArray();
	}
}