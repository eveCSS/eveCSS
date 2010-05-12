/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This abstract class is the base of all devices that can be used during the main phase of
 * a Scan Modul like motor axis and detector channels. Typically somthing is done with a main
 * phase device during the main phase, like writing values into it for moving a motor axis
 * or reading values from like it's done from a detector channel.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 */
public abstract class AbstractMainPhaseDevice extends AbstractDevice {
	
	/**
	 * The trigger of this device.
	 */
	private Function trigger;
	
	/**
	 * This constructors should be called by the inherting class to construct a new AbstractMainPhaseDevice.
	 * This constrcutor is calling the standard constructor of AbstractDevice. The trigger will be set to
	 * null by using this constructor.
	 *
	 */
	public AbstractMainPhaseDevice() {
		super();
		this.trigger = null;
	}
	
	/**
	 * This constructors should be called by the inherting class to construct a new AbstractMainPhaseDevice
	 * with a given trigger object. This constrcutor is calling the standard constructor of AbstractDevice.
	 *  
	 * @param trigger A Trigger object or null.
	 */
	public AbstractMainPhaseDevice( final Function trigger ) {
		super();
		this.trigger = trigger;
	}
	
	/**
	 * This constructors constructs a new, very specific device with all attributes.
	 * 
	 * @param name The name of the device. Must not be null! Use a empty String if you want no name for the device.
	 * @param id The id of the device. Must not be null! Use a empty String if you want no id for the device.
	 * @param unit The unit of the device. May be null.
	 * @param options A list of options of the Device. If it's null, a new empty list will be created.
	 * @param parent The parent of this device.
	 * @param trigger
	 */
	public AbstractMainPhaseDevice( final String name, final String id, final Unit unit, final List<Option> options, final AbstractClassedDevice parent, final Function trigger ) {
		super( name, id, unit, options, parent );
		this.trigger = trigger;
	}
	
	
	/**
	 * Gives back the trigger of the device.
	 * @return 
	 */
	public Function getTrigger() {
		return this.trigger;
	}

	/**
	 * Sets the Trigger of the device.
	 * @param trigger A Trigger object.
	 */
	public void setTrigger( final Function trigger ) {
		this.trigger = trigger;
	}
	
	/**
	 * This method is overriding the setParent Method of the super class. This method contains some
	 * checks if this class can really be the parent of this device. It will throw a ParentNotAllowedException
	 * if there was passes a wrong device type.
	 * 
	 * @param parent The parent that should be settet. 
	 */
	@Override
	protected void setParent( final AbstractDevice parent ) throws ParentNotAllowedException {
		if( parent != null ) {
			if( !( parent instanceof AbstractClassedDevice ) ) {
				throw new ParentNotAllowedException( "Your class is directly or indirectly inhereting from AbstractMainPhaseDevice. The parent of an AbstractMainPhaseDevice can only be a AbstractClassedDevice. Please fix your implementation to check this constraint!" );
			}
		}
		super.setParent( parent );
	}
	
}
