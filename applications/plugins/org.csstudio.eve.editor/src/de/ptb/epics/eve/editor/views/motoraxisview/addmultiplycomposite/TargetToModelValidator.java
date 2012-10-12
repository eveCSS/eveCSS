package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TargetToModelValidator implements IValidator {

	private static final Logger LOGGER = Logger
			.getLogger(TargetToModelValidator.class.getName());
	
	private DataTypes type;
	
	/**
	 * @param type the type
	 */
	public TargetToModelValidator(DataTypes type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		if (value instanceof Double) {
			return ValidationStatus.ok();
		}
		switch (this.type) {
		case DOUBLE:
			try {
				Double.parseDouble(((String)value));
			} catch (Exception e) {
				LOGGER.debug("error validating target to model " + "(" + type
						+ ")");
				return ValidationStatus.error("cannot parse double");
			}
			break;
		case INT:
			try {
				Integer.parseInt((String)value);
			} catch (Exception e) {
				LOGGER.debug("error validating target to model " + "(" + type
						+ ")");
				return ValidationStatus.error("cannot parse int");
			}
			break;
		default:
			return ValidationStatus.error("");
		}
		LOGGER.debug("validation ok");
		return ValidationStatus.ok();
	}
}