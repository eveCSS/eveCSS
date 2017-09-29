package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TargetToModelConverter implements IConverter {

	private static final Logger LOGGER = Logger
			.getLogger(TargetToModelConverter.class.getName());
	
	private DataTypes type;
	private Axis axis;
	
	/**
	 * @param type the type
	 * @param axis the axis
	 */
	public TargetToModelConverter(DataTypes type, Axis axis) {
		this.type = type;
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
		switch (type) {
		case DOUBLE:
			return Double.class;
		case INT: 
			return Integer.class;
		case DATETIME:
			switch (this.axis.getPositionMode()) {
			case ABSOLUTE:
				return Date.class;
			case RELATIVE:
				return Duration.class;
			}
			return null;
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
			switch (this.axis.getPositionMode()) {
			case ABSOLUTE:
				try {
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
							.parse((String) fromObject);
					LOGGER.debug("converted " + date.toString() + " to Date");
					return date;
				} catch (ParseException e) {
					try {
						Date date = new SimpleDateFormat("HH:mm:ss.SSS")
								.parse((String) fromObject);
						LOGGER.debug("converted " + date.toString() + " to Date");
						return date;
					} catch (ParseException e1) {
						LOGGER.error(e1.getMessage(), e1);
						return null;
					}
				}
			case RELATIVE:
				try {
					DatatypeFactory factory = DatatypeFactory.newInstance();
					return factory.newDuration("P0Y0M0DT" + (String)fromObject);
				} catch (DatatypeConfigurationException e) {
					LOGGER.error(e.getMessage(), e);
				}
				break;
			}
			break;
		default:
			return null;
		}
		return null;
	}
}