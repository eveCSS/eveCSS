package de.ptb.epics.eve.ecp1.intern.exceptions;

import de.ptb.epics.eve.ecp1.commands.IECP1Command;

public class WrongStartTagException extends AbstractRestoreECP1CommandException {

	public WrongStartTagException(final byte[] byteArray, final int startTag) {
		super(byteArray, "Wrong start tag. Got "
				+ Integer.toHexString(startTag) + " and expected "
				+ Integer.toHexString(IECP1Command.START_TAG) + ".");
	}
}