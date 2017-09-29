package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.27
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
		if (fromObject == null) {
			return "";
		}
		return ((Double)fromObject).toString();
	}
}