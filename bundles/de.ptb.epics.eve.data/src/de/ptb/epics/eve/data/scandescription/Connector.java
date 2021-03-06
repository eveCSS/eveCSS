package de.ptb.epics.eve.data.scandescription;

/**
 * This class represents connections between scan modules and scan modules to
 * start events. It is constructed to make it more easier to represent the
 * connections in a MVC system (like GEF).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Connector {

	/** */
	public static final String APPENDED = "appended";
	/** */
	public static final String NESTED = "nested";
	/** */
	public static final String PARENT = "parent";

	/**
	 * If the parent is an event, it is saved here.
	 */
	private StartEvent parentEvent;

	/**
	 * If the parent is an scan module, it is saved here.
	 */
	private ScanModule parentScanModule;

	/**
	 * The child scan module of the connection.
	 */
	private ScanModule childScanModule;

	/**
	 * Gives back the child scan module of this connection.
	 * 
	 * @return The child scan module of this connection.
	 */
	public ScanModule getChildScanModule() {
		return childScanModule;
	}

	/**
	 * Sets the child scan module of this connection.
	 * 
	 * @param childScanModule
	 *            The child scan module of this connection.
	 */
	public void setChildScanModule(final ScanModule childScanModule) {
		this.childScanModule = childScanModule;
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
	 * Sets the parent event of this connection. The parent scan module will be
	 * forgotten after calling this method.
	 * 
	 * @param parentEvent
	 *            The parent event.
	 */
	public void setParentEvent(final StartEvent parentEvent) {
		this.parentEvent = null;
		this.parentEvent = parentEvent;
	}

	/**
	 * Gives back the parent scan modul.
	 * 
	 * @return The parent scan modul.
	 */
	public ScanModule getParentScanModule() {
		return parentScanModule;
	}

	/**
	 * Sets the parent scan modul. The parent event will be forgotten after
	 * calling this method.
	 * 
	 * @param parentScanModule
	 *            The parent scan modul.
	 */
	public void setParentScanModule(final ScanModule parentScanModule) {
		this.parentEvent = null;
		this.parentScanModule = parentScanModule;
	}

	/**
	 * This method can be used to find out if the parent of the connection is an
	 * event or not.
	 * 
	 * @return Gives back true if the parent is an event.
	 */
	public boolean isParentEvent() {
		return this.parentEvent != null;
	}

	/**
	 * This method can be used to find out if the parent of the connection is an
	 * scan modul or not.
	 * 
	 * @return Gives back true if the parent is an scan modul.
	 */
	public boolean isParentScanModule() {
		return this.parentScanModule != null;
	}
	
	/**
	 * Returns the "type" of the connection, i.e. if it is appended or nested.
	 * TODO should be an ENUM
	 * 
	 * @return the type of the connection (appended or nested)
	 * @since 1.19
	 */
	public String getType() {
		if (parentEvent != null) {
			return Connector.APPENDED;
		}
		if (parentScanModule != null) {
			if (parentScanModule.getAppended() == this) {
				return Connector.APPENDED;
			}
			if (parentScanModule.getNested() == this) {
				return Connector.NESTED;
			}
		}
		return null;
	}
}