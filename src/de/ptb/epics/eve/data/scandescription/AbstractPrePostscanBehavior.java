/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.Iterator;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class is the basic of all pre- and postscan behaviors. It's implementing
 * the setting of a value and testing if the value is correct for this kind of device
 * or option.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public abstract class AbstractPrePostscanBehavior extends AbstractBehavior {

	/**
	 * The value of the AbstractPrePostscanDevice.
	 */
	private String value;
	
	public AbstractPrePostscanBehavior() {
		this.value = "";
	}
	
	/**
	 * Gives back the value, that should be setted for this Behavior.
	 * 
	 * @return A String object. Never returs null!
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value, that should be setted to the AbstractPrePostscanDevice.
	 * 
	 * @param value A String-object, that's contating the value. Must not be null.
	 */
	public void setValue( final String value ) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter 'value' must not be null!" );
		}
		this.value = value;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the AbstractPrePostscanDevice that is controlled by this behavior.
	 * 
	 * @return
	 */
	public AbstractPrePostscanDevice getAbstractPrePostscanDevice() {
		return (AbstractPrePostscanDevice)this.getAbstractDevice();
	}
	
	/**
	 * Sets the AbstractPrePostscanDevice that is controlled by this behavior.
	 * 
	 * @param abstractPrePostscanDevice The device that should be controlled by this behavior. Must not be null!
	 */
	public void setAbstractPrePostscanDevice( final AbstractPrePostscanDevice abstractPrePostscanDevice ) {
		if( abstractPrePostscanDevice == null ) {
			throw new IllegalArgumentException( "The parameter 'abstractPrePostscanDevice' must not be null!" );
		}
		this.abstractDevice = abstractPrePostscanDevice;
	}
	
	/**
	 * Finds out if the controlled device is a option.
	 * 
	 * @return Returns true if the device is an option and false if it's not.
	 */
	public boolean isOption() {
		return this.getAbstractDevice() instanceof Option;
	}
	
	/**
	 * Finds out if the controlled device is a Device.
	 * 
	 * @return  Returns true if the device is an Device and false if it's not.
	 */
	public boolean isDevice() {
		return this.getAbstractDevice() instanceof Device;
	}
	
	/**
	 * Finds out if a value is possible at this behavior.
	 * 
	 * @param value A String that should be checked. Must not be null!
	 * @return Return true if the value is possible and false if not.
	 */
	public boolean isValuePossible( final String value ) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter'value' must not be null!" );
		}
		return ((AbstractPrePostscanDevice)this.getAbstractDevice()).isValuePossible( value );
	}
	
}
