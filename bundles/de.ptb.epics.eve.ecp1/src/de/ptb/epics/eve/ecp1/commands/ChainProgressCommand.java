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

public class ChainProgressCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0114;

	private long timestamp;
	private int chainId;
	private int positionCounter;
	private int remainingTime;

	public ChainProgressCommand(final long timestamp,
			final int chainId,
			final int positionCounter,
			final int remainingTime) {
		this.timestamp = timestamp;
		this.chainId = chainId;
		this.positionCounter = positionCounter;
		this.remainingTime = remainingTime;
	}

	public ChainProgressCommand(final byte[] byteArray) throws IOException,
			AbstractRestoreECP1CommandException {
		if (byteArray == null) {
			throw new IllegalArgumentException(
					"The parameter 'byteArray' must not be null!");
		}
		if (byteArray.length != 32) {
			throw new WrongByteArrayLengthException(byteArray, 32);
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
		if (commandTypeID != ChainProgressCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					ChainProgressCommand.COMMAND_TYPE_ID);
		}

		final int length = dataInputStream.readInt();
		if (length != 20) {
			throw new WrongLengthException(byteArray, length, 20);
		}

		this.timestamp = dataInputStream.readLong();
		this.chainId = dataInputStream.readInt();
		this.positionCounter = dataInputStream.readInt();
		this.remainingTime = dataInputStream.readInt();
	}

	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(ChainProgressCommand.COMMAND_TYPE_ID);
		dataOutputStream.writeInt(32);
		dataOutputStream.writeLong(this.timestamp);
		dataOutputStream.writeChar(0);
		dataOutputStream.writeByte(0);
		dataOutputStream.writeInt(this.chainId);
		dataOutputStream.writeInt(this.positionCounter);
		dataOutputStream.writeInt(this.remainingTime);
		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public int getChainId() {
		return this.chainId;
	}

	public void setChainId(final int chainId) {
		this.chainId = chainId;
	}

	public long getTimeStamp() {
		return this.timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPositionCounter() {
		return this.positionCounter;
	}

	public void setPositionCounter(final int positionCounter) {
		this.positionCounter = positionCounter;
	}

	public int getRemainingTime() {
		return this.remainingTime;
	}

	public void setRemainingTime(final int remainingTime) {
		this.remainingTime = remainingTime;
	}
}