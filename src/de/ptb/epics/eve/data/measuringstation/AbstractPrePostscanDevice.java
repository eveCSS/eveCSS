/*
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

/**
 * This abstract class is the base of all devices, that can be used in a 
 * pre and post scan phase of a Scan Module (e.g. Devices and Options).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 * 
 */
public abstract class AbstractPrePostscanDevice extends AbstractDevice {
	
	/**
	 * The className of this device. In opposite of the AbstractClassedDevices,
	 * the className of a AbstractPrePostscanDevice can be null.
	 */
	private String className;
	
	/**
	 * The display group of the AbstractPrePostscanDevice.
	 */
	private String displaygroup;
	
	/**
	 * The function description of this AbstractPrePostscanDevice
	 */
	private Function value;
	
	/**
	 * Used by inheriting classes to generate an empty
	 * AbstractPrePostscanDevice.
	 *
	 */
	public AbstractPrePostscanDevice() {
		super();
		this.className = null;
		this.displaygroup = null;
		this.value = null;
	}
	
	/**
	 * Used by inheriting classes to generate a AbstractPrePostscanDevice
	 * with specific attributes.
	 * 
	 * @param className A String object containing the className or null.
	 * @param displaygroup A String object containg the displaygroup or null.
	 * @param value A Function-object.
	 */
	public AbstractPrePostscanDevice(final String className, 
									  final String displaygroup,  
									  final Function value ) {
		super();
		this.className = className;
		this.displaygroup = displaygroup;
		this.value = value;
	}

	/**
	 * Gives back the className of the AbstractPrePostscanDevice.
	 * @return A String object, containing the className, or null.
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Returns the display group of this AbstractPrePostscanDevice.
	 * @return A String object, containing the display group, or null.
	 */
	public String getDisplaygroup() {
		return this.displaygroup;
	}
	
	/**
	 * Sets the className of this AbstractPrePostscanDevice.
	 * 
	 * @param className A String object, containing the name, or null.
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * Sets the display group of this AbstractPrePostscanDevice.
	 * 
	 * @param displaygroup A String object that contains the display group.
	 */
	public void setDisplaygroup(final String displaygroup) {
		this.displaygroup = displaygroup;
	}

	/**
	 * This method gives back the Function object of this 
	 * AbstractPrePoststandDevice.
	 *
	 * @return The Function-object of this AbstractPrePoststandDevice.
	 */		
	public Function getValue() {
		return this.value;
	}

	
	/**
	 * This methods sets the Function-object of this AbstractPrePostscanDevice.
	 * 
	 * @param value The new Function object.
	 */
	public void setValue(final Function value) {
		this.value = value;
	}

	/**
	 * Checks if a value is fitting to the constraints of this
	 * AbstractPrePostscanDevice.
	 * 
	 * @param value The value that should be checked as String object. 
	 * 			Must not be null.
	 * @return Returns 'true' if the value is okay and 'false' if not.
	 */
	public boolean isValuePossible(final String value) {
		return this.value!=null?this.value.isValuePossible( value ):true;

	}
	
	/**
	 * Checks if the AbstractPrePostscanDevice can only have discrete 
	 * values.
	 *
	 * @return Returns 'true' is the AbstractPrePostscanDevice only can have 
	 * 			discrete values.
	 */
	public boolean isDiscrete() {
		return this.value!=null?this.value.isDiscrete():false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((displaygroup == null) ? 0 : displaygroup.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( !super.equals( obj ) ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final AbstractPrePostscanDevice other = (AbstractPrePostscanDevice)obj;
		if( className == null ) {
			if( other.className != null ) {
				return false;
			}
		} else if( !className.equals( other.className ) ) {
			return false;
		}
		if( displaygroup == null ) {
			if( other.displaygroup != null ) {
				return false;
			}
		} else if( !displaygroup.equals( other.displaygroup ) ) {
			return false;
		}
		if( value == null ) {
			if( other.value != null ) {
				return false;
			}
		} else if( !value.equals( other.value ) ) {
			return false;
		}
		return true;
	}	
}