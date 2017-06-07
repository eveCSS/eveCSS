package de.ptb.epics.eve.data;

/**
 * This enum represents differen Tranfport layer systems for accessing a variable.
 * @author   Stephan Rehfeld  <stephan.rehfeld( -at -) ptb.de>
 */
public enum TransportTypes {
	
	/**
	 * Channel Access
	 */
	CA,
	
	/**
	 * A local variable
	 */
	LOCAL;
	
	/**
	 * Converts a <code>String</code> into its corresponding transport type 
	 * (<code>TransportTypes</code>).
	 * 
	 * @param name the <code>String</code> that should be converted
	 * @return The corresponding transport type
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static TransportTypes stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if( name.equals( "ca" ) ) {
			return TransportTypes.CA;
		} else if( name.equals( "local" ) ) {
			return TransportTypes.LOCAL;
		}
		return null;
	}
	
	/**
	 * Converts a transport type (<code>TransportTypes</code>) into its 
	 * corresponding <code>String</code>. 
	 *
	 * @param type the transport type that should be converted.
	 * @return the corresponding <code>String</code>.
	 */
	public static String typeToString(final TransportTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null");
		}	
		switch( type ) {
			case CA:
				return "ca";
			case LOCAL:
				return "local";
		}
		return null;
	}
}