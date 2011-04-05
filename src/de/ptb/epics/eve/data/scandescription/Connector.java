/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

/**
 * This class represents connections between scan modules and scan modules
 * to start events. It is constructed to make it more easier to represent the
 * connections in a MVC system (like GEF).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Connector {

	/**
	 * If the parent is an event, it is saved here.
	 */
	private StartEvent parentEvent;
	
	/**
	 * If the parent is an scan modoul, it is saved here.
	 */
	private ScanModule parentScanModul;
	
	/**
	 * The child scan modul of the connection.
	 */
	private ScanModule childScanModul;
	
	/**
	 * Gives back the child scan modul of this connection.
	 * 
	 * @return The child scan modul of this connection.
	 */
	public ScanModule getChildScanModul() {
		return childScanModul;
	}
	
	/**
	 * Sets the child scan Modul of this connection.
	 * 
	 * @param childScanModul The child scan modul of this connection.
	 */
	public void setChildScanModul( final ScanModule childScanModul ) {
		this.childScanModul = childScanModul;
	}
	
	/**
	 * Gives back the parent event of this connection.
	 * 
	 * @return The parent event of this connection.
	 */
	public StartEvent getParentEvent() {
		return parentEvent;
	}
	
	/**
	 * Sets the parent event of this connection. The parent scan modul will be
	 * forgotten after calling this method.
	 * 
	 * @param parentEvent The parent event.
	 */
	public void setParentEvent( final StartEvent parentEvent ) {
		this.parentEvent = null;
		this.parentEvent = parentEvent;
	}
	
	/**
	 * Gives back the parent scan modul.
	 * 
	 * @return The parent scan modul.
	 */
	public ScanModule getParentScanModul() {
		return parentScanModul;
	}
	
	/**
	 * Sets the parent scan modul. The parent event will be forgotten
	 * after calling this method.
	 * 
	 * @param parentScanModul The parent scan modul.
	 */
	public void setParentScanModul( final ScanModule parentScanModul ) {
		this.parentEvent = null;
		this.parentScanModul = parentScanModul;
	}
	
	/**
	 * This method can be used to find out if the parent of the
	 * connection is an event or not.
	 * 
	 * @return Gives back true if the parent is an event.
	 */
	public boolean isParentEvent() {
		return this.parentEvent!=null;
	}
	/**
	 * This method can be used to find out if the parent of the
	 * connection is an scan modul or not.
	 * 
	 * @return Gives back true if the parent is an scan modul.
	 */
	public boolean isParentScanModul() {
		return this.parentScanModul!=null;
	}
	
}
