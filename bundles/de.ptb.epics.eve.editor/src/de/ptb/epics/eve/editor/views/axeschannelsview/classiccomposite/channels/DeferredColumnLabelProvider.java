package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class DeferredColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Channel channel = (Channel)element;
		
		if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
			return StringLabels.EM_DASH;
		}
		if (channel.getChannelMode().equals(ChannelModes.STANDARD)) {
			if (channel.isDeferred()) {
				return StringLabels.HEAVY_CHECK_MARK;
			} else {
				return "";
			}
		} else if (channel.getChannelMode().equals(ChannelModes.INTERVAL) && 
				channel.getStoppedBy() != null) {
			if (channel.getScanModule().getChannel(
					channel.getStoppedBy()) != null) {
				if (channel.getScanModule().getChannel(
						channel.getStoppedBy()).isDeferred()) {
					return StringLabels.HEAVY_CHECK_MARK;
				} else {
					return StringLabels.EMPTY;
				}
			}
			return StringLabels.EM_DASH;
		} else {
			return StringLabels.EM_DASH;
		}
	}
}
