package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class TriggerDelaySettleTimeTargetToModelConverter implements IConverter {

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
			return 0.0;
		}
		return Double.parseDouble(val);
	}
}
