package de.ptb.epics.eve.data.measuringstation.event;

import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * A detector event is triggered when a detector read has finished.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEvent extends ScanEvent {
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
				this.channel.getAbstractDevice().getName();
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
}