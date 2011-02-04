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
 * This abstract class is the base of all devices that can be used during the
 * main phase of a Scan Module like motor axis and detector channels. Typically
 * something is done with a main phase device during the main phase, like
 * writing values into it for moving a motor axis or reading values from like 
 * it's done from a detector channel.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 */
public abstract class AbstractMainPhaseDevice extends AbstractDevice {
	
	/**
	 * The attribute that is holding the class-name of the device.
	 */
	private String className;
	
		
	/**
	 * The trigger of this device.
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
	 * This constructor constructs a new, very specific device with all
	 * attributes.
	 * 
	 * @param name The name of the device.
	 * @param id The id of the device.
	 * @param unit The unit of the device.
	 * @param options A list of options of the Device.
	 * 			(use 'null' for no options)
	 * @param parent The parent of this device.
	 * @param trigger
	 * @exception IllegalArgumentException if name == 'null'
	 * @exception IllegalArgumentException if id == 'null'
	 * @exception IllegalArgumentException if parent has an illegal type
	 */
	public AbstractMainPhaseDevice(final String name, final String id,
									final Unit unit, final List<Option> options,
									final AbstractMainPhaseDevice parent, 
									final Function trigger ) {
		super(name, id, unit, options, parent);
		this.trigger = trigger;
	}
	
	
	/**
	 * Returns the trigger of the device.
	 * @return 
	 */
	public Function getTrigger() {
		return this.trigger;
	}

	/**
	 * Sets the Trigger of the device.
	 * @param trigger A Trigger object.
	 */
	public void setTrigger(final Function trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Checks if this class can really be the parent of this device. 
	 * .
	 * @param parent The parent that should be set.
	 * @exception ParentNotAllowedException 
	 */
	@Override
	protected void setParent(final AbstractDevice parent) 
								throws ParentNotAllowedException {
		// TODO describe/explain exception thrown
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
	 * @exception IllegalArgumentException if className == 'null'
	 */
	public void setClassName(final String className) {
		if(className == null) {
			throw new IllegalArgumentException(
					"The parameter 'className' must not be null!");
		}
		this.className = className;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

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
