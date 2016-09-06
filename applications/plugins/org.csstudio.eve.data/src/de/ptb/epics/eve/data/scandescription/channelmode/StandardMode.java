package de.ptb.epics.eve.data.scandescription.channelmode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScanEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class StandardMode extends ChannelMode implements IModelUpdateListener, PropertyChangeListener {
	public static final Logger LOGGER = Logger.getLogger(StandardMode.class.getName());
	
	public static final String AVERAGE_COUNT_PROP = "averageCount";
	public static final String MAX_ATTEMPTS_PROP = "maxAttempts";
	public static final String MAX_DEVIATION_PROP = "maxDeviation";
	public static final String MINIMUM_PROP = "minimum";
	public static final String DEFERRED_PROP = "deferred";
	public static final String REDO_EVENT_PROP = "redoEvent";
	
	private static final int AVERAGE_COUNT_DEFAULT_VALUE = 1;
	
	private int averageCount;
	private Integer maxAttempts;
	private Double maxDeviation;
	private Double minimum;

	private boolean deferred;
	
	private ControlEventManager redoControlEventManager;
	
	public StandardMode(Channel channel) {
		super(channel);
		this.averageCount = StandardMode.AVERAGE_COUNT_DEFAULT_VALUE;
		this.maxAttempts = null;
		this.maxDeviation = null;
		this.minimum = null;
		this.deferred = false;
		this.redoControlEventManager = new ControlEventManager(
				ControlEventTypes.CONTROL_EVENT);
		this.redoControlEventManager.addModelUpdateListener(this);
	}

	/**
	 * @return the averageCount
	 */
	@Override
	public int getAverageCount() {
		return averageCount;
	}

	/**
	 * @param averageCount the averageCount to set
	 */
	@Override
	public void setAverageCount(int averageCount) {
		if(averageCount < 0) {
			throw new IllegalArgumentException(
					"The average must be larger than 0.");
		}
		int oldValue = this.averageCount;
		this.averageCount = averageCount;
		this.getPropertyChangeSupport().firePropertyChange(
				StandardMode.AVERAGE_COUNT_PROP, oldValue, this.averageCount);
	}

	/**
	 * @return the maxAttempts
	 */
	@Override
	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * @param maxAttempts the maxAttempts to set
	 */
	@Override
	public void setMaxAttempts(Integer maxAttempts) {
		Integer oldValue = this.maxAttempts;
		this.maxAttempts = maxAttempts;
		this.getPropertyChangeSupport().firePropertyChange(
				StandardMode.MAX_ATTEMPTS_PROP, oldValue, this.maxAttempts);
	}

	/**
	 * @return the maxDeviation
	 */
	@Override
	public Double getMaxDeviation() {
		return maxDeviation;
	}

	/**
	 * @param maxDeviation the maxDeviation to set
	 */
	@Override
	public void setMaxDeviation(Double maxDeviation) {
		Double oldValue = this.maxDeviation;
		this.maxDeviation = maxDeviation;
		this.getPropertyChangeSupport().firePropertyChange(
				StandardMode.MAX_DEVIATION_PROP, oldValue, this.maxDeviation);
	}

	/**
	 * @return the minimum
	 */
	@Override
	public Double getMinimum() {
		return minimum;
	}

	/**
	 * @param minimum the minimum to set
	 */
	@Override
	public void setMinimum(Double minimum) {
		Double oldValue = this.minimum;
		this.minimum = minimum;
		this.getPropertyChangeSupport().firePropertyChange(
				StandardMode.MINIMUM_PROP, oldValue, this.minimum);
	}

	/**
	 * @return the deferred
	 */
	@Override
	public boolean isDeferred() {
		return deferred;
	}

	/**
	 * @param deferred the deferred to set
	 */
	@Override
	public void setDeferred(boolean deferred) {
		boolean oldValue = this.deferred;
		this.deferred = deferred;
		this.getPropertyChangeSupport().firePropertyChange(
				StandardMode.DEFERRED_PROP, oldValue, this.deferred);
	}

	/**
	 * @return the redoControlEventManager
	 */
	@Override
	public ControlEventManager getRedoControlEventManager() {
		return redoControlEventManager;
	}
	
	/**
	 * Adds a redo event. 
	 * 
	 * @param redoEvent the redo event that should be added
	 * @return <code>true</code> if the event has been added, 
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean addRedoEvent(final ControlEvent redoEvent) {
		if (this.redoControlEventManager.addControlEvent(redoEvent)) {
			this.registerEventValidProperty(redoEvent);
			this.getPropertyChangeSupport().firePropertyChange(
					StandardMode.REDO_EVENT_PROP, null, redoEvent);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a redo event.
	 * 
	 * @param redoEvent the redo event that should be removed
	 * @return <code>true</code> if the event has been removed, 
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean removeRedoEvent(final ControlEvent redoEvent) {
		if (this.redoControlEventManager.removeEvent(redoEvent)) {
			this.unregisterEventValidProperty(redoEvent);
			this.getPropertyChangeSupport().firePropertyChange(
					StandardMode.REDO_EVENT_PROP, redoEvent, null);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes all redo events.
	 */
	@Override
	public void removeRedoEvents() {
		for (ControlEvent redoEvent : this.redoControlEventManager.getEvents()) {
			this.unregisterEventValidProperty(redoEvent);
		}
		this.redoControlEventManager.removeAllEvents();
		this.getPropertyChangeSupport().firePropertyChange(StandardMode.REDO_EVENT_PROP, 
				this.redoControlEventManager.getEvents(), null);
	}
	
	/*
	 * A scan event (schedule or detector) becomes invalid if its corresponding scan module/detector channel is 
	 * removed from the scan. In order to react to this event (by removing the event) we have to get informed 
	 * about it.
	 */
	private void registerEventValidProperty(ControlEvent controlEvent) {
		if (controlEvent.getEvent() instanceof ScheduleEvent || 
				controlEvent.getEvent() instanceof DetectorEvent) {
			((ScanEvent) controlEvent.getEvent()).addPropertyChangeListener(
					ScanEvent.VALID_PROP, this);
		}
	}
	
	/*
	 * Unregister the listener (see above explanation) if the event is removed.
	 */
	private void unregisterEventValidProperty(ControlEvent controlEvent) {
		if (controlEvent.getEvent() instanceof ScheduleEvent || 
				controlEvent.getEvent() instanceof DetectorEvent) {
			((ScanEvent) controlEvent.getEvent()).removePropertyChangeListener(
					ScanEvent.VALID_PROP, this);
		}
	}
	
	/*
	 * Due to the fact that we are only informed that any scan event became invalid 
	 * (scan event valid prop listener) we have to check all our events for valid 
	 * state and remove the invalid one(s).
	 */
	private void removeInvalidScanEvents() {
		for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
				this.redoControlEventManager.getEvents())) {
			if (controlEvent.getEvent() instanceof ScanEvent &&
					!((ScanEvent)controlEvent.getEvent()).isValid()) {
				this.removeRedoEvent(controlEvent);
				LOGGER.debug("Redo Event " + controlEvent.getEvent().getName()
						+ " removed from channel "
						+ this.getChannel().getDetectorChannel().getName() + 
						"(Chain  " + this.getChannel().getScanModule().getChain().getId() + 
						", SM " + this.getChannel().getScanModule().getId() + ")");
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerEventValidProperties() {
		for (ControlEvent controlEvent : this.redoControlEventManager.getEvents()) {
			this.registerEventValidProperty(controlEvent);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.averageCount = StandardMode.AVERAGE_COUNT_DEFAULT_VALUE;
		this.maxAttempts = null;
		this.maxDeviation = null;
		this.minimum = null;
		this.deferred = false;
		this.redoControlEventManager.removeAllEvents();
		// TODO set by setters to invoke change events ?
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> modelErrors = new ArrayList<IModelError>();
		if(this.redoControlEventManager.getModelErrors().size() > 0) {
			modelErrors.addAll(this.redoControlEventManager.getModelErrors());
		}
		return modelErrors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ScanEvent.VALID_PROP) &&
				e.getNewValue().equals(Boolean.FALSE)) {
			LOGGER.debug(((ScanEvent)e.getSource()).getName() +
					" (Det: " + this.getChannel().getDetectorChannel().getName() + ") " +
					" got invalid -> start removal");
			this.removeInvalidScanEvents();
		}
		// TODO
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.getChannel().updateEvent(modelUpdateEvent);
		// TODO add fire property change too ?
	}
}