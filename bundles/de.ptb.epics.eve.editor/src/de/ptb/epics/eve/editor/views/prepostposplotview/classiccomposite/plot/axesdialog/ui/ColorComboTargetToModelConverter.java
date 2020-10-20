package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.RGB;

import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.PredefinedColors;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ColorComboTargetToModelConverter implements IConverter {
	private YAxis yAxis;
	
	public ColorComboTargetToModelConverter(YAxis yAxis) {
		this.yAxis = yAxis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return PredefinedColors.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return RGB.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		PredefinedColors predefColor = (PredefinedColors)fromObject;
		if (predefColor.equals(PredefinedColors.CUSTOM)) {
			return yAxis.getColor();
		}
		return predefColor.getColor();
	}
}
