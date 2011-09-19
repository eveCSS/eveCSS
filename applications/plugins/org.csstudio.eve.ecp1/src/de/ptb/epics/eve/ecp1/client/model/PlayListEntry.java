package de.ptb.epics.eve.ecp1.client.model;

public class PlayListEntry {

	private int id;
	private String name;
	private String author;
	
	public PlayListEntry( final de.ptb.epics.eve.ecp1.intern.PlayListEntry playListEntry ) {
		if( playListEntry == null ) {
			throw new IllegalArgumentException( "The parameter 'playListEntry' must not be null!" );
		}
		this.id = playListEntry.getId();
		this.name = playListEntry.getName();
		this.author = playListEntry.getAuthor();
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getAuthor() {
		return this.author;
	}
	
}
