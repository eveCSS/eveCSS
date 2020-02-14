package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval;

import java.text.ParseException;
import java.util.Locale;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import com.ibm.icu.text.NumberFormat;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class TriggerIntervalTargetToModelValidator implements IValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String val = (String)value;
		if (val.isEmpty()) {
			return ValidationStatus.error("Trigger Interval is mandatory!");
		}
		try {
			NumberFormat.getInstance(Locale.US).parse(val);
		} catch (ParseException e) {
			return ValidationStatus.error(e.getMessage());
		}
		return ValidationStatus.ok();
	}
}
