/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.processors;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * This class represents a relation between two scan modules an is used during the loading of a scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class ScanModulRelationReminder {

	/**
	 * The scan module.
	 */
	private ScanModule scanModul;
	
	/**
	 * the id of the parent scan module.
	 */
	private int parent;
	
	/**
	 * The id of the the appended scan module.
	 */
	private int appended;
	
	/**
	 * The id of the nested scan module.
	 */
	private int nested;
	
	/**
	 * This constructor creates a new reminder.
	 * 
	 * @param scanModul The scan module.
	 */
	public ScanModulRelationReminder( final ScanModule scanModul ) {
		this.scanModul = scanModul;
	}

	/**
	 * This method returns the id of the appended module.
	 * 
	 * @return The id of the appended scan module
	 */
	public int getAppended() {
		return this.appended;
	}

	/**
	 * This method sets the id of the appended scan module.
	 * 
	 * @param appended The id of the appended scan module.
	 */
	public void setAppended( final int appended ) {
		this.appended = appended;
	}

	/**
	 * This method returns the id of the nested module.
	 * 
	 * @return The id of the nested scan module
	 */
	public int getNested() {
		return this.nested;
	}

	/**
	 * This method sets the id of the nested scan module.
	 * 
	 * @param nested The id of the nested scan module.
	 */
	public void setNested( final int nested ) {
		this.nested = nested;
	}

	/**
	 * This method returns the id of the parent module.
	 * 
	 * @return The id of the parent scan module
	 */
	public int getParent() {
		return this.parent;
	}

	/**
	 * This method sets the id of the parent scan module.
	 * 
	 * @param parent The id of the parent scan module.
	 */
	public void setParent( final int parent ) {
		this.parent = parent;
	}

	/**
	 * This method return the scan module.
	 * 
	 * @return The scan module.
	 */
	public ScanModule getScanModul() {
		return this.scanModul;
	}

	
	/**
	 * This method sets the scan module.
	 * 
	 * @param scanModul The scan module.
	 */
	public void setScanModul( final ScanModule scanModul ) {
		this.scanModul = scanModul;
	}
	
}
