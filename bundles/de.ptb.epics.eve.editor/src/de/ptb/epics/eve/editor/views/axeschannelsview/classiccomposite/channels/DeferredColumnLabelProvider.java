package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class DeferredColumnLabelProvider extends ColumnLabelProvider {
	private static final String DASH = Character.toString('\u2014');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Channel channel = (Channel)element;
		
		if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
			return DASH;
		}
		if (channel.getChannelMode().equals(ChannelModes.STANDARD)) {
			return Boolean.toString(channel.isDeferred());
		} else if (channel.getChannelMode().equals(ChannelModes.INTERVAL) && 
				channel.getStoppedBy() != null) {
			if (channel.getScanModule().getChannel(
					channel.getStoppedBy()) != null) {
				return Boolean.toString(channel.getScanModule().getChannel(
						channel.getStoppedBy()).isDeferred());
			}
			return DASH;
		} else {
			return DASH;
		}
	}
}
