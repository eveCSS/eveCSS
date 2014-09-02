package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class RepeatCountCommand implements IECP1Command {
	public static final char COMMAND_TYPE_ID = 0x0014;
	private int repeatCount;
	
	/**
	 * Constructor.
	 * 
	 * @param repeatCount the repeat count
	 */
	public RepeatCountCommand(int repeatCount) {
		if (repeatCount < 0 || repeatCount > 65535) {
			throw new IllegalArgumentException(
					"Repeat Count out of Range. Choose a value between 0 and 65535.");
		}
		this.repeatCount = repeatCount;
	}
	
	/**
	 * {@inherit-doc}
	 */
	@Override
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(RepeatCountCommand.COMMAND_TYPE_ID);
		dataOutputStream.writeInt(4);
		dataOutputStream.writeInt(this.repeatCount);

		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * @return the repeatCount
	 */
	public int getRepeatCount() {
		return repeatCount;
	}

	/**
	 * @param repeatCount the repeatCount to set
	 */
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
}