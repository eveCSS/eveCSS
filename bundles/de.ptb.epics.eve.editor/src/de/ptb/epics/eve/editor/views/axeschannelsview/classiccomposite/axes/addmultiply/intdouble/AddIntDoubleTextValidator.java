package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextValidator {
	private static final String MSG_SYNTAX = 
			"Syntax must be a / b / c / d where exactly one of [a,b,c,d] must be omitted (a dash '-')";
	private static final String MSG_SYNTAX_MAINAXIS = 
			"Syntax must be a / b / c where exactly one of [a,b,c] must be omitted (a dash '-')";
	private static final String MSG_DASH_NOT_FOUND = 
			"No dash found.";
	private static final String MSG_DASH_MULTIPLE_FOUND = 
			"Multiple dashes found.";
	private static final String MSG_DASH_INFO = 
			"Define exactly one element as dash.";
	private static final String MSG_TYPE_WRONG_INT = 
			"Elements must be of type int.";
	private static final String MSG_TYPE_WRONG_DOUBLE = 
			"Elements must be of type double.";
	private static final String MSG_TYPE_WRONG_STEPWIDTH = 
			"Stepwidth must be a double.";
	
	private Axis axis;
	private boolean mainAxisSet;
	
	public AddIntDoubleTextValidator(Axis axis) {
		this.axis = axis;
		if (axis.getScanModule().getMainAxis() != null &&
				!axis.getScanModule().getMainAxis().equals(axis)) {
			mainAxisSet = true;
		} else {
			mainAxisSet = false;
		}
	}
	
	/**
	 * @param value the value to be validated
	 * @return the error message, or null indicating that the value is valid
	 */
	public String isValid(String value) {
		// empty entry ?
		if (value.isEmpty()) {
			if (mainAxisSet) {
				return MSG_SYNTAX;
			} else {
				return MSG_SYNTAX_MAINAXIS;
			}
		}
		
		// entry with wrong number of slashes ?
		String[] elements = value.split("/");
		if (mainAxisSet) {
			if (elements.length != 3) {
				return MSG_SYNTAX_MAINAXIS;
			}
		} else {
			if (elements.length != 4) {
				return MSG_SYNTAX;
			}
		}
		
		// exactly one entry must be a dash
		int count = 0;
		for (String element : elements) {
			if (element.trim().equals("-")) {
				count++;
			}
		}
		if (count == 0) {
			return MSG_DASH_NOT_FOUND + MSG_DASH_INFO;
		} else if (count > 1) {
			return MSG_DASH_MULTIPLE_FOUND + MSG_DASH_INFO;
		}
		
		List<String> numbers = new ArrayList<>();
		for (String element : elements) {
			if (!element.trim().equals("-")) {
				numbers.add(element);
			}
		}
		
		// entries of wrong type ?
		switch (axis.getType()) {
		case DOUBLE:
			try {
				for (String element : numbers) {
					Double.parseDouble(element.trim());
				}
			} catch (NumberFormatException e) {
				return MSG_TYPE_WRONG_DOUBLE;
			}
			break;
		case INT:
			try {
				for (String element : numbers.subList(0, numbers.size()-1)) {
					Integer.parseInt(element.trim());
				}
			} catch (NumberFormatException e) {
				return MSG_TYPE_WRONG_INT;
			}
			try {
				Double.parseDouble(numbers.get(numbers.size()-1));
			} catch (NumberFormatException e) {
				return MSG_TYPE_WRONG_STEPWIDTH;
			}
			break;
		default:
			break;
		}
		
		// correct input
		return null;
	}
}
