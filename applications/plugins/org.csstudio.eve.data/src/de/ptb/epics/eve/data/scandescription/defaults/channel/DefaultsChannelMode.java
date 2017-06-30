package de.ptb.epics.eve.data.scandescription.defaults.channel;

import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public abstract class DefaultsChannelMode {
	/**
	 * Returns the corresponding channel mode of this defaults channel mode.
	 * @return the corresponding channel mode of this defaults channel mode.
	 * @since 1.27.6
	 */
	public abstract ChannelModes getType();
}