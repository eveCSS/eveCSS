package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;
import de.ptb.epics.eve.ecp1.types.RequestType;

public class AnswerRequestCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0008;

	private int requestId;
	private RequestType requestType;

	private boolean booleanBuffer;
	private int integerBuffer;
	private float floatBuffer;
	private String textBuffer;

	public AnswerRequestCommand(final int requestId,
			final RequestType requestType, final boolean booleanValue) {
		this.requestId = requestId;
		this.requestType = requestType;
		this.booleanBuffer = booleanValue;
	}

	public AnswerRequestCommand(final int requestId,
			final RequestType requestType, final int integerValue) {
		this.requestId = requestId;
		this.requestType = requestType;
		this.integerBuffer = integerValue;
	}

	public AnswerRequestCommand(final int requestId, final int integerValue) {
		this.requestId = requestId;
		this.requestType = RequestType.INT32;
		this.integerBuffer = integerValue;
	}

	public AnswerRequestCommand(final int requestId, final float floatValue) {
		this.requestId = requestId;
		this.requestType = RequestType.FLOAT32;
		this.floatBuffer = floatValue;
	}

	public AnswerRequestCommand(final int requestId,
			final RequestType requestType, final String textValue) {
		this.requestId = requestId;
		this.requestType = requestType;
		this.textBuffer = textValue;
	}

	public AnswerRequestCommand(final byte[] byteArray) throws IOException,
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
		if (commandTypeID != AnswerRequestCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					AnswerRequestCommand.COMMAND_TYPE_ID);
		}

		final int length = dataInputStream.readInt();

		this.requestId = dataInputStream.readInt();

		dataInputStream.readChar();
		dataInputStream.readByte();

		this.requestType = RequestType.byteToRequestType(dataInputStream
				.readByte());

		switch (this.requestType) {
		case YES_NO:
		case OK_CANCEL:
			this.booleanBuffer = dataInputStream.readInt() == 0 ? false : true;
			break;
		case INT32:
		case TRIGGER:
			this.integerBuffer = dataInputStream.readInt();
			break;
		case FLOAT32:
			this.floatBuffer = dataInputStream.readFloat();
			break;
		case TEXT:
		case ERROR_TEXT:
			final int lenghtOfText = dataInputStream.readInt();
			if (lenghtOfText != 0xffffffff) {
				final byte[] byteBuffer = new byte[lenghtOfText * 2];
				dataInputStream.readFully(byteBuffer);
				this.textBuffer = new String(byteBuffer,
						IECP1Command.STRING_ENCODING);
			}
		}
	}

	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		;
		dataOutputStream.writeChar(AnswerRequestCommand.COMMAND_TYPE_ID);

		switch (this.requestType) {
		case YES_NO:
		case OK_CANCEL:
		case TRIGGER:
		case INT32:
		case FLOAT32:
			dataOutputStream.writeInt(12);
			break;

		case TEXT:
		case ERROR_TEXT:
			byte[] buffer = this.textBuffer
					.getBytes(IECP1Command.STRING_ENCODING);
			dataOutputStream.writeInt(12 + buffer.length);
			break;

		}

		dataOutputStream.writeInt(this.requestId);
		dataOutputStream.writeChar(0);
		dataOutputStream.writeByte(0);
		dataOutputStream.writeByte(RequestType
				.requestTypeToByte(this.requestType));

		switch (this.requestType) {
		case YES_NO:
		case OK_CANCEL:
			dataOutputStream.writeInt(this.booleanBuffer ? 1 : 0);
			break;

		case INT32:
		case TRIGGER:
			dataOutputStream.writeInt(this.integerBuffer);
			break;

		case FLOAT32:
			dataOutputStream.writeFloat(this.floatBuffer);
			break;

		case TEXT:
		case ERROR_TEXT:
			if (this.textBuffer.length() != 0) {
				byte[] buffer = this.textBuffer
						.getBytes(IECP1Command.STRING_ENCODING);
				dataOutputStream.writeInt(this.textBuffer.length());
				dataOutputStream.write(buffer);
			} else {
				dataOutputStream.writeInt(0xffffffff);
			}
			break;
		}
		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public int getRequestId() {
		return this.requestId;
	}

	public void setRequestId(final int requestId) {
		this.requestId = requestId;
	}

	public RequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType(final RequestType requestType) {
		this.requestType = requestType;
	}

	public boolean isYes() {
		return this.requestType == RequestType.YES_NO && this.booleanBuffer;
	}

	public void setYes(final boolean booleanValue) {
		this.requestType = RequestType.YES_NO;
		this.booleanBuffer = booleanValue;
	}

	public boolean isOK() {
		return this.requestType == RequestType.OK_CANCEL && this.booleanBuffer;
	}

	public void setOk(final boolean booleanValue) {
		this.requestType = RequestType.OK_CANCEL;
		this.booleanBuffer = booleanValue;
	}

	public int getIntegerValue() {
		return (this.requestType == RequestType.INT32) ? this.integerBuffer
				: Integer.MIN_VALUE;
	}

	public void setIntegerValue(final int integerValue) {
		this.requestType = RequestType.INT32;
		this.integerBuffer = integerValue;
	}

	public float getFloatValue() {
		return (this.requestType == RequestType.FLOAT32) ? this.floatBuffer
				: Float.MIN_VALUE;
	}

	public void setFloatValue(final float floatValue) {
		this.requestType = RequestType.FLOAT32;
		this.floatBuffer = floatValue;
	}

	public String getText() {
		return this.requestType == RequestType.TEXT ? this.textBuffer : null;
	}

	public void setText(final String text) {
		this.requestType = RequestType.TEXT;
		this.textBuffer = text;
	}

	public String getErrorText() {
		return this.requestType == RequestType.ERROR_TEXT ? this.textBuffer
				: null;
	}

	public void setErrorText(final String errorText) {
		this.requestType = RequestType.ERROR_TEXT;
		this.textBuffer = errorText;
	}
}