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
	private static final String HEAVY_CHECK_MARK = Character.toString('\u2714');
	
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
			if (channel.isDeferred()) {
				return HEAVY_CHECK_MARK;
			} else {
				return "";
			}
		} else if (channel.getChannelMode().equals(ChannelModes.INTERVAL) && 
				channel.getStoppedBy() != null) {
			if (channel.getScanModule().getChannel(
					channel.getStoppedBy()) != null) {
				if (channel.getScanModule().getChannel(
						channel.getStoppedBy()).isDeferred()) {
					return HEAVY_CHECK_MARK;
				} else {
					return "";
				}
			}
			return DASH;
		} else {
			return DASH;
		}
	}
}
