package de.ptb.epics.eve.data.scandescription.errors;

/**
 * This enumeration describes all possible error types of a chain.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum ChainErrorTypes {

	/**
	 * The name of the file is not set.
	 */
	FILENAME_EMPTY,
	
	/**
	 * The name of the file contains illegal character.
	 */
	FILENAME_ILLEGAL_CHARACTER
	
}
