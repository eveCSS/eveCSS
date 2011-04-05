package de.ptb.epics.eve.data;

/**
 * Defines the available plot modes of a line in a plot window.
 * @author   Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version   1.3
 * @see  de.ptb.epics.eve.data.measuringstation.PlugIn
 */
public enum PluginTypes {

	/**
	 * A position plug in, that could be used to 
	 * calculate the position of motor axis.
	 *
	 * @uml.property  name="pOSITION"
	 * @uml.associationEnd  
	 */
	POSITION,
	/**
	 * A save plug in, that could be used in the 
	 * definition of a scan chain.
	 *
	 * @uml.property  name="sAVE"
	 * @uml.associationEnd  
	 */
	SAVE,
	/**
	 * A display plug in, that is doing some 
	 * pre-calculations for a plotWindow. 
	 *
	 * @uml.property  name="dISPLAY"
	 * @uml.associationEnd  
	 */
	DISPLAY,
	
	/**
	 * A post scan positions plug in, that moves an axis
	 * to a position after
	 *
	 * @uml.property  name="pOSTSCANPOSITIONING"
	 * @uml.associationEnd  
	 */
	POSTSCANPOSITIONING;
	
	/**
	 * Converts a plug in type (<code>PluginTypes</code>) into its
	 * corresponding <code>String</code>.
	 *
	 * @param type the type, that should be converted
	 * @return the corresponding <code>String</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static String typeToString(final PluginTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null");
		}
		
		switch(type) {
			case POSITION:
				return "position";
			case SAVE:
				return "save";
			case DISPLAY:
				return "display";
			case POSTSCANPOSITIONING:
				return "postscanpositioning";
		}
		return null;	
	}	
}