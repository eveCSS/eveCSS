package de.ptb.epics.eve.data.measuringstation;

import java.util.List;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TypeValue;

/**
 * A Function is a complex description that can extend the description of an 
 * <code>Access</code> with discrete values or range values.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at- )ptb.de>
 *
 */
public class Function {

	/**
	 * The Access of the function.
	 */
	private Access access;
	
	/**
	 * value which is sent or received using access.
	 */
	private TypeValue value;
	
	/**
	 * This constructor constructs a new Function with a GET Access.
	 *
	 */
	public Function() {
		this(new Access(MethodTypes.GET));
	}
	
	/**
	 * Constructs a <code>Function</code> with the given <code>Access</code>.
	 * 
	 * @param access the <code>Access</code> for the Function
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public Function(final Access access) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null!");
		}
		this.access = access;
	}

	/**
	 * Constructs a <code>Function</code> with a given <code>Access</code> and 
	 * <code>TypeValue</code> description.
	 * 
	 * @param access the <code>Access</code>
	 * @param value the <code>TypeValue</code> that should be used
	 * @throws IllegalArgumentException if access is <code>null</code>
	 */
	public Function(final Access access, final TypeValue value) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null!");
		}
		this.access = access;
		this.value = value;
	}

	/**
	 * Returns the <code>Access</code> of the <code>Function</code>.
	 * 
	 * @return the <code>Access</code> of the Function.
	 */
	public Access getAccess() {
		return this.access;
	}

	/**
	 * Sets an <code>Access</code> for the <code>Function</code>.
	 * 
	 * @param access the <code>Access</code> that should be set.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setAccess(final Access access) {
		if(access == null) {
			throw new IllegalArgumentException(
					"The parameter 'access' must not be null!");
		}
		this.access = access;
	}

	/**
	 * Returns the <code>TypeValue</code> description for this 
	 * <code>Function</code>.
	 * 
	 * @return the <code>TypeValue</code> description for the 
	 * 			<code>Access</code>
	 */
	public TypeValue getValue() {
		return value;
	}

	/**
	 * Sets the <code>TypeValue</code> description for this 
	 * <code>Function</code>.
	 * 
	 * @param value the <code>TypeValue</code> description for this 
	 * 		   <code>Function</code>.
	 */
	public void setValue(final TypeValue value) {
		this.value = value;
	}
	
	/**
	 * Checks whether the values for this <code>Function</code> are discrete.
	 * 
	 * @return <code>true</code> if the values are discrete,
	 * 			<code>false</code> otherwise
	 */
	public boolean isDiscrete() {
		return this.value!=null ? this.value.isDiscrete() : false;
	}
	
	/**
	 * Checks whether the given value is valid for the <code>Function</code>.
	 * 
	 * @param value the value that should be checked
	 * @return <code>true</code> if the value is valid,
	 * 			<code>false</code> otherwise
	 */
	public boolean isValuePossible(final String value) {
		return this.value!=null 
				? this.value.isValuePossible(value) 
				: this.access.isValuePossible(value);
	}

	/**
	 * Returns a <code>String</code> with a valid value for the data type.
	 * If the value cannot be converted, return a default value
	 * 
	 * @param value the value that will be formatted
	 * @return a <code>String</code> with a valid value
	 */
	public String formatValueDefault(final String value) {
		return this.value!=null 
				? this.value.formatValueDefault(value) 
				: this.access.formatValueDefault(value);
	}

	/**
	 * Returns a <code>String</code> with a valid value for the data type.
	 * If the value cannot be converted, return null
	 * 
	 * @param value the value that will be formatted
	 * @return a <code>String</code> or null
	 */
	public String formatValue(final String value) {
		return this.value!=null 
				? this.value.formatValue(value) 
				: this.access.formatValue(value);
	}

	/**
	 * Returns a default value for the data type.
	 * 
	 * @return a <code>String</code> with a default value
	 */
	public String getDefaultValue() {
		return this.value!=null 
				? this.value.getDefaultValue() 
				: this.access.getDefaultValue();
	}

	/**
	 * 
	 * @return the type of the value object or the access object if there is 
	 * 			no value
	 */
	public DataTypes getType(){
		return this.value!=null ? this.value.getType():this.access.getType();
	}

	/**
	 * Returns a list with all possible values (if the <code>Function</code> is
	 * discrete)
	 * 
	 * @return a <code>List</code> containing all possible discrete values. 
	 * 			If this Function is not discrete null will be returned.
	 */
	public List <String> getDiscreteValues() {
		return this.value.getDiscreteValues();
	}
	
	/**
	 * 
	 * @return <code>true</code> , 
	 * 			<code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		// TODO comment JavaDoc
		if (access != null)
			return access.isReadOnly();
		else
			return false;
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
		Function other = (Function) obj;
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
	 * @return a copy of the calling object
	 */
	@Override
	public Object clone() {
		return new Function((Access)
				(this.access!=null ? this.access.clone() : null), 
				(TypeValue)(this.value!=null ? this.value.clone() : null));
	}
}