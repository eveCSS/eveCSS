package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class StoppedByTargetToModelConverter implements IConverter {
	private Channel channel;
	
	public StoppedByTargetToModelConverter(Channel channel) {
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return DetectorChannel.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;
		}
		for (Channel ch : this.channel.getScanModule().getValidStoppedByChannels(channel)) {
			if (ch.getAbstractDevice().getName().equals((String)fromObject)) {
				return ch.getDetectorChannel();
			}
		}
		return null;
	}
}
