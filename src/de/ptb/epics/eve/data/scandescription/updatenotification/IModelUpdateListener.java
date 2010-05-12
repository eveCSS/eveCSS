/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * This interface is implemented by classes that listen to model changes of the scan description.
 * 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld ( -at- ) ptb.de>
 *
 */
public interface IModelUpdateListener {

	/**
	 * This method gets called if a model element has changed.
	 * 
	 * @param modelUpdateEvent
	 */
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent );
	
}
