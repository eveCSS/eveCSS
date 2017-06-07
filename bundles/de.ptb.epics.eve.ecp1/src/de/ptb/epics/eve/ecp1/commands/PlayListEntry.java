package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayListEntry {

	private int id;
	private String name;
	private String author;

	public PlayListEntry() {
		this.id = 0;
		this.name = "";
		this.author = "";
	}

	public PlayListEntry(final int id, final String name, final String author) {
		if (name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must noch be null!");
		}
		if (author == null) {
			throw new IllegalArgumentException(
					"The parameter 'author' must noch be null!");
		}
		this.id = id;
		this.name = name;
		this.author = author;
	}

	protected byte[] toByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(this.id);
		if (this.name.length() != 0) {
			final byte[] byteBuffer = this.name
					.getBytes(IECP1Command.STRING_ENCODING);
			dataOutputStream.writeInt(byteBuffer.length);
			dataOutputStream.write(byteBuffer);
		} else {
			dataOutputStream.writeInt(0xffffffff);
		}

		if (this.author.length() != 0) {
			final byte[] byteBuffer = this.author
					.getBytes(IECP1Command.STRING_ENCODING);
			dataOutputStream.writeInt(byteBuffer.length);
			dataOutputStream.write(byteBuffer);
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
					"The parameter 'author' must noch be null!");
		}
		this.author = author;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must noch be null!");
		}
		this.name = name;
	}
}