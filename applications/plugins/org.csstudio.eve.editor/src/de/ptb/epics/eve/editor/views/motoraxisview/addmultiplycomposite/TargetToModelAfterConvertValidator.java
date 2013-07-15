package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.TransportTypes;
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
		if (axis.getMotorAxis().getGoto().getAccess().getTransport()
				.equals(TransportTypes.LOCAL)) {
			// skip validation for local variables
			return ValidationStatus.ok();
		}
		MotorAxisChannelAccess ca = null;
		Double mPos = null;
		if (this.axis.getMotorAxis().getChannelAccess() != null) {
			ca = this.axis.getMotorAxis().getChannelAccess();
			mPos = ca.getPosition();
		}
		if (ca == null || mPos == null) {
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
		if (LOGGER.isDebugEnabled()) {
			String log = "Position Mode: " + this.axis.getPositionMode()
					+ " , value: " + val + " , mPos: " + mPos;
			if (ca.getLowLimit() != null) {
				log = log.concat(" , LLM: " + ca.getLowLimit());
			}
			if (ca.getHighLimit() != null) {
				log = log.concat(" , HLM: " + ca.getHighLimit());
			}
			LOGGER.debug(log);
		}
		switch(this.axis.getPositionMode()) {
		case ABSOLUTE:
			if (ca.getLowLimit() != null && ca.getLowLimit() > val) {
				LOGGER.info("Value is below the current low limit!");
				return ValidationStatus.warning(
						"Value is below the current low limit!");
			} else if (ca.getHighLimit() != null && ca.getHighLimit() < val) {
				LOGGER.info("Value is above the current high limit!");
				return ValidationStatus.warning(
						"Value is above the current high limit!");
			}
			break;
		case RELATIVE:
			if (ca.getLowLimit() != null && ca.getLowLimit() > mPos + val) {
				LOGGER.info("Value is below the current low limit");
				return ValidationStatus.warning(
						"Value is below the current low limit!");
			} else if (ca.getHighLimit() != null && 
					ca.getHighLimit() < mPos + val) {
				LOGGER.debug("Value is above the current high limit!");
				return ValidationStatus.warning(
						"Value is above the current high limit!");
			}
			break;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Validation ok: " + ca.getLowLimit() + " < " + val
					+ " < " + ca.getHighLimit());
		}
		return ValidationStatus.ok();
	}
}