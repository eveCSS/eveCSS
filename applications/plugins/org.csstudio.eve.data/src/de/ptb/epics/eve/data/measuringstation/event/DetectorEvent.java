package de.ptb.epics.eve.data.measuringstation.event;

import org.apache.log4j.Logger;

import javafx.collections.ListChangeListener;
import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * A detector event is triggered when a detector read has finished.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEvent extends ScanEvent implements 
		ListChangeListener<Channel> {
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
						LOGGER.debug(ch.getDetectorChannel().getName() + 
								" was deleted -> event is invalid");
						ch.getScanModule().removeChannelChangeListener(this);
						this.propertyChangeSupport.firePropertyChange(
								ScanEvent.VALID_PROP, this.valid,
								this.valid = false);
					}
				}
			}
		}
		// TODO notify when the scan module the detector is in is being removed
	}
}