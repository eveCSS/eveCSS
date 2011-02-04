/*
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a Device.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class Device extends AbstractPrePostscanDevice {

	/**
	 * This method is just overriding the getParent Method of AbstractDevice,
	 * because a Device can have no parent. This method is simply doing
	 * nothing.
	 * 
	 * @param parent Pass whatever you want. It will not be used at all!
	 */
	@Override
	protected void setParent(final AbstractDevice parent) 
							throws ParentNotAllowedException {
	}
	
	/**
	 * Returns a copy of the calling Device object
	 * 
	 *@return a clone of the current Device
	 */
	@Override
	public Object clone() {
		final Device device = new Device();
		
		device.setClassName(this.getClassName());
		device.setDisplaygroup(this.getDisplaygroup());
		device.setValue((Function)
				(this.getValue()!=null?this.getValue().clone():null));
		
		device.setName(this.getName());
		device.setId(this.getID());
		device.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		
		return device;
	}
}