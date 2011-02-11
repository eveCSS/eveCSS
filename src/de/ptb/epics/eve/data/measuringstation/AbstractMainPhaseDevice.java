/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * The base of all devices that can be used during the main phase of a scan 
 * module like motor axis and detector channels.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public abstract class AbstractMainPhaseDevice extends AbstractDevice {
	
	/**
	 * The attribute that is holding the class-name of the device.
	 */
	private String className;
	
	/**
	 * The trigger of the device.
	 */
	private Function trigger;
	
	/**
	 * Should be called by the inheriting class to construct a new
	 * AbstractMainPhaseDevice. The trigger will be set to null.
	 */
	public AbstractMainPhaseDevice() {
		super();
		this.trigger = null;
		this.className = "";
	}
	
	/**
	 * Should be called by the inheriting class to construct a new
	 * AbstractMainPhaseDevice with a given trigger object.
	 *  
	 * @param trigger A Trigger object or null.
	 */
	public AbstractMainPhaseDevice(final Function trigger) {
		super();
		this.trigger = trigger;
	}
	
	/**
	 * Constructs a new, very specific device with all attributes.
	 * 
	 * @param name The name of the device.
	 * @param id The id of the device.
	 * @param unit The code>Unit</code> of the device.
	 * @param options A <code>List</code> of <code>Option</code>s of the device.
	 * 			(use 'null' for no options)
	 * @param parent The parent of this device.
	 * @param trigger 
	 * @throws IllegalArgumentException if name or id are <code>null</code>
	 * @throws IllegalArgumentException if parent has an illegal type
	 */
	public AbstractMainPhaseDevice(final String name, final String id,
									final Unit unit, final List<Option> options,
									final AbstractMainPhaseDevice parent, 
									final Function trigger) {
		super(name, id, unit, options, parent);
		this.trigger = trigger;
	}
	
	
	/**
	 * Returns the trigger of the device.
	 * 
	 * @return the Trigger of the device
	 */
	public Function getTrigger() {
		return this.trigger;
	}

	/**
	 * Sets the Trigger of the device.
	 * 
	 * @param trigger A Trigger object.
	 */
	public void setTrigger(final Function trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Checks if this class can really be the parent of this device. 
	 * .
	 * @param parent The parent that should be set.
	 * @throws ParentNotAllowedException 
	 */
	@Override
	protected void setParent(final AbstractDevice parent) 
								throws ParentNotAllowedException {
		// TODO describe/explain exception thrown super doesn't throw anything!
		// It will throw a ParentNotAllowedException
		// if there was passes a wrong device type
		super.setParent( parent );
	}
	
	/**
	 * Returns the class of the device.
	 * 
	 * @return A String containing the name of the class of the device.
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Sets the className of the device.
	 * 
	 * @param className A String object contains the className.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setClassName(final String className) {
		if(className == null) {
			throw new IllegalArgumentException(
					"The parameter 'className' must not be null!");
		}
		this.className = className;
	}

	/**
	 * Mystical Math
	 * 
	 * @return a fancy number yet has to be explained
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and the calling object are equal.
	 * 
	 * @param obj the <code>Object</code> to be checked
	 * @return <code>true</code> if equal, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(final Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !super.equals( obj ) ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final AbstractMainPhaseDevice other = (AbstractMainPhaseDevice)obj;
		if( className == null ) {
			if( other.className != null ) {
				return false;
			}
		} else if( !className.equals( other.className ) ) {
			return false;
		}
		return true;
	}
}