package de.ptb.epics.eve.data.measuringstation.event;

/**
 * An event has a unique id, a human readable name and it can be 
 * lexicographically compared to other events.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public abstract class Event implements Comparable<Event> {
	
	/**
	 * Returns the id of the <code>Event</code>.
	 * 
	 * @return id
	 */
	public abstract String getId();
	
	/**
	 * Returns the name of the <code>Event</code>.
	 */
	public abstract String getName();

	/**
	 * {@inheritDoc}
	 * 
	 * Compares the names of the Events.
	 */
	@Override
	public int compareTo(Event o) {
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}
}