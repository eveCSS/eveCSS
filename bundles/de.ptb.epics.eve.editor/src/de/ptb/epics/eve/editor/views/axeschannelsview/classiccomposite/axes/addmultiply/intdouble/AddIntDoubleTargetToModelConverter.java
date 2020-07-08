package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTargetToModelConverter implements IConverter {
	private static final Logger LOGGER = Logger.getLogger(
			AddIntDoubleTargetToModelConverter.class.getName());
	
	private Axis axis;
	
	public AddIntDoubleTargetToModelConverter(Axis axis) {
		this.axis = axis;
	}

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
		switch (this.axis.getType()) {
		case DOUBLE:
			return Double.class;
		case INT:
			return Integer.class;
		default:
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		switch (this.axis.getType()) {
		case DOUBLE:
			try {
				return Double.parseDouble((String)fromObject);
			} catch (NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}
			break;
		case INT:
			try {
				return Integer.parseInt((String)fromObject);
			} catch (NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}
			break;
		default:
			return null;
		}
		return null;
	}
}
