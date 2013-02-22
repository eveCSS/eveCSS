package de.ptb.epics.eve.editor.views.motoraxisview.positionlistcomposite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.statistics.Range;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class PositionlistValidator implements IValidator {
	
	private DataTypes type;
	private Axis axis;
	
	/**
	 * 
	 * @param axis the axis
	 */
	public PositionlistValidator(Axis axis) {
		this.axis = axis;
		this.type = axis.getType();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String positions = String.valueOf(value);
		if (positions.isEmpty()) {
			return ValidationStatus.error("Please provide at least 1 position!");
		}
		if (!isValid(positions)) {
			return ValidationStatus.error("The list contains syntax errors");
		}
		
		Double hlm = this.axis.getMotorAxis().getChannelAccess().getHighLimit();
		Double llm = this.axis.getMotorAxis().getChannelAccess().getLowLimit();
		
		switch (axis.getType()) {
		case DOUBLE: // intended fall through
		case INT:
			List<Double> values = StringUtil.getDoubleList(Arrays
					.asList(((String) value).split(",")));
			// do limit check only for non-discrete motor axes
			if (!this.axis.getMotorAxis().getGoto().isDiscrete()) {
				List<Double> problems = new ArrayList<Double>();
				if (hlm != null && llm != null) {
					for (double d : values) {
						if (!Range.isInRange(d, llm, hlm)) {
							problems.add(d);
						}
					}
				}
				if (!problems.isEmpty()) {
					return ValidationStatus.warning(
						"The following values exceed the current axis bounds: "
						+ StringUtil.buildCommaSeparatedString(problems));
				}
			}
			break;
		case STRING:
			break;
		default:
			return ValidationStatus.error("unknown axis type");
		}
		return ValidationStatus.ok();
	}
	
	/*
	 * 
	 */
	private boolean isValid(String list) {
		switch (this.type) {
		case DOUBLE:
			return StringUtil.isPositionList(list, Double.class);
		case INT:
			return StringUtil.isPositionList(list, Integer.class);
		case STRING:
			return StringUtil.isPositionList(list, String.class);
		default:
			return false;
		}
	}
}