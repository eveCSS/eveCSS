package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime;

import java.util.Date;
import java.text.SimpleDateFormat;

import javax.xml.datatype.Duration;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeModelToTargetConverter implements IConverter {
	private Axis axis;
	private SimpleDateFormat dateFormat;

	/**	 
	 * @param axis the axis
	 * @param shortDate if <code>true</code> converted date Strings are 
	 * 		formatted with <code>HH:mm:ss.SSS</code>, 
	 * 		<code>yyyy-MM-dd HH:mm:ss.SSS</code> will be used otherwise
	 */
	public AddDateTimeModelToTargetConverter(Axis axis, boolean shortDate) {
		this.axis = axis;
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
		switch (axis.getPositionMode()) {
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
	public Object getToType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		switch (axis.getPositionMode()) {
		case ABSOLUTE:
			return this.dateFormat.format((Date)fromObject);
		case RELATIVE:
			return ((Duration)fromObject).toString().substring(8);
		default:
			return fromObject.toString();
		}
	}

}
