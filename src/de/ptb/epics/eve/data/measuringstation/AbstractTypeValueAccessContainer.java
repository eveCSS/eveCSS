package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * The <code>AbstractTypeValueAccessContainer</code> is the base class of all 
 * classes that contain a {@link de.ptb.epics.eve.data.TypeValue} and an 
 * {@link de.ptb.epics.eve.data.measuringstation.Access}. 
 * The basic 'not null' validations for 
 * {@link de.ptb.epics.eve.data.measuringstation.Access} are implemented in 
 * this class.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public abstract class AbstractTypeValueAccessContainer {

	// the type value 
	private TypeValue dataType;
	
	// the Access object
	private Access access;
	
	/**
	 * Constructs an empty <code>AbstractTypeValueAccessContainer</code>.
	 * 
	 * @param access the {@link de.ptb.epics.eve.data.measuringstation.Access}
	 */
	public AbstractTypeValueAccessContainer(final Access access) {
		this(access, null);
	}
	
	/**
	 * Constructs an <code>AbstractTypeValueAccessContainer</code>.
	 * 
	 * @param access the {@link de.ptb.epics.eve.data.measuringstation.Access}
	 * @param dataType the {@link de.ptb.epics.eve.data.TypeValue}
	 * @throws IllegalArgumentException if <code>access</code> is 
	 * 			<code>null</code>
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
	 * Returns the {@link de.ptb.epics.eve.data.TypeValue}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.TypeValue}
	 */
	public TypeValue getDataType() {
		return this.dataType;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Access}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.Access}
	 */
	public Access getAccess() {
		return this.access;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.measuringstation.Access}.
	 * 
	 * @param access the {@link de.ptb.epics.eve.data.measuringstation.Access} 
	 * 			that should be set
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
	 * Sets the {@link de.ptb.epics.eve.data.TypeValue}.
	 * 
	 * @param dataType the {@link de.ptb.epics.eve.data.TypeValue} that should 
	 * 			be set
	 */
	public void setDataType(final TypeValue dataType) {
		this.dataType = dataType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
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