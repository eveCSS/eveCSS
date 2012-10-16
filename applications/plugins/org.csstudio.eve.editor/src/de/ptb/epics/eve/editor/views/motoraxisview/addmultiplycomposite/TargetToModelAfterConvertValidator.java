package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.measuringstation.MotorAxisChannelAccess;
import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TargetToModelAfterConvertValidator implements IValidator {
	private static final Logger LOGGER = Logger
			.getLogger(TargetToModelAfterConvertValidator.class.getName());
	
	private Axis axis;
	
	/**
	 * @param axis the axis to validate with
	 */
	public TargetToModelAfterConvertValidator(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		MotorAxisChannelAccess ca = null;
		if (this.axis.getMotorAxis().getChannelAccess() != null) {
			ca = this.axis.getMotorAxis().getChannelAccess();
		}
		if (ca == null) {
			LOGGER.warn("motor position could not be verified due to missing channel access");
			return ValidationStatus.ok();
		}
		Double val = null;
		switch (this.axis.getType()) {
		case DOUBLE:
			val = (Double)value;
			break;
		case INT:
			val = (double) ((Integer)value).intValue();
			break;
		default:
			break;
		}
		if (val == null) {
			LOGGER.warn("value could not be read");
			return ValidationStatus.ok();
		}
		if (ca.getLowLimit() != null && ca.getLowLimit() > val) {
			return ValidationStatus.warning(
					"The entered value is below the current low limit!");
		} else if (ca.getHighLimit() != null && ca.getHighLimit() < val) {
			return ValidationStatus.warning(
					"The entered value is above the current high limit!");
		}
		return ValidationStatus.ok();
	}
}