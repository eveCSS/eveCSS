package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class ValueCountValidator implements IValidator {

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
			if (i <= 0) {
				return ValidationStatus.error("Value Count must be positive!");
			}
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse integer.");
		}
	}
}