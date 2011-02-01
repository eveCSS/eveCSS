/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The class TypeValue is a very fundamental class in the datatype system of the Scan Modul Editor. Very often a device has more limitations in it's possible values than just the primitive datatypes. In this case you have a discrete amount of values like green, red and blue. So the TypeValue class gets intialized with the DataTypes.STRING and value "green red blue". Integer or Double values may have a range defined, 
 * @author  Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version  1.4
 * @see  de.ptb.epics.eve.data.measuringstation.ProcessVariable
 */
public class TypeValue {

	/**
	 * The basic primitive datatype on which this definition based.
	 * @uml.property  name="type"
	 * @uml.associationEnd  
	 */
	private final DataTypes type;
	
	
	/**
	 * This constructor is used if a TypeValue object should be initialized that has no further limitations than the base type.
	 * 
	 * @param type The basic primitive datatype on which this definition based.
	 */
	public TypeValue( final DataTypes type ) {
		
		this( type, null );
		
	}
	
	/**
	 * we have a range constraint
	 */
	boolean hasRange;
	
	/**
	 * value must be one of a set of discrete values
	 * @uml.property  name="isDiscrete"
	 */
	boolean isDiscrete;

	/**
	 * string list containing the discrete values or the range borders
	 */
	List<String> elements;

	/**
	 * This constructor is used if a TypeValue object should be initialized that has a limitation of the possible values.
	 * 
	 * @param type The basic primitive datatype on which this definition based.
	 * @param value The possible values seperated by a whitespace
	 */
	public TypeValue( final DataTypes type, final String values ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		elements = new ArrayList<String>();
		this.type = type;
		setValues(values);
		
	}
	
	/**
	 * Returns the datatype of this TypeValue object.
	 * @return  The datatype.
	 * @uml.property  name="type"
	 */
	public DataTypes getType() {
		return this.type;
	}
	
	/**
	 * Build a string with the discrete values or range specification.
	 * 
	 * @return the discrete values or range specification.
	 */
	public String getValues() {
		StringBuffer returnString = new StringBuffer();
		String token="";
		
		if (isDiscrete){
			token = ", ";
		}
		else if (hasRange){
			token = " to ";
		}
		else if (elements.size() == 1){
			return elements.get(0);
		}
		

		int count = 0;
		for (String string : elements){
			if (count > 0) returnString.append(token);
			returnString.append(string);
			++count;
		}
		return returnString.toString();
	}

	/**
	 * Sets the possible values of this TypeValue.
	 * - a string with comma-separated values
	 * - a range of int or doubles in the following format <startval>to<endval> 
	 * - an arbitrary string without a comma
	 * 
	 * @param values A String of the possible values
	 */
	public void setValues( final String values ) {

		hasRange = false;
		isDiscrete = false;
		if( values == null ) return;
		
		if (((type == DataTypes.INT)||(type == DataTypes.DOUBLE)) && values.contains("to")) {
			// we have a range
			String[] splits = values.split( "to" );
			if ( splits.length == 2){
				hasRange=true;
				try {
					if (type == DataTypes.INT){
						elements.add(Integer.toString(Integer.parseInt(splits[0].trim())));
						elements.add(Integer.toString(Integer.parseInt(splits[1].trim())));
					}
					else {
						elements.add(Double.toString(Double.parseDouble(splits[0].trim())));
						elements.add(Double.toString(Double.parseDouble(splits[1].trim())));
					}
				} catch( final NumberFormatException e ) {
					hasRange=false;
				}
			}
		}
		else if (values.contains(",")){
			// we have discrete values
			String[] splits = values.split( "," );
			for (String string : splits){
				string.trim().replace("\"", "");
				if (string.length() > 0) elements.add(string);
			}
			if (elements.size() > 0) isDiscrete = true;
		}
		else if (values.length() > 0){
			elements.add(values);
		}
	}

	/**
	 * This method gives back if this TypeValue is discrete.
	 * @return  'true' if this TypeValue is discrete, 'false' if not.
	 * @uml.property  name="isDiscrete"
	 */
	public boolean isDiscrete() {
		return isDiscrete;
	}
	
	/**
	 * If this TypeValue is discrete, this method gives back List of String that contains all possible values.
	 * 
	 * @return A List that contains all possible discrete values. If this TypeValue is not discrete null will be returned.
	 */
	public List<String> getDiscreteValues() {

		if( isDiscrete )
			return new ArrayList<String>(elements);
		else
			return null;

	}
	
	/**
	 * Finds out if a value is possible under the constraints of this TypeValue object.
	 * 
	 * @param value The value that should be checked
	 * @return Returns true if the value fits the constrains, false if not.
	 */
	public boolean isValuePossible( final String value ) {
		if (formatValue(value) == null )
			return false;
		else
			return true;
	}

	/**
	 * Return a string formatted to the corresponding DataTypes.
	 * If the string can be converted, return a well-formatted string, else return null.
	 * 
	 * @param value string to be formatted
	 * @return a formatted string or null
	 */
	public String formatValue(String value){
		
		String returnString = DataTypes.formatValue(type, value);

		if (returnString != null){
			if (isDiscrete) {
				if (!elements.contains(returnString))
					returnString = null;
			}
			else if (hasRange){
				if (type == DataTypes.INT){
					Integer intval = Integer.parseInt(returnString);
					if (!((Integer.parseInt(elements.get(0)) <= intval) && 
							(Integer.parseInt(elements.get(1)) >= intval))) {
						returnString = null;					
					}
				}
				else if (type == DataTypes.DOUBLE){
					Double dblval = Double.parseDouble(returnString);
					if (!((Double.parseDouble(elements.get(0)) <= dblval) && 
							(Double.parseDouble(elements.get(1)) >= dblval))) {
						returnString = null;					
					}			
				}
			}
		}
		return returnString;
	}

	/**
	 * Return a string formatted to the corresponding DataTypes or a valid default value.
	 * If the string can be converted, return a well-formatted string else a valid default value.
	 * 
	 * @param value string to be formatted
	 * @return a formatted string or null
	 */
	public String formatValueDefault(String value){
		
		String returnString = formatValue(value);
		if (returnString == null ){
			if (isDiscrete || hasRange)
				returnString = elements.get(0);
			else
				returnString = DataTypes.formatValueDefault(type, value);
		}
		return returnString;
	}

	/**
	 * Return a well-formatted string with a valid default value, which 
	 * is the low limit (range) or the first element (set) or the default for this data type
	 * 
	 * @return a default value
	 */
	public String getDefaultValue(){

		if (isDiscrete || hasRange)
			return elements.get(0);
		else {
			if (elements.size() > 0)
				return elements.get(0);
			else
				return DataTypes.getDefaultValue(type);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + (hasRange ? 1231 : 1237);
		result = prime * result + (isDiscrete ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals( final Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( obj == null ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final TypeValue other = (TypeValue) obj;
		if( elements == null ) {
			if( other.elements != null ) {
				return false;
			}
		} else if( !elements.equals( other.elements ) ) {
			return false;
		}
		if( hasRange != other.hasRange ) {
			return false;
		}
		if( isDiscrete != other.isDiscrete ) {
			return false;
		}
		if( type == null ) {
			if( other.type != null ) {
				return false;
			}
		} else if( !type.equals(other.type ) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public Object clone() {
		final TypeValue typeValue = new TypeValue( this.type );
		typeValue.elements = new ArrayList<String>( this.elements );
		typeValue.hasRange = this.hasRange;
		typeValue.isDiscrete = this.isDiscrete;
		return typeValue;
	}
}
