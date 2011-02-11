/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation.exceptions;

/**
 * This Exception indicates, that a parent was set, that was not allowed in the 
 * context.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (- at -) ptb.de >
 *
 */
public class ParentNotAllowedException extends Exception {
	
	/** 
	 * The id for serialization.
	 */
	private static final long serialVersionUID = -2616220233669984321L;
	
	/**
	 * The constructor for a new ParentNotAllowedException.
	 * 
	 * @param message  The message that describes the problem.
	 */
	public ParentNotAllowedException(final String message) {
		super(message);
	}
}