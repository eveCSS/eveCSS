package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TargetToModelConverter implements IConverter {

	private static final Logger LOGGER = Logger
			.getLogger(TargetToModelConverter.class.getName());
	
	private DataTypes type;
	
	/**
	 * @param type the type
	 */
	public TargetToModelConverter(DataTypes type) {
		this.type = type;
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
	public Object convert(Object fromObject) {
		switch (this.type) {
		case DOUBLE:
			Double dVal = Double.parseDouble((String)fromObject);
			LOGGER.debug("converted " + dVal.toString() + " to String");
			return dVal;
		case INT:
			Integer iVal = Integer.parseInt(((String)fromObject));
			LOGGER.debug("converted " + iVal.toString() + " to String");
			return iVal;
		default:
			return null;
		}
	}
}