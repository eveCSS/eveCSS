package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ValueCountTargetToModelConverter implements IConverter {

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
			return 1;
		}
		return Integer.parseInt(val);
	}
}
