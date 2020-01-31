package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeTargetToModelConverter implements IConverter {
	private static final Logger LOGGER = Logger.getLogger(
			AddDateTimeTargetToModelConverter.class.getName());
	
	private Axis axis;
	
	public AddDateTimeTargetToModelConverter(Axis axis) {
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
		switch (this.axis.getPositionMode()) {
		case ABSOLUTE:
			return Date.class;
		case RELATIVE:
			return Duration.class;
		default:
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
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
		default:
			return null;
		}
		return null;
	}
}
