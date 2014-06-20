package de.ptb.epics.eve.data.measuringstation.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A ScanEvent is a volatile event, i.e. an event which could become 
 * invalid due to removal of its origin, i.e. a scan module or a detector 
 * channel.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public abstract class ScanEvent extends Event {
	public static final String VALID_PROP = "valid";
	
	private boolean valid;
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructor.
	 */
	public ScanEvent() {
		this.valid = true;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Checks whether the event is valid. (It becomes invalid if its origin 
	 * is removed from the scan).
	 * 
	 * @return <code>true</code> if the event is valid, <code>false</code>
	 * 		otherwise
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Adds the given property change listener for the given property.
	 * 
	 * @param property the property to listen to
	 * @param listener the listener
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * Removes the given property change for the given property.
	 *  
	 * @param property the property to stop listen to
	 * @param listener the listener+
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(String property, 
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
}