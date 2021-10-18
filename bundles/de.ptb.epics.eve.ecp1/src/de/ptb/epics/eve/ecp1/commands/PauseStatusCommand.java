package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;
import de.ptb.epics.eve.ecp1.types.PauseStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseStatusCommand implements IECP1Command {
	public static final char COMMAND_TYPE_ID = 0x0115;

	private long timestamp;
	private int chainId;
	private int generalTimeStamp;
	private int nanoseconds;
	private List<PauseStatusEntry> pauseStatusList;
	
	
	public PauseStatusCommand(final byte[] byteArray) throws IOException, 
			AbstractRestoreECP1CommandException {
		if (byteArray == null) {
			throw new IllegalArgumentException("byteArray must not be null!");
		}
		final ByteArrayInputStream byteArrayInputStream = 
				new ByteArrayInputStream(byteArray);
		final DataInputStream dataInputStream = 
				new DataInputStream(byteArrayInputStream);
		
		final int startTag = dataInputStream.readInt();
		if (startTag != IECP1Command.START_TAG) {
			throw new WrongStartTagException(byteArray, startTag);
		}

		final char version = dataInputStream.readChar();
		if (version != IECP1Command.VERSION) {
			throw new WrongVersionException(byteArray, version);
		}

		final char commandTypeID = dataInputStream.readChar();
		if (commandTypeID != PauseStatusCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					PauseStatusCommand.COMMAND_TYPE_ID);
		}
		
		int length = dataInputStream.readInt();
		
		this.generalTimeStamp = dataInputStream.readInt();
		this.nanoseconds = dataInputStream.readInt();
		this.chainId = dataInputStream.readInt();
		
		length -= 12;
		
		this.pauseStatusList = new ArrayList<>();
		while (length > 0) {
			PauseStatus pauseStatus = PauseStatus.byteToPauseStatus(
					dataInputStream.readByte());
			int pauseId = dataInputStream.readInt();
			this.pauseStatusList.add(new PauseStatusEntry(pauseId, pauseStatus));
			length -= 5;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getByteArray() throws IOException {
		// nothing to be done here. Engine to Viewer only
		return null;
	}

	public int getChainId() {
		return chainId;
	}

	public List<PauseStatusEntry> getPauseStatusList() {
		return pauseStatusList;
	}
	
	public int getTimeStampSeconds() {
		return this.generalTimeStamp;
	}

	public int getTimeStampNanoSeconds() {
		return this.nanoseconds;
	}
}
