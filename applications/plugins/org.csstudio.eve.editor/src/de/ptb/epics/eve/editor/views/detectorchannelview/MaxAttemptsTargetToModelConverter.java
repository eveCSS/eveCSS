package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class MaxAttemptsTargetToModelConverter implements IConverter {

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
		return Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		String val = (String)fromObject;
		if (val.isEmpty()) {
			return Integer.MIN_VALUE;
		}
		return Integer.parseInt(val);
	}
}