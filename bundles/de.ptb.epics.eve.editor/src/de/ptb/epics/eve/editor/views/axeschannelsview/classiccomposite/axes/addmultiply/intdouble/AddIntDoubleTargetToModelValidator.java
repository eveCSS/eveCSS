package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.math.Constants;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTargetToModelValidator implements IValidator {
	private static final Logger LOGGER = Logger.getLogger(
			AddIntDoubleTargetToModelValidator.class.getName());
	
	private Axis axis;

	public AddIntDoubleTargetToModelValidator(Axis axis) {
		this.axis = axis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		switch (this.axis.getType()) {
		case DOUBLE:
			if (!((String)value).matches(Constants.FLOATING_POINT_REGEXP)) {
				LOGGER.debug("error validating target to model (double)");
				return ValidationStatus.error("Cannot parse double!");
			}
			break;
		case INT:
			try {
				Integer.parseInt((String)value);
			} catch (NumberFormatException e) {
				LOGGER.debug("error validating target to model (int)");
				return ValidationStatus.error("Cannot parse integer!");
			}
			break;
		default:
			break;
		}
		return ValidationStatus.ok();
	}
}
