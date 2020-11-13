package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.RGB;

import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.PredefinedColors;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ColorComboModelToTargetConverter implements IConverter {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return RGB.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return PredefinedColors.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		RGB color = (RGB)fromObject;
		for (PredefinedColors predefColor : PredefinedColors.values()) {
			if (predefColor.getColor().equals(color)) {
				return predefColor;
			}
		}
		return PredefinedColors.CUSTOM;
	}
}
