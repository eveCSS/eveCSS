package de.ptb.epics.eve.data.scandescription;

/**
 * This enum describes the position mode of a motor axis.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum PositionMode {
	
	/**
	 * The positions are understood as relative to the axis position at the beginning of the scan module.
	 */
	RELATIVE,
	
	/**
	 * The positions are absolute.
	 */
	ABSOLUTE;
	
	/**
	 * This method converts an enum value to a string.
	 * 
	 * @param positionMode The enum value to convert.
	 * @return The corresponding string.
	 */
	public static String typeToString( final PositionMode positionMode ) {
		switch( positionMode ) {
			case RELATIVE:
				return "relative";
			case ABSOLUTE:
				return "absolute";
				
		}
		return null;
	}
	
	/**
	 * This method converts a string to corresponding enum value.
	 * 
	 * @param name The string that should be converted.
	 * @return The corresponding enum value.
	 */
	public static PositionMode stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "relative" ) ) {
			return PositionMode.RELATIVE;
		}
		if( name.equals( "absolute" ) ) {
			return PositionMode.ABSOLUTE;
		}
		return null;
	}
	
	/**
	 * This method returns all possible position modes as an array of string.
	 * 
	 * @return All possible position modes as an array of string.
	 */
	public static String[] getPossiblePositionModes() {
		final String[] values = { "relative", "absolute" }; 
		return values;
	}
}
