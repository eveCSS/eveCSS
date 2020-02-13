package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class MaxDeviationTargetToModelValidator implements IValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String val = (String)value;
		if (val.isEmpty()) {
			return ValidationStatus.ok();
		}
		try {
			Double d = Double.parseDouble(val);
			if (d < 0) {
				return ValidationStatus.error(
						"Standard deviation must be positive!");
			}
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse double!");
		}
	}
}
