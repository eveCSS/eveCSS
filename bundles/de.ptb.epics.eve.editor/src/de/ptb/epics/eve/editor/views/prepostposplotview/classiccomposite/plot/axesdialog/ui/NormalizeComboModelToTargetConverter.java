package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.YAxis;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class NormalizeComboModelToTargetConverter implements IConverter {
	private YAxis yAxis;
	
	public NormalizeComboModelToTargetConverter(YAxis yAxis) {
		this.yAxis = yAxis;
	}
	
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
		return Channel.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		return yAxis.getNormalizeChannel();
	}
}
