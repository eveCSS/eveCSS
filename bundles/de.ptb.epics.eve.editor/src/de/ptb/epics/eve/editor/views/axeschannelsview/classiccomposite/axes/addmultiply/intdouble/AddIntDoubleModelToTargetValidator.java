package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleModelToTargetValidator implements IValidator {
	private static final Logger LOGGER = Logger.getLogger(
			AddIntDoubleModelToTargetValidator.class.getName());
	
	private Axis axis;
	
	public AddIntDoubleModelToTargetValidator(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		switch (this.axis.getType()) {
		case DOUBLE:
			return ValidationStatus.ok();
		case INT:
			return ValidationStatus.ok();
		default:
			LOGGER.debug("validation error");
			return ValidationStatus.error("error reading model value");
		}
	}
}
