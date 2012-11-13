package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Intended as SetAfterGetValidator for MaxAttempts property.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class MaxAttemptsTargetToModelValidator implements IValidator {

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
			int i = Integer.parseInt(val);
			if (i < 0) {
				return ValidationStatus.error(
						"Max Attempts must not be negative!");
			}
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse integer.");
		}
	}
}