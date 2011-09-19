package de.ptb.epics.eve.data.scandescription.errors;

/**
 * This enumeration contains all possible errors of a pre- or postscan.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public enum PostscanErrorTypes {
	
	/**
	 * The value of the pre- or postscan is not possible.
	 */
	VALUE_NOT_POSSIBLE,

	/**
	 * The value of the pre- or postscan is not possible.
	 */
	PRESCAN_ERROR,

	/**
	 * The value of the pre- or postscan is not possible.
	 */
	POSTSCAN_ERROR

}
