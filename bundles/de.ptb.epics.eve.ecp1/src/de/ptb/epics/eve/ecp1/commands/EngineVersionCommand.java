package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongByteArrayLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class EngineVersionCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0113;

	private int version;
	private int revision;
	private int patchlevel;

	public EngineVersionCommand(final int version,
			final int rev, final int patchlev) {
		this.version = version;
		this.revision = rev;
		this.patchlevel = patchlev;
	}

	public EngineVersionCommand(final byte[] byteArray) throws IOException,
			AbstractRestoreECP1CommandException {
		if (byteArray == null) {
			throw new IllegalArgumentException(
					"The parameter 'byteArray' must not be null!");
		}
		if (byteArray.length != 24) {
			throw new WrongByteArrayLengthException(byteArray, 24);
		}

		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				byteArray);
		final DataInputStream dataInputStream = new DataInputStream(
				byteArrayInputStream);

		final int startTag = dataInputStream.readInt();
		if (startTag != IECP1Command.START_TAG) {
			throw new WrongStartTagException(byteArray, startTag);
		}

		final char version = dataInputStream.readChar();
		if (version != IECP1Command.VERSION) {
			throw new WrongVersionException(byteArray, version);
		}

		final char commandTypeID = dataInputStream.readChar();
		if (commandTypeID != EngineVersionCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					EngineVersionCommand.COMMAND_TYPE_ID);
		}

		// unused length fiel
		final int fieldlength = dataInputStream.readInt();
		if (fieldlength != 12) {
			throw new WrongLengthException(byteArray, fieldlength, 12);
		}
		this.version = dataInputStream.readInt();
		this.revision = dataInputStream.readInt();
		this.patchlevel = dataInputStream.readInt();

	}

	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(ErrorCommand.COMMAND_TYPE_ID);
		dataOutputStream.writeInt(12);
		dataOutputStream.writeInt(this.version);
		dataOutputStream.writeInt(this.revision);
		dataOutputStream.writeInt(this.patchlevel);

		return byteArrayOutputStream.toByteArray();
	}

	public int getVersion() {
		return this.version;
	}

	public int getRevision() {
		return this.revision;
	}

	public int getPatchlevel() {
		return this.patchlevel;
	}

}