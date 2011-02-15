/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * The <code>AbstractTypeValueAccessContainer</code> is the base class of all 
 * classes that contain a <code>TypeValue</code> and an <code>Access</code>. 
 * The basic 'not null' validations for <code>Access</code> are implemented in 
 * this class.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 * @see de.ptb.epics.eve.data.measuringstation.Access
 * @see de.ptb.epics.eve.data.measuringstation.Event
 */
public abstract class AbstractTypeValueAccessContainer {

	/**
	 * The TypeValue object of the <code>AbstractTypeValueAccessContainer</code> 
	 */
	private TypeValue dataType;
	
	/**
	 * The Access object of the <code>AbstractTypevalueAccessContainer</code>  
	 */
	private Access access;
	
	/**
	 * Constructs an empty <code>AbstractTypeValueAccessContainer</code>.
	 */
	public AbstractTypeValueAccessContainer(final Access access) {
		this(access, null);
	}
	
	/**
	 * Constructs an <code>AbstractTypeValueAccessContainer</code> with 
	 * specific values. 
	 * 
	 * @param pv an <code>Access</code>
	 * @param dataType a <code>TypeValue</code>
	 * @throws IllegalArgumentException if access is <code>null</code>
	 */
	public AbstractTypeValueAccessContainer(final Access access, 
											final TypeValue dataType) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null");
		}
		this.access = access;
		this.dataType = dataType;
	}
	
	/**
	 * Returns the <code>TypeValue</code> of this container.
	 * 
	 * @return the <code>TypeValue</code>
	 */
	public TypeValue getDataType() {
		return this.dataType;
	}
	
	/**
	 * Returns the <code>Access</code> of this container.
	 * 
	 * @return the <code>Access</code>
	 */
	public Access getAccess() {
		return this.access;
	}
	
	/**
	 * Sets the <code>Access</code>.
	 * 
	 * @param access the <code>Access</code> to be set
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setAccess(final Access access) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null");
		}
		this.access = access;
	}
	
	/**
	 * Sets the <code>TypeValue</code>.
	 * 
	 * @param dataType the <code>TypeValue</code> to be set
	 */
	public void setDataType(final TypeValue dataType) {
		this.dataType = dataType;
	}

	/**
	 * some fancy math
	 */
	@Override
	public int hashCode() {
		// TODO Explain !
		final int prime = 31;
		int result = 1;
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling <code>Object</code> are equal.
	 * 
	 * @param obj the object to be checked
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final AbstractTypeValueAccessContainer other = 
					(AbstractTypeValueAccessContainer)obj;
		if(access == null) {
			if(other.access != null) {
				return false;
			}
		} else if(!access.equals( other.access)) {
			return false;
		}
		if(dataType == null) {
			if(other.dataType != null) {
				return false;
			}
		} else if(!dataType.equals( other.dataType)) {
			return false;
		}
		return true;
	}	
}