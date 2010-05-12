/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * This interface must be implemented by every object that provides model update events.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public interface IModelUpdateProvider {

	/**
	 * This method is used to add a model update listener.
	 * 
	 * @param modelUpdateListener The model update listener.
	 * @return Returns 'true' if the update listener has been added.
	 */
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener );
	
	/**
	 * 
	 * This method removes a model update listener.
	 * 
	 * @param modelUpdateListener The model update listener.
	 * @return Return 'true' if the update listener has been added.
	 */
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener );
	
}
