/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * 
 * The class AbstractClassedDevice adds the Attribute className to a Device and is the base for a motor
 * and detector.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * @see de.ptb.epics.eve.data.measuringstation.Motor
 * @see de.ptb.epics.eve.data.measuringstation.Detector
 */
public abstract class AbstractClassedDevice extends AbstractMainPhaseDevice {

	/**
	 * The attribute that is holding the class-name of the device.
	 */
	private String className;
	
	/**
	 * This constructor is used by inhereting classes to generate an empty AbstractClassedDevice, with no
	 * explicit class name. The attribute className will be initialized with an empty String. This construtor
	 * will call constructor without any parameters of AbstractDevice().
	 * 
	 * @see de.ptb.epics.eve.data.measuringstation.AbstractDevice
	 */
	public AbstractClassedDevice() {
		super();
		this.className = new String();
	}
	
	/**
	 * Give back the class of the device.
	 * 
	 * @return A String that is containing the name of the class of the device. It will never be null.
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Sets the className of the device.
	 * 
	 * @param className A String object contains the className. Must not be null!
	 */
	public void setClassName( final String className ) {
		if( className == null ) {
			throw new IllegalArgumentException( "The parameter 'className' must not be null!" );
		}
		this.className = className;
	}

	/**
	 * This method is just overriding the getParent Method of AbstractDevice, because all inhererting
	 * classes from AbstractClassedDevice can have no parent. This method is simly doing nothing.
	 * 
	 * @param parent Pass whatever you want. It will not be used at all!
	 */
	@Override
	protected void setParent( final AbstractDevice parent ) throws ParentNotAllowedException {
	}
	
}
