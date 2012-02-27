package de.ptb.epics.eve.data.scandescription.errors;


/**
 * This enumeration contains all possible errors of a scanModule.
 * 
 * 
 * @author Hartmut Scherr
 *
 */
public enum ScanModuleErrorTypes {

	/**
	 * The maximum deviation is not a possible value.
	 */
	TRIGGER_DELAY_NOT_POSSIBLE,
	
	/**
	 * The minimum is not a possible value.
	 */
	SETTLE_TIME_NOT_POSSIBLE
	
}