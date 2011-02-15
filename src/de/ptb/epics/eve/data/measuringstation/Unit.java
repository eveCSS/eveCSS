/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

/**
 * This class represents a unit that is defined inside of a measuring station 
 * description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Unit {

	/**
	 * This attribute is holding the value of the unit (lbs. m, mm, ft., etc.).
	 */
	private String value;
	
	/**
	 * This attribute is holding the access which describes where the value can 
	 * be received from.
	 */
	private Access access;

	/**
	 * Constructs an <code>Unit</code>, with an empty <code>String</code> as 
	 * value.
	 *
	 */
	public Unit() {
		this("");
	}
	
	/**
	 * Constructs an <code>Unit</code> with the given <code>String</code> as 
	 * value.
	 * 
	 * @param value The value of the Unit. Must not be null!
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public Unit(final String value) {
		if(value == null) {
			throw new IllegalArgumentException(
					"The parameter 'value' must not be null!");
		}
		this.value = value;
	}
	
	/**
	 * Constructs an <code>Unit</code> with the given <code>Access</code>.
	 * 
	 * @param access the <code>Access</code> where the value is received from
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public Unit(final Access access) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null!");
		}
		this.access = access;
	}
	
	/**
	 * Checks whether the <code>Unit</code> is specified by an 
	 * <code>Access</code>.
	 * 
	 * @return <code>true</code> if the unit is specified by an 
	 * 			<code>Access</code>, <code>false</code> otherwise
	 */
	public boolean isAccess() {
		return this.access != null;
	}
	
	/**
	 * Checks whether the <code>Unit</code> is specified by a 
	 * <code>String</code>.
	 * 
	 * @return <code>true</code> if the unit is specified by a 
	 * 			<code>String</code>, <code>false</code> otherwise
	 */
	public boolean isValue() {
		return this.value != null;
	}
	
	/**
	 * Returns the value of the <code>Unit</code>.
	 * 
	 * @return a <code>String</code> containing the value of the 
	 * 			<code>Unit</code> or <code>null</code> if the unit is specified 
	 * 			by a process variable (PV)
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Returns the <code>Access</code> of the <code>Unit</code>.
	 * 
	 * @return an <code>Access</code> or <code>null</code> if the unit is 
	 * 			specified by a <code>String</code>
	 */
	public Access getAccess() {
		return this.access;
	}
	
	/**
	 * Sets an <code>Access</code> as the source of this unit. If a value was 
	 * set before, the object will 'forget' the value.
	 * 
	 * @param pv an <code>Access</code>. Must not be null!
	 */
	public void setAccess(final Access access) {
		this.access = access;
		this.value = null;
	}

	/**
	 * Sets a <code>String</code> as the Unit. If a process variable was set 
	 * before, the object will 'forget' the Access.
	 * 
	 * @param value a <code>String</code> containing the value of the unit. 
	 * 		   Must not be null!
	 */
	public void setValue(final String value) {
		this.value = value;
		this.access = null;
	}

	/**
	 * @return a hash
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Unit other = (Unit) obj;
		if (access == null) {
			if (other.access != null) {
				return false;
			}
		} else if (!access.equals(other.access)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	/**
	 * Clones the calling object.
	 * 
	 * @return a copy of the calling <code>Object</code>
	 */
	@Override
	public Object clone() {
		// TODO Explain !!!
		return this.access!=null 
				? new Unit((Access)this.access.clone()) 
				: new Unit(this.value); 
	}
}