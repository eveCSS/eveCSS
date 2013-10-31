package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.BrokenByteArrayException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class AutoPlayCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0013;

	private boolean autoplay;

	public AutoPlayCommand() {
		this.autoplay = false;
	}

	public AutoPlayCommand(final boolean autoplay) {
		this.autoplay = autoplay;
	}

	public AutoPlayCommand(final byte[] byteArray) throws IOException,
			AbstractRestoreECP1CommandException {
		if (byteArray == null) {
			throw new IllegalArgumentException(
					"The parameter 'byteArray' must not be null!");
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
		if (commandTypeID != AutoPlayCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					AutoPlayCommand.COMMAND_TYPE_ID);
		}

		final int length = dataInputStream.readInt();
		if (length != 4) {
			throw new WrongLengthException(byteArray, length, 4);
		}

		final int autoplay = dataInputStream.readInt();
		if (autoplay == 0) {
			this.autoplay = false;
		} else if (autoplay == 1) {
			this.autoplay = true;
		} else {
			throw new BrokenByteArrayException(byteArray,
					"Wrong value for autoplay. Expected 0 or 1, got "
							+ autoplay + "!");
		}

	}

	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(AutoPlayCommand.COMMAND_TYPE_ID);
		dataOutputStream.writeInt(4);
		if (this.autoplay) {
			dataOutputStream.writeInt(1);
		} else {
			dataOutputStream.writeInt(0);
		}

		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public boolean isAutoplay() {
		return this.autoplay;
	}

	public void setAutoplay(final boolean autoplay) {
		this.autoplay = autoplay;
	}
}