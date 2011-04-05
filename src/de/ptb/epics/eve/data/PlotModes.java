package de.ptb.epics.eve.data;

/**
 * This enum defines the possible plotmodes of a line in a plot window.
 * @author   Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version   1.3
 * @see de.ptb.epics.eve.data.scandescription.PlotWindow
 * @see  de.ptb.epics.eve.data.scandescription.YAxis
 */
public enum PlotModes {

	/**
	 * Defines that the line will be plotted linear.
	 *
	 * @uml.property  name="lINEAR"
	 * @uml.associationEnd  
	 */
	LINEAR,
	
	/**
	 * Defines that the line will be plotted logarithmically.
	 *
	 * @uml.property  name="lOG"
	 * @uml.associationEnd  
	 */
	LOG;
	
	/**
	 * Converts a <code>String</code> into its corresponding plot mode 
	 * (<code>PlotModes</code>).
	 * 
	 * @param name the <code>String</code> that should be converted<br>
	 * 			<b>Precondition:</b> name is element of {"linear", "log"}
	 * @return the corresponding plot mode
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static PlotModes stringToMode(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		
		if( name.equals("linear")) {
			return PlotModes.LINEAR;
		} else if(name.equals("log")) {
			return PlotModes.LOG;
		}
		return null;
	}
	
	/**
	 * Converts a plot mode (<code>PlotModes</code>) into its corresponding 
	 * <code>String</code>.
	 *  
	 * @param mode  the mode, that should be translated
	 * @return the corresponding <code>String</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static String modeToString(final PlotModes mode) {
		if(mode == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		
		switch(mode) {
		 case LINEAR:
			 return "linear";
		 case LOG:
			 return "log";
		}
		return null;
	}
	
	/**
	 * Returns an array containing all plot modes as <code>String</code>s
	 * 
	 * @return an array of <code>String</code>s containing the plot modes
	 */
	public static String[] valuesAsString() {
		final PlotModes[] plotModes = PlotModes.values();
		final String[] values = new String[plotModes.length];
		for( int i = 0; i < plotModes.length; ++i ) {
			values[i] = modeToString( plotModes[i] );
		}
		return values;
	}
}