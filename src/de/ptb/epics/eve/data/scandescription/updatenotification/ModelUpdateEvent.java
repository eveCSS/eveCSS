/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * This class represents an update in the scan description model.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class ModelUpdateEvent {

	/**
	 * The sender of the update message.
	 */
	private final Object sender;
	
	/**
	 * An option message of the update.
	 */
	private final Object message;
	
	/**
	 * This constructor creates a new update event.
	 * 
	 * @param sender The sender object of the update message. Must not be null.
	 * @param message An optional message. Maybe null.
	 */
	public ModelUpdateEvent( final Object sender, final Object message ) {
		if( sender == null ) {
			throw new IllegalArgumentException( "ModelUpdateEvent: 'sender' must not be null!" );
		}
		this.sender = sender;
		this.message = message;
	}
	
	/**
	 * Gives back the sender object of this message.
	 * 
	 * @return The sender of the message. Never null.
	 */
	public Object getSender() {
		return this.sender;
	}
	
	/**
	 * This method gives back if a message is appended.
	 * 
	 * @return Gives back 'true' if a message is appended and 'false' if not.
	 */
	public boolean isMessageAppended() {
		return this.message != null;
	}
	
	/**
	 * This method gives back the message of this update event.
	 * 
	 * @return The message or null if no message is set.
	 */
	public Object getMessage() {
		return this.message;
	}
}
