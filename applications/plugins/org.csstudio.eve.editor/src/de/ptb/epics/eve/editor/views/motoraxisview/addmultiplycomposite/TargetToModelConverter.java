package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	public Object convert(Object fromObject) {
		switch (this.type) {
		case DOUBLE:
			try {
				Double dVal = Double.parseDouble((String)fromObject);
				LOGGER.debug("converted " + dVal.toString() + " to Double");
				return dVal;
			} catch (NumberFormatException e) {
				LOGGER.warn(e.getMessage(), e);
				return null;
			}
		case INT:
			Integer iVal = Integer.parseInt(((String)fromObject));
			LOGGER.debug("converted " + iVal.toString() + " to Integer");
			return iVal;
		case DATETIME:
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
						.parse((String) fromObject);
				LOGGER.debug("converted " + date.toString() + " to Date");
				return date;
			} catch (ParseException e) {
				LOGGER.error(e.getMessage(), e);
				return null;
			}
		default:
			return null;
		}
	}
}