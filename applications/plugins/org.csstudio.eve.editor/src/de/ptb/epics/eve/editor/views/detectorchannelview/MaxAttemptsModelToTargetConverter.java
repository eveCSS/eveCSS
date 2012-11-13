package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Necessary because of Magic Number in Channel model.
 * (Just translates Integer.MIN_VALUE into an empty String).
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class MaxAttemptsModelToTargetConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return Integer.class;
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
		Integer i = (Integer)fromObject;
		if (i.equals(Integer.MIN_VALUE)) {
			return "";
		}
		return i.toString();
	}
}