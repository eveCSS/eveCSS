package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class TriggerDelaySettleTimeTargetToModelValidator implements IValidator {
	private String message;
	
	public TriggerDelaySettleTimeTargetToModelValidator(String message) {
		this.message = message;
	}
	
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
			double d = Double.parseDouble(val);
			if (d < 0.0) {
				return ValidationStatus.error(this.message + 
						"must not be negative!");
			}
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse double.");
		}
	}
}
