/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;


/**
 * This class describes a postscan behavior.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Postscan extends AbstractPostscanBehavior {

	/**
	 * If the value should be resetted to the same as it was before the scan modul begun.
	 */
	private boolean reset;
	
	/**
	 * Gives back if reset is setted.
	 * 
	 * @return Gives back true if the value should be resetted aber the scan modul.
	 */
	public boolean isReset() {
		return this.reset;
	}
	
	/**
	 * Sets the reset value. If you pass true here, the bahavior will forget the previous setted value.
	 * 
	 * @param reset True if you want that the value will be resetted.
	 */
	public void setReset( final boolean reset ) {
		this.reset = reset;
		if (reset) {
			super.setValue("");
		}
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.AbstractPrePostscanBehavior#setValue(java.lang.String)
	 */
	@Override
	public void setValue( final String value ) {
		this.reset = false;
		super.setValue( value );
	}
	
	/**
	 * Gives back if a value is set for this Postscan.
	 * 
	 * @return Returns 'true' if a value has been set and 'false' if not.
	 */
	public boolean isValue() {
		return (super.getValue() != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.AbstractPrePostscanBehavior#getModelErrors()
	 */
	@Override
	public List< IModelError> getModelErrors() {
		return this.reset?new ArrayList< IModelError >():super.getModelErrors();
	}
}
