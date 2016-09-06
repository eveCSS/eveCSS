package de.ptb.epics.eve.data.scandescription.channelmode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;

/**
 * Channel mode is the current mode of the channel.
 * 
 * State of state pattern.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public abstract class ChannelMode {
	/**
	 * Standard channel mode: triggers average times
	 */
	public static final int STANDARD = 1;
	/**
	 * Interval channel mode: triggers with given interval until stop event
	 */
	public static final int INTERVAL = 2;
	
	private PropertyChangeSupport propertyChangeSupport;
	private Channel channel;
	
	public ChannelMode(Channel channel) {
		this.channel = channel;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * @return the channel
	 */
	protected Channel getChannel() {
		return channel;
	}
	
	/**
	 * Resets the mode to its default values
	 */
	public abstract void reset();
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getAverageCount()
	 */
	public int getAverageCount() {
		throw new IllegalStateException();
	}

	/**
	 * @param averageCount
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setAverageCount(int)
	 */
	public void setAverageCount(int averageCount) {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getMaxAttempts()
	 */
	public Integer getMaxAttempts() {
		throw new IllegalStateException();
	}

	/**
	 * @param maxAttempts
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setMaxAttempts(java.lang.Integer)
	 */
	public void setMaxAttempts(Integer maxAttempts) {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getMaxDeviation()
	 */
	public Double getMaxDeviation() {
		throw new IllegalStateException();
	}

	/**
	 * @param maxDeviation
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setMaxDeviation(java.lang.Double)
	 */
	public void setMaxDeviation(Double maxDeviation) {
		throw new IllegalStateException();
	}

	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getMinimum()
	 */
	public Double getMinimum() {
		throw new IllegalStateException();
	}

	/**
	 * @param minimum
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setMinimum(java.lang.Double)
	 */
	public void setMinimum(Double minimum) {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#isDeferred()
	 */
	public boolean isDeferred() {
		throw new IllegalStateException();
	}

	/**
	 * @param deferred
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setDeferred(boolean)
	 */
	public void setDeferred(boolean deferred) {
		throw new IllegalStateException();
	}

	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getRedoControlEventManager()
	 */
	public ControlEventManager getRedoControlEventManager() {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getRedoEvents()
	 */
	public List<ControlEvent> getRedoEvents() {
		throw new IllegalStateException();
	}

	/**
	 * @param redoEvent
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#addRedoEvent(de.ptb.epics.eve.data.scandescription.ControlEvent)
	 */
	public boolean addRedoEvent(ControlEvent redoEvent) {
		throw new IllegalStateException();
	}

	/**
	 * @param redoEvent
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#removeRedoEvent(de.ptb.epics.eve.data.scandescription.ControlEvent)
	 */
	public boolean removeRedoEvent(ControlEvent redoEvent) {
		throw new IllegalStateException();
	}

	/**
	 * @see de.ptb.epics.eve.data.scandescription.Channel#removeRedoEvents()
	 */
	public void removeRedoEvents() {
		throw new IllegalStateException();
	}

	/**
	 * @see de.ptb.epics.eve.data.scandescription.Channel#registerEventValidProperties()
	 */
	public void registerEventValidProperties() {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getTriggerInterval()
	 */
	public double getTriggerInterval() {
		throw new IllegalStateException();
	}
	
	/**
	 * @param triggerInterval
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setTriggerInterval(double)
	 */
	public void setTriggerInterval(double triggerInterval) {
		throw new IllegalStateException();
	}
	
	/**
	 * @return
	 * @see de.ptb.epics.eve.data.scandescription.Channel#getStoppedBy()
	 */
	public DetectorChannel getStoppedBy() {
		throw new IllegalStateException();
	}
	
	/**
	 * @param stoppedBy
	 * @see de.ptb.epics.eve.data.scandescription.Channel#setStoppedBy(DetectorChannel)
	 */
	public void setStoppedBy(DetectorChannel stoppedBy) {
		throw new IllegalStateException();
	}
	
	/**
	 * Returns an empty error list.
	 * @return an empty error list
	 */
	public List<IModelError> getModelErrors() {
		return new ArrayList<IModelError>();
	}
	
	/**
	 * @return the propertyChangeSupport
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}
	
	/**
	 * @param listener the listener to add
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * @param listener the listener that should be removed
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Adds a listener for the given property.
	 * 
	 * @param property the property to listen to
	 * @param listener the listener that should be added
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * Removes a listener for the given property.
	 * 
	 * @param property the property to stop listening to
	 * @param listener the listener that should be removed
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}