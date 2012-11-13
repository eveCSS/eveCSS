package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Necessary because of Magic Number in Channel model.
 * (just translates Double.NEGATIVE_INFINITY into an empty String).
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DoubleModelToTargetConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return Double.class;
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
		Double d = (Double)fromObject;
		if (d.equals(Double.NEGATIVE_INFINITY)) {
			return "";
		}
		return d.toString();
	}
}