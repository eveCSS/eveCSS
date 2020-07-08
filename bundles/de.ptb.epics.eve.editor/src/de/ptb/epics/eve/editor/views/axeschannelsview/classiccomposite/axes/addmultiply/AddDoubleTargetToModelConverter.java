package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDoubleTargetToModelConverter implements IConverter {
	private static final Logger LOGGER = Logger.getLogger(
			AddDoubleTargetToModelConverter.class.getName());
	
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
		try {
			return Double.parseDouble((String)fromObject);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
}
