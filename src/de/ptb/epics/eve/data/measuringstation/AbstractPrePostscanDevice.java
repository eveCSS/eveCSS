/*
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

/**
 * This abstract class is the base of all devices, that can be used in a 
 * pre and post scan phase of a scan module (e.g. <code>Device</code>s and 
 * <code>Option</code>s).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 * 
 */
public abstract class AbstractPrePostscanDevice extends AbstractDevice {
	
	/**
	 * The class name of the device.
	 */
	private String className;
	
	/**
	 * The display group.
	 */
	private String displaygroup;
	
	/**
	 * The function description.
	 */
	private Function value;
	
	/**
	 * Constructs an empty <code>AbstractPrePostscanDevice</code>.
	 */
	public AbstractPrePostscanDevice() {
		super();
		this.className = null;
		this.displaygroup = null;
		this.value = null;
	}
	
	/**
	 * Constructs an <code>AbstractPrePostscanDevice</code> with specific 
	 * attributes.
	 * 
	 * @param className a <code>String</code> containing the class name
	 * @param displaygroup a <code>String</code> containing the display group
	 * @param value a <code>Function</code>
	 */
	public AbstractPrePostscanDevice(final String className, 
									  final String displaygroup,  
									  final Function value) {
		super();
		this.className = className;
		this.displaygroup = displaygroup;
		this.value = value;
	}

	/**
	 * Returns the class name.
	 * 
	 * @return A <code>String</code> containing the class name
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Returns the display group.
	 * 
	 * @return A <code>String</code> containing the display group
	 */
	public String getDisplaygroup() {
		return this.displaygroup;
	}
	
	/**
	 * Sets the className.
	 * 
	 * @param className a <code>String</code> containing the name
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * Sets the display group.
	 * 
	 * @param displaygroup a <code>String</code> containing the display group
	 */
	public void setDisplaygroup(final String displaygroup) {
		this.displaygroup = displaygroup;
	}

	/**
	 * Returns the <code>Function</code>.
	 *
	 * @return the <code>Function</code>
	 */		
	public Function getValue() {
		return this.value;
	}

	/**
	 * Sets the <code>Function</code>.
	 * 
	 * @param value The new <code>Function</code>.
	 */
	public void setValue(final Function value) {
		this.value = value;
	}

	/**
	 * Checks whether a value is valid for this 
	 * <code>AbstractPrePostscanDevice</code>.
	 * 
	 * @param value the value that should be checked 
	 * 			Must not be null.
	 * @return <code>true</code> if the value is valid,
	 * 			<code>false</code> otherwise
	 */
	public boolean isValuePossible(final String value) {
		// TODO EXPLAIN !!!
		return this.value!=null ? this.value.isValuePossible(value) : true;
	}
	
	/**
	 * Checks whether the <code>AbstractPrePostscanDevice</code> must have 
	 * discrete values.
	 *
	 * @return <code>true</code> if only discrete values are valid,
	 * 			<code>false</code> otherwise 
	 */
	public boolean isDiscrete() {
		// TODO Explain !!!
		return this.value!=null?this.value.isDiscrete():false;
	}

	/**
	 * 
	 * @return a fancy value yet unexplained
	 */
	@Override
	public int hashCode() {
		// TODO Explain !!!
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((displaygroup == null) ? 0 : displaygroup.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects equal,
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final AbstractPrePostscanDevice other = (AbstractPrePostscanDevice)obj;
		if(className == null) {
			if(other.className != null) {
				return false;
			}
		} else if(!className.equals(other.className)) {
			return false;
		}
		if(displaygroup == null) {
			if(other.displaygroup != null) {
				return false;
			}
		} else if(!displaygroup.equals( other.displaygroup)) {
			return false;
		}
		if(value == null) {
			if(other.value != null) {
				return false;
			}
		} else if(!value.equals( other.value)) {
			return false;
		}
		return true;
	}	
}