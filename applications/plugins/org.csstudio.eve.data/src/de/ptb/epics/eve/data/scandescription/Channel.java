package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class describes the behavior of a detector during the main phase.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class Channel extends AbstractMainPhaseBehavior implements
		PropertyChangeListener {

	private static Logger logger = Logger.getLogger(Channel.class.getName());
	
	// delegated observable
	private PropertyChangeSupport propertyChangeSupport;
	
	/*
	 * This attribute controls how often the detector should be read to make
	 * an average value. default value is 1.
	 * If input is invalid, set 0
	 */
	private int averageCount;

	/*
	 * The maximum attempts for reading the channel.
	 * -1 = value is invalid
	 * Integer.MIN_VALUE = value is empty
	 */
	private int maxAttempts;

	/*
	 * The max deviation of this channel.
	 * Double.NaN = value is invalid
	 * Double.NEGATIVE_INFINITY = value is empty
	 */
	private double maxDeviation;
	
	/*
	 * The minimum for this channel.
	 * Double.NaN = value is invalid
	 * Double.NEGATIVE_INFINITY = value is empty
	 */
	private double minimum;
	
	/*
	 * A flag for repeat on redo.
	 */
	private boolean repeatOnRedo;

	// 
	private DetectorChannel normalizeChannel;

	private boolean deferred;
	
	/*
	 * The detector ready event.
	 */
	private Event detectorReadyEvent;

	/*
	 * A list of the ControlEvents, that holds the configuration for the redo 
	 * events.
	 */
	private List<ControlEvent> redoEvents;
	
	/*
	 * This control event manager controls the redo events.
	 */
	private ControlEventManager redoControlEventManager;
	
	/**
	 * Constructs a <code>Channel</code>.
	 * 
	 * @param scanModule The parent scan module.
	 * @throws IllegalArgumentException if the parameter is <code>null</code>.
	 */
	public Channel(final ScanModule scanModule) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModule' must not be null!");

		}
		this.scanModule = scanModule;
		this.averageCount = 1;
		this.maxAttempts = Integer.MIN_VALUE;
		this.maxDeviation = Double.NEGATIVE_INFINITY;
		this.minimum = Double.NEGATIVE_INFINITY;
		this.repeatOnRedo = false;
		this.deferred = false;
		this.normalizeChannel = null;
		this.redoEvents = new ArrayList<ControlEvent>();
		this.redoControlEventManager = new ControlEventManager(
				this, this.redoEvents, ControlEventTypes.CONTROL_EVENT);
		this.redoControlEventManager.addModelUpdateListener(this);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Better Constructor.
	 * 
	 * @param scanModule the scan module the channel corresponds to
	 * @param channel the corresponding device
	 */
	public Channel(final ScanModule scanModule, final DetectorChannel channel) {
		this(scanModule);
		this.setDetectorChannel(channel);
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param channel the channel to be copied
	 * @param scanModule the scan module the channel will be added to
	 * @return a copy of the given channel
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static Channel newInstance(Channel channel, ScanModule scanModule) {
		Channel newChannel = new Channel(scanModule, 
				(DetectorChannel)channel.getAbstractDevice());
		newChannel.setAverageCount(channel.getAverageCount());
		newChannel.setMaxAttempts(channel.getMaxAttempts());
		newChannel.setMaxDeviation(channel.getMaxDeviation());
		newChannel.setMinimum(channel.getMinimum());
		newChannel.setDeferred(channel.isDeferred());
		// TODO normalize channel
		// TODO repeat on redo ?
		// TODO Redo Events
		return newChannel;
	}
	
	/**
	 * Returns how often the detector should be read to make an average
	 * result for the measuring.
	 * 
	 * @return the average count
	 */
	public int getAverageCount() {
		return this.averageCount;
	}

	/**
	 * Sets how often the detector should be read to make an average result
	 * for the measuring.
	 * 
	 * @param averageCount How often the detector should be read.
	 * @throws IllegalArgumentException if <code>averageCount</code> 
	 * 		   is less than zero
	 */
	public void setAverageCount(final int averageCount) {
		if(averageCount < 0) {
			throw new IllegalArgumentException(
					"The average must be larger than 0.");
		}
		logger.debug("average count set");
		this.propertyChangeSupport.firePropertyChange("averageCount",
				this.averageCount, this.averageCount = averageCount);
		updateListeners();
	}

	/**
	 * Returns the maximum attempts to read the detector.
	 * 
	 * @return the maximum attempts to read the detector
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * Sets the maximum attempts to read the detector.
	 * 
	 * @param maxAttempts the maximum attempts to read the detector.
	 */
	public void setMaxAttempts(final int maxAttempts) {
		this.propertyChangeSupport.firePropertyChange("maxAttempts",
				this.maxAttempts, this.maxAttempts = maxAttempts);
		updateListeners();
	}

	/**
	 * Returns the maximum deviation between the taken values.
	 *  
	 * @return The maximum deviation.
	 */
	public double getMaxDeviation() {
		return this.maxDeviation;
	}

	/**
	 * Sets the maximum deviation.
	 * 
	 * @param maxDeviation the new maximum deviation.
	 */
	public void setMaxDeviation(final double maxDeviation) {
		this.propertyChangeSupport.firePropertyChange("maxDeviation",
				this.maxDeviation, this.maxDeviation = maxDeviation);
		updateListeners();
	}

	/**
	 * Returns the minimum.
	 * 
	 * @return the minimum for this channel.
	 */
	public double getMinimum() {
		return this.minimum;
	}

	/**
	 * Sets the minimum for this channel.
	 * 
	 * @param minimum the new minimum for this channel.
	 */
	public void setMinimum(final double minimum) {
		this.propertyChangeSupport.firePropertyChange("minimum", this.minimum,
				this.minimum = minimum);
		updateListeners();
	}

	/**
	 * Returns whether the channel should repeat on redo.
	 * 
	 * @return <code>true</code> if the channel repeats reading on a redo event.
	 */
	public boolean isRepeatOnRedo() {
		return repeatOnRedo;
	}

	/**
	 * Sets whether the read of the detector should be repeated on a 
	 * redo event.
	 * 
	 * @param repeatOnRedo <code>true</code> if the detector read should be 
	 * 		repeated, <code>false</code> otherwise
	 */
	public void setRepeatOnRedo(final boolean repeatOnRedo) {
		this.propertyChangeSupport.firePropertyChange("repeatOnRedo",
				this.repeatOnRedo, this.repeatOnRedo = repeatOnRedo);
		updateListeners();
	}

	/**
	 * @return the deferred
	 */
	public boolean isDeferred() {
		return deferred;
	}

	/**
	 * @param deferred the deferred to set
	 */
	public void setDeferred(boolean deferred) {
		this.propertyChangeSupport.firePropertyChange("deferred", 
				this.deferred, this.deferred = deferred);
		updateListeners();
	}

	/**
	 * @return the normalizeChannel
	 */
	public DetectorChannel getNormalizeChannel() {
		return normalizeChannel;
	}

	/**
	 * @param normalizeChannel the normalizeChannel to set
	 */
	public void setNormalizeChannel(DetectorChannel normalizeChannel) {
		this.propertyChangeSupport.firePropertyChange("normalizeChannel", 
				this.normalizeChannel, this.normalizeChannel = normalizeChannel);
		updateListeners();
	}

	/**
	 * Adds a redo event. 
	 * 
	 * @param redoEvent the redo event that should be added
	 * @return <code>true</code> if the event has been added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addRedoEvent(final ControlEvent redoEvent) {
		if(this.redoEvents.add(redoEvent)) {
			updateListeners();
			redoEvent.addModelUpdateListener(this.redoControlEventManager);
			this.redoControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(redoEvent, 
					ControlEventMessageEnum.ADDED)));
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
	public boolean removeRedoEvent(final ControlEvent redoEvent) {
		if( this.redoEvents.remove(redoEvent)) {
			updateListeners();
			this.redoControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(redoEvent, 
					ControlEventMessageEnum.REMOVED)));
			redoEvent.removeModelUpdateListener(this.redoControlEventManager);
			return true;
		} 
		return false;
	}

	/**
	 * Returns an iterator of the redo events.
	 * 
	 * @return an iterator of the redo events
	 * @deprecated use {@link #getRedoControlEventManager()} and 
	 * {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * 			instead.
	 */
	public Iterator<ControlEvent> getRedoEventsIterator() {
		return this.redoEvents.iterator();
	}
	
	/**
	 * This method returns the detector ready event.
	 * 
	 * @return The detector ready event.
	 */
	public Event getDetectorReadyEvent() {
		return detectorReadyEvent;
	}
	
	/**
	 * 
	 * @return true if channel sends an ready event
	 */
	public Boolean hasReadyEvent() {
		if (detectorReadyEvent != null){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param detectorReadyEvent add a detectorReadyEvent
	 */
	public void setDetectorReadyEvent(final Event detectorReadyEvent) {
		this.detectorReadyEvent = detectorReadyEvent;
		updateListeners();
	}

	/**
	 * This method returns 
	 * 
	 * @return
	 */
	public ControlEventManager getRedoControlEventManager() {
		return redoControlEventManager;
	}
	
	/**
	 * Sets the detector channel that will be controlled by this behavior.
	 * 
	 * @param detectorChannel The detector channel that will be controlled by this behavior.
	 */
	public void setDetectorChannel(final DetectorChannel detectorChannel) {
		this.abstractDevice = detectorChannel;
		updateListeners();
	}
	
	/**
	 * Gives back the detector channel that is controlled by this behavior.
	 * 
	 * @return The detector channel that is controlled by this behavior.
	 */
	public DetectorChannel getDetectorChannel() {
		return (DetectorChannel)this.abstractDevice;
	}

	/**
	 * Resets the channel to its default values.
	 */
	public void reset() {
		this.averageCount = 1;
		this.maxAttempts = Integer.MIN_VALUE;
		this.maxDeviation = Double.NEGATIVE_INFINITY;
		this.minimum = Double.NEGATIVE_INFINITY;
		this.repeatOnRedo = false;
		this.normalizeChannel = null;
		this.redoControlEventManager.removeAllControlEvents();
		logger.debug("Channel " + this.getDetectorChannel().getName()
				+ " has been reset");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> modelErrors = new ArrayList<IModelError>();

		if(Double.compare(this.maxDeviation, Double.NaN) == 0) {
			modelErrors.add(new ChannelError(this, 
					ChannelErrorTypes.MAX_DEVIATION_NOT_POSSIBLE));
		}
		if(Double.compare(this.minimum, Double.NaN) == 0) {
			modelErrors.add(new ChannelError(this, 
					ChannelErrorTypes.MINIMUM_NOT_POSSIBLE));
		}
		if(this.redoControlEventManager.getModelErrors().size() > 0) {
			modelErrors.addAll(this.redoControlEventManager.getModelErrors());
			modelErrors.add(new ChannelError(this, 
					ChannelErrorTypes.PLUGIN_ERROR));
		}
		return modelErrors;
	}
	
	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(modelUpdateListener);
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (this.normalizeChannel == null) {
			return;
		}

		if (this.getScanModule().sm_loading) {
			// Scan is loading
			return;
		}

		if (e.getPropertyName().equals(ScanModule.CHANNELS_PROP)) {
			if (!((List<Channel>)e.getNewValue()).contains(this.normalizeChannel)) {
				// normalize channel has been deleted -> remove
				this.setNormalizeChannel(null);
			}
		}
	}

	/**
	 * See 
	 * {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)}.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
			this.propertyChangeSupport.addPropertyChangeListener(propertyName,
					listener);
	}
	
	/**
	 * See
	 * {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)}.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
}