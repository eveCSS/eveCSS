package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;

/**
 * Converts a "a / b / c / d" or "a / b / c" String (for main axis) into typed
 * values. Expects a valid input. Used in Editing Support setValue method.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextConverter {
	private static final Logger LOGGER = Logger.getLogger(
			AddIntDoubleTextConverter.class.getName());
	
	public static AddIntDoubleValues<Integer> convertInt(String input) {
		String[] elements = input.split("/");
		int dashIndex = 4;
		for (int i=0; i < elements.length; i++) {
			if (elements[i].trim().equals("-")) {
				dashIndex = i;
			}
		}
		if (dashIndex == 4) {
			LOGGER.error("no dash found in input");
			return null;
		}
		String start = elements[0].trim();
		String stop = elements[1].trim();
		String stepwidth = elements[2].trim();
		Double stepcount;
		if (elements.length == 3) {
			// main axis set, stepcount supervised by main axis
			stepcount = Double.NaN;
		} else if (elements[3].trim().equals("-")){
			stepcount = null;
		} else {
			stepcount = Double.parseDouble(elements[3].trim());
		}
		switch (dashIndex) {
		case 0:
			// start omitted
			return new AddIntDoubleValues<>(null, Integer.parseInt(stop), 
				Integer.parseInt(stepwidth), stepcount, AdjustParameter.START);
		case 1:
			// stop omitted
			return new AddIntDoubleValues<>(Integer.parseInt(start), null,
				Integer.parseInt(stepwidth), stepcount, AdjustParameter.STOP);
		case 2:
			// stepwidth omitted
			return new AddIntDoubleValues<>(Integer.parseInt(start), 
					Integer.parseInt(stop), null, stepcount, 
					AdjustParameter.STEPWIDTH);
		case 3:
			// stepcount omitted (only possible if no main axis is set)
			return new AddIntDoubleValues<>(Integer.parseInt(start), 
					Integer.parseInt(stop), Integer.parseInt(stepwidth), null, 
					AdjustParameter.STEPCOUNT);
		default:
			break;
		}
		return null;
	}
	
	public static AddIntDoubleValues<Double> convertDouble(String input) {
		String[] elements = input.split("/");
		int dashIndex = 4; 
		for (int i=0; i < elements.length; i++) {
			if (elements[i].trim().equals("-")) {
				dashIndex = i;
			}
		}
		if (dashIndex == 4) {
			LOGGER.error("no dash found in input");
			return null;
		}
		String start = elements[0].trim();
		String stop = elements[1].trim();
		String stepwidth = elements[2].trim();
		Double stepcount;
		if (elements.length == 3) {
			// main axis set, stepcount supervised by main axis
			stepcount = Double.NaN;
		} else if (elements[3].trim().equals("-")){
			stepcount = null;
		} else {
			stepcount = Double.parseDouble(elements[3].trim());
		}
		switch (dashIndex) {
		case 0:
			// start omitted
			return new AddIntDoubleValues<>(null, Double.parseDouble(stop), 
				Double.parseDouble(stepwidth), stepcount, AdjustParameter.START);
		case 1:
			// stop omitted
			return new AddIntDoubleValues<>(Double.parseDouble(start), null,
				Double.parseDouble(stepwidth), stepcount, AdjustParameter.STOP);
		case 2:
			// stepwidth omitted
			return new AddIntDoubleValues<>(Double.parseDouble(start), 
					Double.parseDouble(stop), null, stepcount, 
					AdjustParameter.STEPWIDTH);
		case 3:
			// stepcount omitted (only possible if no main axis is set)
			return new AddIntDoubleValues<>(Double.parseDouble(start), 
					Double.parseDouble(stop), Double.parseDouble(stepwidth), 
					null, AdjustParameter.STEPCOUNT);
		default:
			break;
		}
		return null;
	}
	
	private AddIntDoubleTextConverter() {
	}
}
