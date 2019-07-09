package de.ptb.epics.eve.editor.views.scanview;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.31.2
 */
public class RepeatCountConverter implements IConverter {

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
			return 0;
		}
		return Integer.parseInt(val);
	}
}