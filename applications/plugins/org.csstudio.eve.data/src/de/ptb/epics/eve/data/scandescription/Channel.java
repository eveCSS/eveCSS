package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelMode;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class describes the behavior of a detector during the main phase.
 * 
 * Context of state pattern.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class Channel extends AbstractMainPhaseBehavior implements
		PropertyChangeListener {
	public static final String NORMALIZE_CHANNEL_PROP = "normalizeChannel";
	public static final String CHANNEL_MODE_PROP = "channelMode";
	
	private static final Logger LOGGER = Logger.getLogger(Channel.class.getName());
	
	// delegated observable
	private PropertyChangeSupport propertyChangeSupport;

	private DetectorChannel normalizeChannel;

	private ChannelMode channelMode;
	
	/*
	 * Indicates when the channel was loaded into the scan module.
	 * Should be used during Loading to preserve the order in 
	 * which they were introduced into the scan module. 
	 * E.g. during XML Loading channels are reordered to account for
	 * normalization dependencies. Afterwards the original order can 
	 * be reestablished.
	 */
	private int loadTime;
	
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
		this.channelMode = new StandardMode(this);
		this.channelMode.addPropertyChangeListener(this);
		this.normalizeChannel = null;
		this.loadTime = 0;
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
		newChannel.setNormalizeChannel(channel.getNormalizeChannel());
		newChannel.setLoadTime(channel.getLoadTime());
		
		switch (channel.getChannelMode()) {
		case STANDARD:
			newChannel.setChannelMode(ChannelModes.STANDARD);
			newChannel.setAverageCount(channel.getAverageCount());
			newChannel.setMaxAttempts(new Integer(channel.getMaxAttempts().intValue()));
			newChannel.setMaxDeviation(new Double(channel.getMaxDeviation().doubleValue()));
			newChannel.setMinimum(new Double(channel.getMinimum().doubleValue()));
			newChannel.setDeferred(channel.isDeferred());
			for (ControlEvent event : channel.getRedoEvents()) {
				newChannel.addRedoEvent(ControlEvent.newInstance(event));
			}
			break;
		case INTERVAL:
			newChannel.setChannelMode(ChannelModes.INTERVAL);
			newChannel.setTriggerInterval(channel.getTriggerInterval());
			newChannel.setStoppedBy(channel.getStoppedBy());
			break;
		}
		return newChannel;
	}

	/**
	 * Returns the current channel mode. Available modes are defined by 
	 * {@link de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes}.
	 * 
	 * @return the current channel mode
	 * @throws IllegalStateException if no channel mode is set
	 */
	public ChannelModes getChannelMode() {
		if (this.channelMode instanceof StandardMode) {
			return ChannelModes.STANDARD;
		} else if (this.channelMode instanceof IntervalMode) {
			return ChannelModes.INTERVAL;
		}
		throw new IllegalStateException("no channel mode set!");
	}
	
	/**
	 * Sets the Channel Mode. Available modes are defined by 
	 * {@link de.ptb.epics.eve.data.scandescription.channelmode.ChannelMode}.
	 * 
	 * @param channelMode the channelMode to set
	 */
	public void setChannelMode(ChannelModes channelMode) {
		ChannelModes oldValue = this.getChannelMode();
		this.channelMode.removePropertyChangeListener(this);
		switch (channelMode) {
		case STANDARD:
			this.channelMode = new StandardMode(this);
			break;
		case INTERVAL:
			this.channelMode = new IntervalMode(this);
			break;
		}
		this.channelMode.addPropertyChangeListener(this);
		this.propertyChangeSupport.firePropertyChange(Channel.CHANNEL_MODE_PROP, oldValue, channelMode);
		this.updateListeners();
	}

	/**
	 * Returns the average count (how often the detector should be read to make an average
	 * result for the measuring).
	 * 
	 * @return the average count
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public int getAverageCount() {
		return this.channelMode.getAverageCount();
	}

	/**
	 * Sets the average count (how often the detector should be read to make an average result
	 * for the measuring).
	 * 
	 * @param averageCount How often the detector should be read.
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public void setAverageCount(final int averageCount) {
		this.channelMode.setAverageCount(averageCount);
	}

	/**
	 * Returns the maximum attempts to read the detector or <code>null</code> if none.
	 * 
	 * @return the maximum attempts to read the detector or <code>null</code> if none
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public Integer getMaxAttempts() {
		return this.channelMode.getMaxAttempts();
	}

	/**
	 * Sets the maximum attempts to read the detector.
	 * 
	 * @param maxAttempts the maximum attempts to read the detector or <code>null</code> to reset
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public void setMaxAttempts(final Integer maxAttempts) {
		this.channelMode.setMaxAttempts(maxAttempts);
	}

	/**
	 * Returns the maximum deviation between the taken values or <code>null</code> if none,
	 *  
	 * @return The maximum deviation.
	 */
	public Double getMaxDeviation() {
		return this.channelMode.getMaxDeviation();
	}

	/**
	 * Sets the maximum deviation.
	 * 
	 * @param maxDeviation the new maximum deviation or <code>null</code> to reset
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public void setMaxDeviation(final Double maxDeviation) {
		this.channelMode.setMaxDeviation(maxDeviation);
	}

	/**
	 * Returns the minimum or <code>null<code> if none
	 * 
	 * @return the minimum
	 */
	public Double getMinimum() {
		return this.channelMode.getMinimum();
	}

	/**
	 * Sets the minimum for this channel.
	 * 
	 * @param minimum the new minimum or <code>null</code> to reset
	 * @throws IllegalStateException if the channel is not in standard mdoe
	 */
	public void setMinimum(final Double minimum) {
		this.channelMode.setMinimum(minimum);
	}

	/**
	 * Returns whether the channel should be triggered in the second trigger phase 
	 * (after channels which are not deferred).
	 * 
	 * @return <code>true</code> if the channel should be triggered in the second trigger phase, 
	 * 		<code>false</code> otherwise
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public boolean isDeferred() {
		return this.channelMode.isDeferred();
	}

	/**
	 * Sets whether the channel should be triggered in the second trigger phase
	 * 
	 * @param deferred <code>true</code> if the channel should be triggered in the second trigger phase, 
	 * 		<code>false</code> otherwise
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public void setDeferred(boolean deferred) {
		this.channelMode.setDeferred(deferred);
	}

	/**
	 * Returns the trigger interval
	 * @return the trigger interval
	 * @throws IllegalStateException if the channel is not in interval mode
	 */
	public double getTriggerInterval() {
		return this.channelMode.getTriggerInterval();
	}
	
	/**
	 * Sets the trigger interval
	 * @param triggerInterval the trigger delay to set
	 * @throws IllegalStateException if the channel is not in interval mode
	 */
	public void setTriggerInterval(double triggerInterval) {
		this.channelMode.setTriggerInterval(triggerInterval);
	}
	
	/**
	 * Returns the channel that should stop the interval measure or <code>null</code> if none
	 * @return the channel that should stop the interval measure ir <code>null</code> if none
	 * @throws IllegalStateException if the channel is not in interval mode
	 */
	public DetectorChannel getStoppedBy() {
		return this.channelMode.getStoppedBy();
	}
	
	/**
	 * Sets the channel that should stop the interval measure.
	 * @param stoppedBy the channel that should stop the interval measure
	 * @throws IllegalStateException if the channel is not in interval mode
	 */
	public void setStoppedBy(DetectorChannel stoppedBy) {
		this.channelMode.setStoppedBy(stoppedBy);
	}
	
	/**
	 * @return the normalizeChannel
	 */
	public DetectorChannel getNormalizeChannel() {
		return normalizeChannel;
	}

	/**
	 * Sets a channel for normalization. Only valid channels (as given by 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule#getValidNormalizationChannels(Channel)}) 
	 * are eligible.
	 * 
	 * @param normalizeChannel the normalizeChannel to set
	 */
	public void setNormalizeChannel(DetectorChannel normalizeChannel) {
		boolean valid = false;
		if (normalizeChannel == null) {
			valid = true;
		} else {
			for (Channel ch : this.getScanModule()
					.getValidNormalizationChannels(this)) {
				if (ch.getDetectorChannel().getID()
						.equals(normalizeChannel.getID())) {
					valid = true;
				}
			}
		}
		if (!valid) {
			LOGGER.warn(normalizeChannel.getName()
					+ " is not valid for normalization of "
					+ this.getDetectorChannel().getName());
			return;
		}
		this.propertyChangeSupport.firePropertyChange(Channel.NORMALIZE_CHANNEL_PROP, 
				this.normalizeChannel, this.normalizeChannel = normalizeChannel);
		updateListeners();
	}

	/**
	 * Returns a list of redo events (original List).
	 * 
	 * @return a list of redo events (original list)
	 * @throws IllegalStateException if the channel is not in standard mode
	 * @author Marcus Michalsky
	 * @since 1.19
	 * @since 1.27 delegation to channel mode
	 */
	public List<ControlEvent> getRedoEvents() {
		return this.channelMode.getRedoControlEventManager().getEvents();
	}
	
	/**
	 * Adds a redo event. 
	 * 
	 * @param redoEvent the redo event that should be added
	 * @return <code>true</code> if the event has been added, 
	 * 			<code>false</code> otherwise
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public boolean addRedoEvent(final ControlEvent redoEvent) {
		return this.channelMode.addRedoEvent(redoEvent);
	}

	/**
	 * Removes a redo event.
	 * 
	 * @param redoEvent the redo event that should be removed
	 * @return <code>true</code> if the event has been removed, 
	 * 			<code>false</code> otherwise
	 * @throws IllegalStateException if the channel is not in standard mode
	 */
	public boolean removeRedoEvent(final ControlEvent redoEvent) {
		return this.channelMode.removeRedoEvent(redoEvent);
	}
	
	/**
	 * Removes all redo events.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void removeRedoEvents() {
		this.channelMode.removeRedoEvents();
	}

	/**
	 * Present for backward compatibility.
	 * TODO Collection should not be exposed
	 * 
	 * @return the control event manager
	 * @throws IllegalStateException if channel is not in standard mode
	 */
	public ControlEventManager getRedoControlEventManager() {
		return this.channelMode.getRedoControlEventManager();
	}
	
	/**
	 * Sets the detector channel that will be controlled by this behavior.
	 * 
	 * @param detectorChannel The detector channel that will be controlled by this behavior.
	 */
	public void setDetectorChannel(final DetectorChannel detectorChannel) {
		this.abstractDevice = detectorChannel;
		this.setDeferred(detectorChannel.isDeferred());
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
	 * @return the loadTime
	 */
	public int getLoadTime() {
		return loadTime;
	}

	/**
	 * @param loadTime the loadTime to set
	 */
	public void setLoadTime(int loadTime) {
		this.loadTime = loadTime;
	}

	/**
	 * Resets the channel to its default values.
	 */
	public void reset() {
		this.normalizeChannel = null;
		if(this.channelMode != null) {
			this.channelMode.reset();
		}
		LOGGER.debug("Channel " + this.getDetectorChannel().getName()
				+ " has been reset");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		return this.channelMode.getModelErrors();
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
		if (this.getScanModule().smLoading) {
			// Scan is loading
			return;
		}

		if (e.getPropertyName().equals(ScanModule.CHANNELS_PROP)) {
			boolean changes = false;
			boolean normalizeChannelFound = false;
			boolean stoppedByChannelFound = false;
			for (Channel channel : ((List<Channel>)e.getNewValue())) {
				if (channel.getDetectorChannel().equals(this.normalizeChannel)) {
					normalizeChannelFound = true;
				}
				if (this.getChannelMode().equals(ChannelModes.INTERVAL) 
						&& this.getStoppedBy() != null
						&& this.getStoppedBy().equals(channel.getAbstractDevice())) {
					stoppedByChannelFound = true;
				}
			}
			if (!normalizeChannelFound) {
				this.setNormalizeChannel(null);
				changes = true;
			}
			if (this.getChannelMode().equals(ChannelModes.INTERVAL) && !stoppedByChannelFound) {
				this.setStoppedBy(null);
				changes = true;
			}
			if (!changes) {
				return;
			}
		}
		
		this.propertyChangeSupport.firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
		this.updateListeners();
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
	
	/**
	 * Due to the late registration of ScanEvents (due to mutability) during 
	 * scan description loading the control events don't register themselves 
	 * via registerEventValidProperty(ControlEvent) because their events aren't
	 * set at that time. So it must be triggered manually afterwards.
	 * Usage of this function is therefore only necessary during scan description 
	 * loading.
	 * 
	 * @throws IllegalStateException if the channel is not in standard mode
	 * @author Marcus Michalsky
	 * @since 1.19
	 * @since 1.27 delegated to standard mode
	 * @see Redmine #1401 Comments #16,#17
	 */
	public void registerEventValidProperties() {
		this.channelMode.registerEventValidProperties();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (this.getDetectorChannel() != null) {
			return this.getDetectorChannel().getName();
		}
		return super.toString();
	}
}