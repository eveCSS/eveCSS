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

	// TODO correct Datatypes onoff and openclose
	/**
	 * On/Off is used to display On or Off for data of type integer.
	 */
	ONOFF,
	/**
	 * Open/Close is used to display Open or Close for data of type integer.
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
	STRING,
	
	/**
	 * DateTime may be an absolute datetime spec: yyyy-mm-dd hh:mm:ss.sss
	 * or an absolute time without date hh:mm:ss.sss which assumes today as date
	 * or an relative time hh:mm:ss.sss or an relative time ss.sss
	 * Examples: 2009-10-01 17:09:20.000 (abs) valid absolute datetime
	 *           2009-10-01 17:09:20.000 (rel) invalid relative datetime
	 *                      17:09:20.000 (abs) valid absolute datetime assuming date today
	 *                      17:09:20.000 (rel) valid relative time (duration of 1580 secs)
	 *                      1580.0 		 (rel) valid relative time (duration of 1580 secs)
	 */
	DATETIME;
	
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
		case DATETIME:	
						if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[0-9.]*"))
							return true;
						else if (value.matches("\\d{2}:\\d{2}:\\d{2}[0-9.]*"))
							return true;
						else
							return isValuePossible(DOUBLE, value);
		
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
		} else if( name.equals( "datetime" ) ) {
			return DataTypes.DATETIME;
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
			case DATETIME:
				return "datetime";
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
			case DATETIME:
			case INT:
			case DOUBLE:
				return new ComparisonTypes[]{ ComparisonTypes.EQ, ComparisonTypes.NE, ComparisonTypes.GT, ComparisonTypes.LT };
		}
		return null;
	}
	
	/**
	 * This static method gives back if a comparison type is possible for a Datatype.
	 * 
	 * I.e. EQ and NE are working for a string but GT and LT makes no sense in that way.
	 * 
	 * @param dataType The Datatype
	 * @param comparisonType The comparison type that should be checked.
	 * @return Gives back 'true' if the comparison type if possible for the given datatype and 'false' if not.
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
