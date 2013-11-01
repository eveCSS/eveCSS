package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class CurrentXMLCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0110;

	private String xmlName;
	private String xmlAuthor;
	private byte[] xmlData;

	public CurrentXMLCommand() {
		this.xmlName = "";
		this.xmlAuthor = "";
		this.xmlData = new byte[0];
	}

	public CurrentXMLCommand(final String xmlName, final String xmlAuthor,
			final byte[] xmlData) {
		if (xmlName == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlName' must not be null!");
		}
		if (xmlAuthor == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlAuthor' must not be null!");
		}
		if (xmlData == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlData' must not be null!");
		}
		this.xmlName = xmlName;
		this.xmlAuthor = xmlAuthor;
		this.xmlData = Arrays.copyOf(xmlData, xmlData.length);
	}

	public CurrentXMLCommand(final byte[] byteArray) throws IOException,
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
		if (commandTypeID != CurrentXMLCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					CurrentXMLCommand.COMMAND_TYPE_ID);
		}

		// TODO: Length is never read, but the readInt() is necessary !!!!
		// for unknown reason
		final int length = dataInputStream.readInt();

		final int xmlNameLength = dataInputStream.readInt();
		if (xmlNameLength != 0xffffffff) {
			final byte[] xmlNameBuffer = new byte[xmlNameLength];
			dataInputStream.readFully(xmlNameBuffer);
			this.xmlName = new String(xmlNameBuffer,
					IECP1Command.STRING_ENCODING);
		} else {
			this.xmlName = "";
		}

		final int xmlAuthorLength = dataInputStream.readInt();
		if (xmlAuthorLength != 0xffffffff) {
			final byte[] xmlAuthorBuffer = new byte[xmlAuthorLength];
			dataInputStream.readFully(xmlAuthorBuffer);
			this.xmlAuthor = new String(xmlAuthorBuffer,
					IECP1Command.STRING_ENCODING);
		} else {
			this.xmlAuthor = "";
		}

		final int xmlDataLength = dataInputStream.readInt();
		if (xmlDataLength != 0xffffffff) {
			final byte[] xmlDataBuffer = new byte[xmlDataLength];
			dataInputStream.readFully(xmlDataBuffer);
			xmlData = xmlDataBuffer;
		} else {
			this.xmlData = new byte[0];
		}
	}

	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(CurrentXMLCommand.COMMAND_TYPE_ID);

		final byte[] xmlNameBuffer = this.xmlName
				.getBytes(IECP1Command.STRING_ENCODING);
		final byte[] xmlAuthorBuffer = this.xmlAuthor
				.getBytes(IECP1Command.STRING_ENCODING);

		dataOutputStream.writeInt(12 + xmlNameBuffer.length
				+ xmlAuthorBuffer.length + xmlData.length);

		if (this.xmlName.length() != 0) {
			dataOutputStream.writeInt(xmlNameBuffer.length);
			dataOutputStream.write(xmlNameBuffer);
		} else {
			dataOutputStream.writeInt(0xffffffff);
		}

		if (this.xmlAuthor.length() != 0) {
			dataOutputStream.writeInt(xmlAuthorBuffer.length);
			dataOutputStream.write(xmlAuthorBuffer);
		} else {
			dataOutputStream.writeInt(0xffffffff);
		}

		if (this.xmlData.length != 0) {
			dataOutputStream.writeInt(xmlData.length);
			dataOutputStream.write(xmlData);
		} else {
			dataOutputStream.writeInt(0xffffffff);
		}
		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public byte[] getXmlData() {
		return this.xmlData;
	}

	public void setXmlData(final byte[] xmlData) {
		if (xmlData == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlData' must not be null!");
		}
		this.xmlData = xmlData;
	}

	public String getXmlAuthor() {
		return this.xmlAuthor;
	}

	public void setXmlAuthor(final String xmlAuthor) {
		if (xmlAuthor == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlAuthor' must not be null!");
		}
		this.xmlAuthor = xmlAuthor;
	}

	public String getXmlName() {
		return this.xmlName;
	}

	public void setXmlName(final String xmlName) {
		if (xmlName == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlName' must not be null!");
		}
		this.xmlName = xmlName;
	}
}