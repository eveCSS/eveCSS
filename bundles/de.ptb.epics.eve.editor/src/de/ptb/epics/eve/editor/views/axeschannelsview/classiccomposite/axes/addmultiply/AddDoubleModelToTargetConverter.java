package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDoubleModelToTargetConverter implements IConverter {

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
		return fromObject.toString();
	}
}
