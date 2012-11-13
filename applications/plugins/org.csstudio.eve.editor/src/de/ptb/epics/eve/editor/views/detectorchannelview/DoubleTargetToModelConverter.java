package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * For Minimum and MaxDeviation properties.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DoubleTargetToModelConverter implements IConverter {

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
		return Double.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		String val = (String)fromObject;
		if (val.isEmpty()) {
			return Double.NEGATIVE_INFINITY;
		}
		return Double.parseDouble(val);
	}
}