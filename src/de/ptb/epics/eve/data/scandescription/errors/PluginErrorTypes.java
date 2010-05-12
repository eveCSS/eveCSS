package de.ptb.epics.eve.data.scandescription.errors;

/**
 * This enum describes contains the types of errors for a plug in.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum PluginErrorTypes {

	/**
	 * A mandatory parameter has not been set.
	 */
	MISSING_MANDATORY_PARAMETER,
	
	/**
	 * A parameter have a wrong value.
	 */
	WRONG_VALUE, 
	
	/**
	 * The plug in has not been set.
	 */
	PLUING_NOT_SET
	
}
