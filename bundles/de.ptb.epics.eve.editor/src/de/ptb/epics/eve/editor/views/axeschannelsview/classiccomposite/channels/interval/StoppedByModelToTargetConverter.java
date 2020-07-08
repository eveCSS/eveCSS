package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class StoppedByModelToTargetConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return DetectorChannel.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		return ((DetectorChannel)fromObject).getName();
	}
}
