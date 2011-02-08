/*
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

/**
 * This enum represents the data types for plug in variables. They are similar 
 * to the data types that are defined in the enum DataTypes. 
 * Additionally this enum has the types AXISID, CHANNELID and DEVIDEID that 
 * indicate, that the plug in parameter is a channel, axis or device.
 * @author Stephan Rehfeld
 */
public enum PluginDataType {
	
	/**
	 * On/Off data type is used at some Devices to switch them on or off.
	 *
	 * @uml.property  name="oNOFF"
	 * @uml.associationEnd  
	 */
	ONOFF,
	
	/**
	 * Open/Close data type is used to open or close a device 
	 * e.g. a beam shutter.
	 *
	 * @uml.property  name="oPENCLOSE"
	 * @uml.associationEnd  
	 */
	OPENCLOSE,
	
	/**
	 * Normal integer data type.
	 *
	 * @uml.property  name="iNT"
	 * @uml.associationEnd  
	 */
	INT, 
	
	/**
	 * Normal double data type.
	 *
	 * @uml.property  name="dOUBLE"
	 * @uml.associationEnd  
	 */
	DOUBLE,
	
	/**
	 * Normal String data type.
	 *
	 * @uml.property  name="sTRING"
	 * @uml.associationEnd  
	 */
	STRING,
	
	/**
	 * A String that represents an identifier of an axis.
	 *
	 * @uml.property  name="aXISID"
	 * @uml.associationEnd  
	 */
	AXISID,
	
	/**
	 * A String that represents an identifier of an channel.
	 *
	 * @uml.property  name="cHANNELID"
	 * @uml.associationEnd  
	 */
	CHANNELID,
	
	/**
	 * A String that represents an identifier of an device.
	 *
	 * @uml.property  name="dEVICEID"
	 * @uml.associationEnd  
	 */
	DEVICEID;
	
	/**
	 * This method checks whether a value is possible for a given data type.
	 * 
	 * @param type the type  the value should be checked with
	 * @param value the value that should be checked
	 * @return (value possible) ? TRUE : FALSE
	 * @exception IllegalArgumentException if type == 'null'
	 * @exception IllegalArgumentException if value  == 'null'
	 */
	public static boolean isValuePossible(final PluginDataType type, 
											final String value) {
		
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		if(value == null) {
			throw new IllegalArgumentException(
					"The parameter 'value' must not be null!");
		}	
		
		switch(type) {
		
		case ONOFF:		return (value.toUpperCase().equals("ON") || 
								 value.toUpperCase().equals("OFF"));
		
		case OPENCLOSE:	return (value.toUpperCase().equals("OPEN") || 
								 value.toUpperCase().equals("CLOSE"));
		
		case INT:		try {
							Integer.parseInt(value);
						} catch(final NumberFormatException e) {
							return false;
						}
						return true;
						
		case DOUBLE:	try {
							Double.parseDouble(value);
						} catch(final NumberFormatException e) {
							return false;
						}
						return true; 
			
		// TODO clean programming of the following lines ?
		// TODO Does it really do what we want ?
		case STRING:
		case AXISID:
		case CHANNELID:
		case DEVICEID:
				return true;		
		}		
		return false;
	}
	
	/**
	 * translates a name of a plug in data type into its corresponding 
	 * PluginDataType.
	 * 
	 * @param name one of {"OnOff", "OpenClose", "int", "double", "string", 
	 * 						"axisid", "channelid", "deviceid"}
	 * @return The corresponding PluginDataType.
	 * @exception IllegalArgumentException if name == 'null'
	 */
	public static PluginDataType stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
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
	 * Returns a well-formatted string with a valid default value.
	 * 
	 * @param type data type for which a default value is returned
	 * @return a default value
	 */
	public static String getDefaultValue(final PluginDataType type){
		
		switch( type ) {
		
		case ONOFF:		return "OFF";
		case OPENCLOSE:	return "CLOSE";		
		case INT:		return new Integer(0).toString();
		case DOUBLE:	return new Double(0.0).toString();
		case STRING:	return "<unknown>";
		case AXISID:	return "";
		case CHANNELID:	return "";
		case DEVICEID:	return "";
		}
		return "";
	}
	
	/**
	 * translates a PluginDataType into a String. 
	 * 
	 * @param type the type, that should be translated.
	 * @return The corresponding string. Null if the Type was invalid.
	 * @exception IllegalArgumentException if type == 'null'
	 */
	public static String typeToString(final PluginDataType type) {
		
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
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
	 * Returns an array of possible comparison types of the given plug in 
	 * data type.
	 * 
	 * @see de.ptb.epics.eve.data.ComparisonTypes
	 * @param type The plug in data type of interest
	 * @return (type valid) ? array containing possible comparisonTypes : 'null'
	 * @exception IllegalArgumentException if type == 'null'
	 */
	public static ComparisonTypes[] getPossibleComparisonTypes(
										final PluginDataType type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		switch(type) {
			case ONOFF:
			case OPENCLOSE:
			case STRING:
			case AXISID:
			case CHANNELID:
			case DEVICEID:
				return new ComparisonTypes[]
				                   {ComparisonTypes.EQ, ComparisonTypes.NE};
			case INT:
			case DOUBLE:
				return new ComparisonTypes[]
				                   {ComparisonTypes.EQ, ComparisonTypes.NE, 
									ComparisonTypes.GT, ComparisonTypes.LT};
		}
		return null;
	}
	
	/**
	 * Checks if a comparison type is possible for a given plug in data type.
	 * E.g. EQ and NE are working for a string but GT and LT are not.
	 * 
	 * @param dataType the plug in data type
	 * @param comparisonType the comparison type that should be checked.
	 * @return (type possible) ? TRUE : FALSE
	 */
	public static boolean isComparisonTypePossible(
								final PluginDataType dataType, 
								final ComparisonTypes comparisonType ) {
		ComparisonTypes[] comparisonTypes = 
				PluginDataType.getPossibleComparisonTypes( dataType );
		
		for(int i=0; i<comparisonTypes.length; ++i) {
			if(comparisonTypes[i] == comparisonType) {
				return true;
			}
		}
		return false;
	}	
}