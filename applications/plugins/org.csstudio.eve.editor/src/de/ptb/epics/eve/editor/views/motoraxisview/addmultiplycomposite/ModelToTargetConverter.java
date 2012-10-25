package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;


import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	private SimpleDateFormat dateFormat;
	
	/**
	 * @param type the type
	 * @param shortDate if <code>true</code> converted date Strings are 
	 * 		formatted with <code>HH:mm:ss.SSS</code>, 
	 * 		<code>yyyy-MM-dd HH:mm:ss.SSS</code> will be used otherwise
	 */
	public ModelToTargetConverter(DataTypes type, boolean shortDate) {
		this.type = type;
		if (shortDate) {
			this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		} else {
			this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
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
		case DATETIME:
			return Date.class;
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
		switch (type) {
		case DOUBLE:
			if (((Double)fromObject).isNaN()) {
				return "0.0";
			}
			break;
		case INT: 
			break;
		case DATETIME:
			return this.dateFormat.format((Date)fromObject);
		default: return fromObject.toString();
		}
		return fromObject.toString();
	}
}