package de.ptb.epics.eve.data.scandescription.errors;

/**
 * This enumeration describes all possible error reasons of an axis.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum AxisErrorTypes {

	/**
	 * The start value is not set.
	 */
	START_NOT_SET,
	
	/**
	 * The stop value is not set.
	 */
	STOP_NOT_SET,
	
    /**
	 * The stepwidth value is not set.
	 */
	STEPWIDTH_NOT_SET,
	
	/**
	 * The start value is not possible.
	 */
	START_VALUE_NOT_POSSIBLE,
	
    /**
	 * The stop value is not possible.
	 */
	STOP_VALUE_NOT_POSSIBLE,
	
	/**
	 * The filename value is not set.
	 */
	FILENAME_NOT_SET,
	
    /**
	 * The position list value is not set.
	 */
	POSITIONLIST_NOT_SET,

    /**
	 * In the plugin is an error.
	 */
	PLUGIN_ERROR

}
