package de.ptb.epics.eve.data.scandescription;

import de.ptb.epics.eve.data.measuringstation.Event;

/**
 * This class represents the start event of a chain.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class StartEvent {

	/**
	 * The event that starts the chain.
	 */
	private Event event;
	
	/**
	 * The chain that is started by the start event.
	 */
	private Chain chain;
	
	/**
	 * The connector that describes the connection between start event and first scan module.
	 */
	private Connector connector;
	
	/**
	 * The x position of the start event.
	 */
	private int x;
	
	/**
	 * The y position of the start event.
	 */
	private int y;
	
	/**
	 * This constructor creates a new start event.
	 */
	public StartEvent() {
		this.event = null;
		this.chain = null;
		this.x = 10;
		this.y = 10;
	}
	
	/**
	 * This constructor creates a new start event with a given event and chain. 
	 * 
	 * @param event The chain of the start event.
	 * @param chain The chain that is started by this start event.
	 */
	public StartEvent( final Event event, final Chain chain ) {
		this.event = event;
		this.chain = chain;
		this.x = 10;
		this.y = 10;
	}
	
	/**
	 * This constructor creates a new start event with a given event, chain, x, and y position.
	 * 
	 * @param event The chain of the start event.
	 * @param chain The chain that is started by this start event.
	 * @param x The x position for the start event.
	 * @param y The y position for the start event.
	 */
	public StartEvent( final Event event, final Chain chain, final int x, final int y ) {
		this.event = event;
		this.chain = chain;
		this.x = x;
		this.y = y;
	}

	/**
	 * This method return the chain that is started by the start event.
	 * 
	 * @return The chain that is started by the start event.
	 */
	public Chain getChain() {
		return this.chain;
	}

	/**
	 * This method sets the chain that is started by the start event.
	 * 
	 * @param chain The chain that is started by the start event.
	 */
	public void setChain( final Chain chain ) {
		this.chain = chain;
	}

	/**
	 * This method returns the event that starts this start event.
	 * 
	 * @return The event that starts this start event.
	 */
	public Event getEvent() {
		return this.event;
	}

	/**
	 * This method sets the events that starts this start event.
	 * 
	 * @param event The event that starts this start event.
	 */
	public void setEvent( final Event event ) {
		this.event = event;
	}

	/**
	 * This method returns the connector that connects this start event to a scan module.
	 * 
	 * @return The connector that connects this start element to a scan module.
	 */
	public Connector getConnector() {
		return this.connector;
	}

	/**
	 * This method sets the connector that connects this start event so a scan module.
	 *
	 * @param connector The connector that connects this start element to a scan module.
	 */
	public void setConnector( final Connector connector ) {
		this.connector = connector;
	}

	/**
	 * This method returns the x position of the start event.
	 * 
	 * @return The x position of the start event.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * This method sets the x position of the start event.
	 * 
	 * @param x The new x position of the start event.
	 */
	public void setX( final int x ) {
		this.x = x;
	}

	/**
	 * This method returns the y position of the start event.
	 * 
	 * @return The current y position of the start event.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * This method sets the y position of the start event.
	 * 
	 * @param y The new y position for the start event.
	 */
	public void setY( final int y ) {
		this.y = y;
	}
	
}
