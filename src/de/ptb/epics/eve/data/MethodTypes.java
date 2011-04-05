package de.ptb.epics.eve.data;

/**
 * This enum defines the possible methods of an access description.
 * @author   Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version   1.3
 * @see  de.ptb.epics.eve.data.measuringstation.Access
 */
public enum MethodTypes {
	
	/**
	 * Indicates that a value is be putted in this access.
	 *
	 * @uml.property  name="pUT"
	 * @uml.associationEnd  
	 */
	PUT,
	
	/**
	 * Indicates that a value is be putted in this access and
	 * that a callback will occure after the value hat been putted.
	 *
	 * @uml.property  name="pUTCB"
	 * @uml.associationEnd  
	 */
	PUTCB,
	
	/**
	 * Indicates that a value is be read in this access.
	 *
	 * @uml.property  name="gET"
	 * @uml.associationEnd  
	 */
	GET,
	
	/**
	 * Indicates that a value is be read in this access and
	 * that a callback will occure after the value hat been read.
	 *
	 * @uml.property  name="gETCB"
	 * @uml.associationEnd  
	 */
	GETCB,
	
	/**
	 * Read and write access, no callback
	 *
	 * @uml.property  name="gETPUT"
	 * @uml.associationEnd  
	 */
	GETPUT,
	
	/**
	 * Read and write access, with callback
	 *
	 * @uml.property  name="gETPUTCB"
	 * @uml.associationEnd  
	 */
	GETPUTCB;
	
	
	/**
	 * Converts a <code>String</code> into its corresponding method type 
	 * (<code>MethodTypes</code>).
	 * 
	 * @param name the <code>String</code> that should be converted.<br>
	 * 				<b>Precondition:</b> name is element of {"PUT", "PUTCB", 
	 * 									"GET", "GETCB", "GETPUTCB", "GETPUT"}
	 * @return the corresponding method type
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static MethodTypes stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		
		if( name.equals( "PUT" ) ) {
			return MethodTypes.PUT;
		} else if( name.equals( "PUTCB" ) ) {
			return MethodTypes.PUTCB;
		} else if( name.equals( "GET" ) ) {
			return MethodTypes.GET;
		} else if( name.equals( "GETCB" ) ) {
			return MethodTypes.GETCB;
		} else if( name.equals( "GETPUTCB" ) ) {
			return MethodTypes.GETPUTCB;
		} else if( name.equals( "GETPUT" ) ) {
			return MethodTypes.GETPUT;
		}
		return null;
	}
	
	/**
	 * Converts a method type (<code>MethodTypes</code>) into its corresponding 
	 * <code>String</code>,  
	 * 
	 * @param type the type, that should be converted
	 * @return the corresponding <code>String</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static String typeToString(final MethodTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		
		switch(type) {
			case PUT:
				return "PUT";
			case PUTCB:
				return "PUTCB";
			case GET:
				return "GET";
			case GETCB:
				return "GETCB";
			case GETPUT:
				return "GETPUT";
			case GETPUTCB:
				return "GETPUTCB";
		}		
		return null;
	}
}