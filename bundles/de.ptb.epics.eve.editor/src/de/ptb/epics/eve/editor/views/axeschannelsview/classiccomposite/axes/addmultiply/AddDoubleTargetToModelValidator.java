package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.util.math.Constants;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDoubleTargetToModelValidator implements IValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		if (value instanceof Double) {
			return ValidationStatus.ok();
		}
		if (value.toString().matches(Constants.FLOATING_POINT_REGEXP)) {
			return ValidationStatus.ok();
		}
		return ValidationStatus.error("cannot parse double");
	}
}
