package de.ptb.epics.eve.data.scandescription.errors;

/**
 * This enumeration contains all possible error reasons in a control event.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Hartmut Scherr
 *
 */
public enum ControlEventErrorTypes {

	/**
	 * This value is not possible.
	 */
	VALUE_NOT_POSSIBLE,
	
    /**
	 * The limit of the monitor Event is not set.
	 */
	MONITOR_LIMIT_NOT_SET
}
