package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class ModelToTargetConverter implements IConverter {

	private static final Logger LOGGER = Logger
			.getLogger(ModelToTargetConverter.class.getName());
	
	private DataTypes type;
	
	/**
	 * @param type the type
	 */
	public ModelToTargetConverter(DataTypes type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		switch (type) {
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
	public Object getToType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		LOGGER.debug("converted: " + fromObject.toString() + " (was " + type
				+ ")");
		return fromObject.toString();
	}
}