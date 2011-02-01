/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
	 * Says, that the line will be plotted linear.
	 *
	 * @uml.property  name="lINEAR"
	 * @uml.associationEnd  
	 */
	LINEAR,
	
	/**
	 * Says, that the line will be plotted logarithmic.
	 *
	 * @uml.property  name="lOG"
	 * @uml.associationEnd  
	 */
	LOG;
	
	/**
	 * This static Method is translating a name for the plot type like it's used in the
	 * measuring station description or the scan description into the correpondenting PlotMode.
	 * Possible values are: PUT, PUTCB, GET, GETCB, GETPUTCB and monitor
	 * 
	 * @param name The String that should be translated. Must not be null!
	 * @returnThe correspondenting PlotMode.
	 */
	public static PlotModes stringToMode( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		if( name.equals( "linear" ) ) {
			return PlotModes.LINEAR;
		} else if( name.equals( "log" ) ) {
			return PlotModes.LOG;
		}
		return null;
	}
	
	/**
	 * This static method translates a PlotMode into a String, like it's used in the measuring
	 * station description or the scan description.
	 *  
	 * @param mode  The mode, that should be translated. Must not be null!
	 * @return The correpondentin string. Null if the mode was invalid.
	 */
	public static String modeToString( final PlotModes mode ) {
		if( mode == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		
		switch( mode ) {
		 case LINEAR:
			 return "linear";
		 case LOG:
			 return "log";
		}
		return null;
	}
	
	/**
	 * This static method gives back an Array of all elements of the enum as String.
	 * 
	 * @see de.trustedcode.scanmoduleditor.views.PlotWindowComposite
	 * @return A array of all values.
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
