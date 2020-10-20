package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class NormalizeComboTargetToModelConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return Channel.class;
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
		return ((Channel)fromObject).getDetectorChannel();
	}
}
