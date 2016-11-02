package de.ptb.epics.eve.editor.views.detectorchannelview;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class MinimumTargetToModelValidator implements IValidator {

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
			Double.parseDouble(val);
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse double.");
		}
	}
}