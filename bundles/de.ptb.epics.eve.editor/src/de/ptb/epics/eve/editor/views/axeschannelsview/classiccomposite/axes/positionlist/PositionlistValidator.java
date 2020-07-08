package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistValidator implements IValidator {
	private Axis axis;
	
	public PositionlistValidator(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String positions = String.valueOf(value);
		if (positions.isEmpty()) {
			return ValidationStatus.error("Provide at least 1 position!");
		}
		if (!isValid(positions)) {
			return ValidationStatus.error("The list contains syntax errors");
		}
		return ValidationStatus.ok();
	}
	
	private boolean isValid(String positionList) {
		switch (axis.getType()) {
		case DOUBLE:
			return StringUtil.isPositionList(positionList, Double.class);
		case INT:
			return StringUtil.isPositionList(positionList, Integer.class);
		case STRING:
			return StringUtil.isPositionList(positionList, String.class);
		default:
			return false;
		}
	}
}
