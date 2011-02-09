/*
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation.processors;

/**
 * This enum describes loader states for the loading of elements like options, 
 * units, and functions that are part of many different devices.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (- at -) ptb.de>
 *
 */
public enum MeasuringStationLoaderSubStates {

	/**
	 * This is the initial and the final state.
	 */
	NONE,
	
	/**
	 * This state indicates that a option is loading.
	 */
	OPTION_LOADING,
	
	/**
	 * The begin tag of the option name has been read.
	 */
	OPTION_NAME_NEXT,
	
	/**
	 * The name of the option has been read and the handler is waiting for the 
	 * close tag.
	 */
	OPTION_NAME_READ,
	
	/**
	 * The begin tag of the option id has been read.
	 */
	OPTION_ID_NEXT,
	
	/**
	 * The id of the option has been read and the handler is waiting for the 
	 * close tag.
	 */
	OPTION_ID_READ,
	
	/**
	 * The begin tag of the option display group has been read.
	 */
	OPTION_DISPLAYGROUP_NEXT,
	
	/**
	 * The display group of the option has been read and the handler is waiting 
	 * for the close tag.
	 */
	OPTION_DISPLAYGROUP_READ,
	
	/**
	 * The option value is loading.
	 */
	OPTION_VALUE_LOADING,
	
	/**
	 * The value of the option value begin tag.
	 */
	OPTION_VALUE_VALUE_NEXT,
	
	/**
	 * The value of the option value has been read.
	 */
	OPTION_VALUE_VALUE_READ,
	
	/**
	 * The access of the option value begin tag.
	 */
	OPTION_VALUE_ACCESS_NEXT,
	
	/**
	 * The access of the option value has been read.
	 */
	OPTION_VALUE_ACCESS_READ,
	
	/**
	 * This state indicates that a unit is loading.
	 */
	UNIT_LOADING,
	
	/**
	 * This state indicates that the value of the unit is read as next.
	 */
	UNIT_VALUE_NEXT,
	
	/**
	 * This state indicates that the value of the unit has been read.
	 */
	UNIT_VALUE_READ,
	
	/**
	 * This state indicates that the access of the unit is read as next.
	 */
	UNIT_ACCESS_NEXT,
	
	/**
	 * This state indicates that the access of the unit has been read.
	 */
	UNIT_ACCESS_READ,
	
	/**
	 * This state indicates that a function is loading.
	 */
	FUNCTION_LOADING,
	
	/**
	 * This state indicates that the value of the function is read as next.
	 */
	FUNCTION_VALUE_NEXT,
	
	/**
	 * This state indicates that the value of the function has been read.
	 */
	FUNCTION_VALUE_READ,
	
	/**
	 * This state indicates that the access of the function is read as next.
	 */
	FUNCTION_ACCESS_NEXT,
	
	/**
	 * This state indicates that the access of the unit has been read.
	 */
	FUNCTION_ACCESS_READ,
}