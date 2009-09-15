/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;


/**
 * 
 * This enum defines some special data types which are used inside of an EPICS enviornment.
 * It also provides some static methods which finds out if a value can exist at on this data type.
 * This definitions are methods are used by severeal classes of the Scan Modul Editor project.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 *
 */
public enum DataTypes {

	/**
	 * On/Off datatype is used at some Devices to switch them on or off.
	 */
	ONOFF,
	/**
	 * Open/Close datatype is used to open or close a device i.e. a beamshutter.
	 */
	OPENCLOSE,
	/**
	 * Normal integer datatype.
	 */
	INT, 
	/**
	 * Normal double datatype.
	 */
	DOUBLE,
	/**
	 * Normal String datatype.
	 */
	STRING;
	
	/**
	 * This method is used to find out if a value can exist unter this data type.
	 * 
	 * @param type The data type on which base the value should be checked i.e. DataTypes.OPENCLOSE or DataTypes.ONOFF.
	 * @param value The value that should be checked.
	 * @return The method returns true if the value is ok, false if it's not.
	 */
	public static boolean isValuePossible( final DataTypes type, final String value ) {
		
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter 'value' must not be null!" );
		}
		
		
		switch( type ) {
		
		case ONOFF:		return ( value.toUpperCase().equals( "ON" ) || value.toUpperCase().equals( "OFF" ) );
		
		case OPENCLOSE:	return ( value.toUpperCase().equals( "OPEN" ) || value.toUpperCase().equals( "CLOSE" ) );
		
		case INT:		try {
							Integer.parseInt( value );
						} catch( final NumberFormatException e ) {
							return false;
						}
						return true;
						
		case DOUBLE:	try {
							Double.parseDouble( value );
						} catch( final NumberFormatException e ) {
							return false;
						}
						return true;
						
		case STRING:	return true;
			
		
		}
		
		return false;
		
	}
	
	/**
	 * This static Method is translating a name for the data type like it's used in the
	 * measuring station description or the scan description into the correpondenting DataType.
	 * Possible values are: OpenClose, OnOff, int, double and string.
	 * 
	 * @param name The String that should be tranlated. Must not be null.
	 * @return The correspondenting DataType.
	 */
	public static DataTypes stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "OnOff" ) ) {
			return DataTypes.ONOFF;
		} else if( name.equals( "OpenClose" ) ) {
			return DataTypes.OPENCLOSE;
		} else if( name.equals( "int" ) ) {
			return DataTypes.INT;
		} else if( name.equals( "double" ) ) {
			return DataTypes.DOUBLE;
		} else if( name.equals( "string" ) ) {
			return DataTypes.STRING;
		} 
		return null;
		
	}
	
	/**
	 * This static method translates a DataType into a String, like it's used in the measuring
	 * station description or the scan description. 
	 * 
	 * @param type The type, that should be translated. Must not be null!
	 * @return The correpondentin string. Null if the Type was invalid.
	 */
	public static String typeToString( final DataTypes type ) {
		
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		switch( type ) {
			case ONOFF:
				return "OnOff";
			case OPENCLOSE:
				return "OpenClose";
			case INT:
				return "int";
			case DOUBLE:
				return "double";
			case STRING:
				return "string";
		}
		
		return null;
		
	}
	
	/**
	 * This static method give back an array of the possible comparison types of the given data type.
	 * 
	 * @see de.ptb.epics.eve.data.ComparisonTypes
	 * @param type The data type of which you want to have the possible comparison types.
	 * @return An array, which contains the possible comparisonTypes. Null, if the type was invalid.
	 */
	public static ComparisonTypes[] getPossibleComparisonTypes( final DataTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		switch( type ) {
			case ONOFF:
			case OPENCLOSE:
			case STRING:
				return new ComparisonTypes[]{ ComparisonTypes.EQ, ComparisonTypes.NE };
			case INT:
			case DOUBLE:
				return new ComparisonTypes[]{ ComparisonTypes.EQ, ComparisonTypes.NE, ComparisonTypes.GT, ComparisonTypes.LT };
		}
		return null;
	}
	
	/**
	 * This static method gives back if a comparision type is possible for a Datatype.
	 * 
	 * I.e. EQ and NE are working for a string but GT and LT makes no sense in that way.
	 * 
	 * @param dataType The Datatype
	 * @param comparisonType The comparison type that should be checked.
	 * @return Gives back 'true' if the comparision type if possible for the given datantype and 'false' if not.
	 */
	public static boolean isComparisonTypePossible( final DataTypes dataType, final ComparisonTypes comparisonType ) {
		ComparisonTypes[] comparisonTypes = DataTypes.getPossibleComparisonTypes( dataType );
		for( int i = 0; i < comparisonTypes.length; ++i ) {
			if( comparisonTypes[i] == comparisonType ) {
				return true;
			}
		}
		return false;
	}
	
}
