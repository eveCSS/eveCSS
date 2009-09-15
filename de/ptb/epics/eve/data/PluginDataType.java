/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

/**
 * This enum represents the datatypes for plugin variables. The datatypes are siminlar to the
 * datatypes that are defindes in the enum DataTypes. Additionally this enum has the types AXISID,
 * CHANNELID and DEVIDEID that indicates, that the plugin parameter is a channel, axis or device.
 * 
 * @author Stephan Rehfeld
 */
public enum PluginDataType {
	
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
	STRING,
	
	/**
	 * A String that must represents an identifier of an axis.
	 */
	AXISID,
	
	/**
	 * A String that must respresents an identifier of an channel.
	 */
	CHANNELID,
	
	/**
	 * A String that must respresents an identifier of an device.
	 */
	DEVICEID;
	
	/**
	 * This method is used to find out if a value can exist unter this data type.
	 * 
	 * @param type The type on which base the value should be checked i.e. PluginDataType.OPENCLOSE or PluginDataType.ONOFF.
	 * @param value The value that should be checked.
	 * @return The method returns true if the value is ok, false if it's not.
	 */
	public static boolean isValuePossible( final PluginDataType type, final String value ) {
		
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
						
		case STRING:
		case AXISID:
		case CHANNELID:
		case DEVICEID:
				return true;
			
		
		}
		
		return false;
		
	}
	
	/**
	 * This static Method is translating a name for the plugindata type like it's used in the
	 * measuring station description or the scan description into the correpondenting PluginDataType.
	 * Possible values are: OpenClose, OnOff, int, double, string, axisid, channelid and deviceid.
	 * 
	 * @param name The String that should be tranlated. Must not be null.
	 * @return The correspondenting PluginDataType.
	 */
	public static PluginDataType stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "OnOff" ) ) {
			return PluginDataType.ONOFF;
		} else if( name.equals( "OpenClose" ) ) {
			return PluginDataType.OPENCLOSE;
		} else if( name.equals( "int" ) ) {
			return PluginDataType.INT;
		} else if( name.equals( "double" ) ) {
			return PluginDataType.DOUBLE;
		} else if( name.equals( "string" ) ) {
			return PluginDataType.STRING;
		} else if( name.equals( "axisid" ) ) {
			return PluginDataType.AXISID;
		} else if( name.equals( "channelid" ) ) {
			return PluginDataType.CHANNELID;
		} else if( name.equals( "deviceid" ) ) {
			return PluginDataType.DEVICEID;
		} 
		return null;
		
	}
	
	/**
	 * This static method translates a PluginDataType into a String, like it's used in the measuring
	 * station description or the scan description. 
	 * 
	 * @param type The type, that should be translated. Must not be null!
	 * @return The correpondentin string. Null if the Type was invalid.
	 */
	public static String typeToString( final PluginDataType type ) {
		
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
			case AXISID:
				return "axisid";
			case CHANNELID:
				return "channelid";
			case DEVICEID:
				return "deviceid";
					
		}
		
		return null;
		
	}
	
	/**
	 * This static method give back an array of the possible comparison types of the given plugin data type.
	 * 
	 * @see de.ptb.epics.eve.data.ComparisonTypes
	 * @param type The plugin data type of which you want to have the possible comparison types.
	 * @return An array, which contains the possible comparisonTypes. Null, if the type was invalid.
	 */
	public static ComparisonTypes[] getPossibleComparisonTypes( final PluginDataType type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		switch( type ) {
			case ONOFF:
			case OPENCLOSE:
			case STRING:
			case AXISID:
			case CHANNELID:
			case DEVICEID:
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
	public static boolean isComparisonTypePossible( final PluginDataType dataType, final ComparisonTypes comparisonType ) {
		ComparisonTypes[] comparisonTypes = PluginDataType.getPossibleComparisonTypes( dataType );
		for( int i = 0; i < comparisonTypes.length; ++i ) {
			if( comparisonTypes[i] == comparisonType ) {
				return true;
			}
		}
		return false;
	}
	
}
