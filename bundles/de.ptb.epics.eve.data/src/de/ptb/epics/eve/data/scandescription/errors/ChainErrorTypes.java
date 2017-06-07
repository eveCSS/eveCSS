package de.ptb.epics.eve.data.scandescription.errors;

/**
 * Contains all possible error types of a chain.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum ChainErrorTypes {

	/**
	 * The file name is not set.
	 */
	FILENAME_EMPTY,
	
	/**
	 * The file name contains illegal character.
	 */
	FILENAME_ILLEGAL_CHARACTER
}