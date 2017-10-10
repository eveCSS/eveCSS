package de.ptb.epics.eve.editor.views.motoraxisview.rangecomposite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.util.math.range.DoubleRange;
import de.ptb.epics.eve.util.math.range.IntegerRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class RangeTargetToModelValidator implements IValidator {
	private DataTypes type;
	
	public RangeTargetToModelValidator(DataTypes type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String patternString = "";
		switch (this.type) {
		case DOUBLE:
			patternString = DoubleRange.DOUBLE_RANGE_REPEATED_REGEXP;
			break;
		case INT:
			patternString = IntegerRange.INTEGER_RANGE_REPEATED_REGEXP;
			break;
		default:
			patternString = "";
			break;
		}
		Pattern p = Pattern.compile(patternString);
		Matcher m = p.matcher((String)value);
		if (!m.matches()) {
			return ValidationStatus.error("expressions is invalid");
		}
		return ValidationStatus.ok();
	}
}