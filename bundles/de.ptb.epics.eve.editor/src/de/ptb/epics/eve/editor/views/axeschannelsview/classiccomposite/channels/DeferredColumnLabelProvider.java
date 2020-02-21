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
	private static final String BOX_WITH_CHECK = Character.toString('\u2611');
	private static final String BOX_EMPTY = Character.toString('\u2610');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Channel channel = (Channel)element;
		
		if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
			String test = Character.toString('\u2611') + 
					Character.toString('\u2713') + 
					Character.toString('\u2714') + 
					Character.toString('\u2705') + 
					Character.toString('\u2612') + 
					Character.toString('\u2610') +
					Character.toString('\u2715') + 
					Character.toString('\u274e') +
					Character.toString('\u2717') + 
					Character.toString('\u2718') +
					Character.toString('\u2716') +
					Character.toString('\u274c');
			return test;//DASH;
		}
		if (channel.getChannelMode().equals(ChannelModes.STANDARD)) {
			if (channel.isDeferred()) {
				return BOX_WITH_CHECK;
			} else {
				return BOX_EMPTY;
			}
		} else if (channel.getChannelMode().equals(ChannelModes.INTERVAL) && 
				channel.getStoppedBy() != null) {
			if (channel.getScanModule().getChannel(
					channel.getStoppedBy()) != null) {
				if (channel.getScanModule().getChannel(
						channel.getStoppedBy()).isDeferred()) {
					return BOX_WITH_CHECK;
				} else {
					return BOX_EMPTY;
				}
			}
			return DASH;
		} else {
			return DASH;
		}
	}
}
