/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.Iterator;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;


/**
 * Because a pause event have one more field, that is indicating that all operations
 * will be continue if the conditions of event are not true anymore, PauseEvent extends
 * ControlEvent to provide the additional field.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class PauseEvent extends ControlEvent {

	/**
	 * The attribute.
	 */
	private boolean continueIfFalse;

	/**
	 * Gives back if the attribute is setted or not.
	 * 
	 * @return Returns 'true' if its set and 'false' if not.
	 */
	public boolean isContinueIfFalse() {
		return continueIfFalse;
	}

	/**
	 * This constructor creates a new pause event.
	 * 
	 * @param type The type of the pause event.
	 */
	public PauseEvent( final EventTypes type ) {
		super( type );
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param continueIfFalse Set 'true' if you want to continue if false.
	 */
	public void setContinueIfFalse( boolean continueIfFalse ) {
		this.continueIfFalse = continueIfFalse;
		Iterator<IModelUpdateListener> it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( this, ControlEventMessageEnum.UPDATED ) ) );
		}
	}
	
	
	
}
