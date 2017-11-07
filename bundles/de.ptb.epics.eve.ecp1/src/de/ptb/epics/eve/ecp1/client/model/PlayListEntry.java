package de.ptb.epics.eve.ecp1.client.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayListEntry {
	private int id;
	private String name;
	private String author;
	private Date timeStamp;

	public PlayListEntry(
			final de.ptb.epics.eve.ecp1.commands.PlayListEntry playListEntry) {
		if (playListEntry == null) {
			throw new IllegalArgumentException(
					"The parameter 'playListEntry' must not be null!");
		}
		this.id = playListEntry.getId();
		this.name = playListEntry.getName();
		this.author = playListEntry.getAuthor();
		this.timeStamp = playListEntry.getTimeStamp();
	}

	public int getId() {
		return this.id;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public String getName() {
		return this.name;
	}

	public String getAuthor() {
		return this.author;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.29
	 */
	@Override
	public String toString() {
		return "PlayListEntry(" + this.id + ", " +
				this.name + ", " + 
				this.author + ", " + 
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
						this.timeStamp) + ")";
	}
}