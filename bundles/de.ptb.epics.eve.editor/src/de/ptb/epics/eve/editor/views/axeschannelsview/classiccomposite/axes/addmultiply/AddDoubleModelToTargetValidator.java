package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDoubleModelToTargetValidator implements IValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		return ValidationStatus.ok();
	}
}
