/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

/**
 * This class represents a Unit that is defined inside of a measuring station description.
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
	 * This attribute is holding the access which describes where the value can be received from.
	 */
	private Access access;

	/**
	 * This constructor construct a new Unit, with a empty String as value.
	 *
	 */
	public Unit() {
		this( "" );
	}
	
	/**
	 * This constructor constructs a new Unit with the given String as value.
	 * 
	 * @param value The value of the Unit. Must not be null!
	 */
	public Unit( final String value ) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter 'value' must not be null!" );
		}
		this.value = value;
	}
	
	/**
	 * This constructor constructs a new Unit with the given Access.
	 * 
	 * @param access The access from where the value can be received from. Must not be null!
	 */
	public Unit( final Access access ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null!" );
		}
		this.access = access;
	}
	
	/**
	 * Gives back if this unit is specified by an Access.
	 * 
	 * @return Returns 'true' if the unit is specified by an Access.
	 */
	public boolean isAccess() {
		return this.access != null;
	}
	
	/**
	 * Gevies back if the unit is specified by a String.
	 * 
	 * @return Returns 'true' if the unit is specified by a String.
	 */
	public boolean isValue() {
		return this.value != null;
	}
	
	/**
	 * Gives back the value of the Unit.
	 * 
	 * @return Returns a String object that contains the unit or null if the unit is specified by a ProcessVariable.
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Gives back the Access for this unit.
	 * 
	 * @return Returns a Access object or null if the unit is specified by a String.
	 */
	public Access getAccess() {
		return this.access;
	}
	
	/**
	 * Sets a Access as the source of this unit. If a value was setted before, the object will forget
	 * the value.
	 * 
	 * @param pv A Access object. Must not be null!
	 */
	public void setAccess( final Access access ) {
		this.access = access;
		this.value = null;
	}

	/**
	 * Sets a String a the Unit. If a ProcessVariable was setted before, the object will forget the Access.
	 * 
	 * @param value A String object, contating the unit. Must not be null!
	 */
	public void setValue( final String value ) {
		this.value = value;
		this.access = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

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

	
}
