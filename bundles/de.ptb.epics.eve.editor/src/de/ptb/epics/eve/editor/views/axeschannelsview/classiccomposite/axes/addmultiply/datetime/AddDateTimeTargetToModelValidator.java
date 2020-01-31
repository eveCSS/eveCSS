package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeTargetToModelValidator implements IValidator {
	private Axis axis;
	
	public AddDateTimeTargetToModelValidator(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		switch (this.axis.getPositionMode()) {
		case ABSOLUTE:
			try {
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(
						value.toString());
				return ValidationStatus.ok();
			} catch (ParseException e) {
				try {
					new SimpleDateFormat("HH:mm:ss.SSS").parse(value.toString());
					return ValidationStatus.ok();
				} catch (ParseException e1) {
					return ValidationStatus.error(e1.getMessage());
				}
			}
		case RELATIVE:
			try {
				DatatypeFactory factory = DatatypeFactory.newInstance();
				factory.newDuration("P0Y0M0DT" + (value.toString()));
				return ValidationStatus.ok();
			} catch (DatatypeConfigurationException e) {
				return ValidationStatus.error(e.getMessage());
			}
		default:
			return ValidationStatus.error("Unknown position mode.");
		}
	}
}
