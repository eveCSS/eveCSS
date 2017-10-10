package de.ptb.epics.eve.data.measuringstation.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

import javafx.collections.ListChangeListener;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;

/**
 * A detector event is triggered when a detector read has finished.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEvent extends ScanEvent implements 
		ListChangeListener<Channel>, PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(DetectorEvent.class
			.getName());
	
	private Channel channel;
	private final String id;
	private final String name;
	
	public DetectorEvent(Channel channel) {
		this.channel = channel;
		this.id = "D-" +
				channel.getScanModule().getChain().getId() + "-" + 
				channel.getScanModule().getId() + "-" + 
				channel.getDetectorChannel().getID();
		this.name = this.channel.getAbstractDevice().getParent().getName() + 
				" " + (char)187 + " " + 
				this.channel.getAbstractDevice().getName() + 
				" ( D-" + this.channel.getScanModule().getChain().getId() + 
				"-" + this.channel.getScanModule().getId() + " )";
		this.channel.getScanModule().addChannelChangeListener(this);
		//this.channel.getScanModule().getChain().addScanModuleChangeListener(this);
		this.channel.addPropertyChangeListener(Channel.CHANNEL_MODE_PROP, this);
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the chain id the channel belongs to
	 * @return the chain id the channel belongs to
	 */
	public int getChainId() {
		return this.channel.getScanModule().getChain().getId();
	}
	
	/**
	 * Returns the scan module id the channel belongs to
	 * @return the scan module id the channel belongs to
	 */
	public int getScanModuleId() {
		return this.channel.getScanModule().getId();
	}
	
	private void invalidate() {
		LOGGER.debug(this.channel.getDetectorChannel().getName() + 
				" was deleted -> event is invalid");
		this.channel.removePropertyChangeListener(Channel.CHANNEL_MODE_PROP, this);
		this.channel.getScanModule().removeChannelChangeListener(this);
		this.propertyChangeSupport.firePropertyChange(
				ScanEvent.VALID_PROP, this.valid,
				this.valid = false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(
			ListChangeListener.Change<? extends Channel> change) {
		while (change.next()) {
			if (change.wasRemoved()) {
				for (Channel ch : change.getRemoved()) {
					if (ch == this.getChannel()) {
						this.invalidate();
					}
				}
			}
		}
		// TODO notify when the scan module the detector is in is being removed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Channel.CHANNEL_MODE_PROP) 
				&& !e.getNewValue().equals(ChannelModes.STANDARD)) {
			this.invalidate();
		}
	}
}