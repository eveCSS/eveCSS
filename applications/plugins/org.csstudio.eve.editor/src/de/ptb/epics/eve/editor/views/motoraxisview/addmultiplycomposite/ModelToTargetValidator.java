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
public class ModelToTargetValidator implements IValidator {

	private static final Logger LOGGER = Logger
			.getLogger(ModelToTargetValidator.class.getName());
	
	private DataTypes type;
	
	/**
	 * @param type the type
	 */
	public ModelToTargetValidator(DataTypes type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		switch (this.type) {
		case DOUBLE:
		case INT:
		case DATETIME:
			LOGGER.debug("validation ok");
			return ValidationStatus.ok();
		default:
			LOGGER.debug("validation error");
			return ValidationStatus.error("error reading model value");
		}
	}
}