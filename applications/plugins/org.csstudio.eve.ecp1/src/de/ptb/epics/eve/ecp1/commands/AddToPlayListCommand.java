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

public class AddToPlayListCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0201;

	private String name;
	private String author;
	private byte[] xmlData;

	public AddToPlayListCommand(final String name, final String author,
			final byte[] xmlData) {
		if (name == null) {
			throw new IllegalArgumentException(
					"The paramter 'name' must not be null!");
		}
		if (author == null) {
			throw new IllegalArgumentException(
					"The paramter 'author' must not be null!");
		}
		if (xmlData == null) {
			throw new IllegalArgumentException(
					"The paramter 'xmlData' must not be null!");
		}
		this.name = name;
		this.author = author;
		this.xmlData = xmlData;
	}

	public AddToPlayListCommand(final byte[] byteArray) throws IOException,
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
		if (commandTypeID != AddToPlayListCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					AddToPlayListCommand.COMMAND_TYPE_ID);
		}

		final int length = dataInputStream.readInt();

		final int nameLength = dataInputStream.readInt();
		if (nameLength != 0xffffffff) {
			final byte[] nameByteArray = new byte[nameLength];
			dataInputStream.readFully(nameByteArray);
			this.name = new String(nameByteArray, IECP1Command.STRING_ENCODING);
		} else {
			this.name = "";
		}

		final int authorLength = dataInputStream.readInt();
		if (authorLength != 0xffffffff) {
			final byte[] authorByteArray = new byte[authorLength];
			dataInputStream.readFully(authorByteArray);
			this.author = new String(authorByteArray,
					IECP1Command.STRING_ENCODING);
		} else {
			this.author = "";
		}

		final int xmlDataLength = dataInputStream.readInt();
		if (xmlDataLength != 0xffffffff) {
			final byte[] xmlData = new byte[xmlDataLength];
			dataInputStream.readFully(xmlData);
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
		;
		dataOutputStream.writeChar(AddToPlayListCommand.COMMAND_TYPE_ID);

		final byte[] nameByteBuffer = this.name
				.getBytes(IECP1Command.STRING_ENCODING);
		final byte[] authorByteBuffer = this.author
				.getBytes(IECP1Command.STRING_ENCODING);
		int length = 12;
		if (this.name.length() != 0) {
			length += nameByteBuffer.length;
		}

		if (this.author.length() != 0) {
			length += authorByteBuffer.length;
		}
		length += xmlData.length;

		dataOutputStream.writeInt(length);

		if (this.name.length() != 0) {
			dataOutputStream.writeInt(nameByteBuffer.length);
			dataOutputStream.write(nameByteBuffer);
		} else {
			dataOutputStream.writeInt(0xffffffff);
		}

		if (this.author.length() != 0) {
			dataOutputStream.writeInt(authorByteBuffer.length);
			dataOutputStream.write(authorByteBuffer);
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

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(final String author) {
		if (author == null) {
			throw new IllegalArgumentException(
					"The paramter 'author' must not be null!");
		}
		this.author = author;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"The paramter 'name' must not be null!");
		}
		this.name = name;
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
}