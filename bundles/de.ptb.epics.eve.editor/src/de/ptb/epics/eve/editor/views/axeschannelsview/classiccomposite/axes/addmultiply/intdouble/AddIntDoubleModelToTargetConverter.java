package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleModelToTargetConverter implements IConverter {
	private Axis axis;
	
	public AddIntDoubleModelToTargetConverter(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		switch (this.axis.getType()) {
		case DOUBLE:
			return Double.class;
		case INT:
			return Integer.class;
		default:
			break;
		}
		return null;
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
