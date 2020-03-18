package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import javax.xml.datatype.Duration;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsk
 * @since 1.34
 */
public class ValuesColumnStringFormatter {
	private static final String RIGHT_ARROW = Character.toString('\u2192');
	private static final String SLASH_WITH_SPACE = " / ";
	private static final String LONG_DASH = Character.toString('\u2014');
	private static final String WATCH = Character.toString('\u231a');
	private static final String HOUR_GLASS = Character.toString('\u231b');
	private static final String ALARM_CLOCK = Character.toString('\u23f0');
	private static final String STOP_WATCH = Character.toString('\u23f1');
	
	public static String getValuesString(Axis axis) {
		switch (axis.getStepfunction()) {
		case ADD:
			if (!axis.getType().equals(DataTypes.DATETIME)) {
				return axis.getStart().toString() + RIGHT_ARROW +
					axis.getStop().toString() + SLASH_WITH_SPACE + 
					axis.getStepwidth().toString();
			} else {
				return getDateString(axis);
			}
		case FILE:
			if (axis.getFile() != null && axis.getFile().getName() != null) {
				return axis.getFile().getName();
			}
			return "<path invalid>";
		case MULTIPLY:
			return axis.getStart().toString() + RIGHT_ARROW +
					axis.getStop().toString() + SLASH_WITH_SPACE + 
					axis.getStepwidth().toString();
		case PLUGIN:
			if (axis.getPluginController() == null || 
				axis.getPluginController().getPlugin() == null) {
					return "Plugin";
			}
			return "Plugin (" + axis.getPluginController().
				getPlugin().getName() + ")";
		case POSITIONLIST:
			return axis.getPositionlist();
		case RANGE:
			return axis.getRange();
		default:
			return LONG_DASH;
		}
	}
	
	private static String getDateString(Axis axis) {
		StringBuilder sb = new StringBuilder();
		switch (axis.getPositionMode()) {
		case ABSOLUTE:
			return Character.toString('\u25f4') + 
					Character.toString('\u25f5') + 
					Character.toString('\u25f6') + 
					Character.toString('\u25f7') + 
					Character.toString('\u231a')  + 
					Character.toString('\u231b')  + 
					Character.toString('\u23f1') ;
		case RELATIVE:
			if (!axis.getStart().toString().equals("P0Y0M0DT0H0M0.000S")) {
				sb.append(WATCH + ": ");
				Duration startDuration = (Duration)axis.getStart();
				if (startDuration.getHours() != 0) {
					sb.append(startDuration.getHours() + "h ");
				}
				if (startDuration.getMinutes() != 0) {
					sb.append(startDuration.getMinutes() + "min ");
				}
				if (startDuration.getSeconds() != 0) {
					sb.append(startDuration.getSeconds() + "s ");
				}
				sb.replace(sb.length()-1, sb.length(), ", ");
			}
			sb.append(HOUR_GLASS + ": ");
			Duration stepwidthDuration = (Duration)axis.getStepwidth();
			if (stepwidthDuration.getHours() != 0) {
				sb.append(stepwidthDuration.getHours() + "h ");
			}
			if (stepwidthDuration.getMinutes() != 0) {
				sb.append(stepwidthDuration.getMinutes() + "min ");
			}
			if (stepwidthDuration.getSeconds() != 0) {
				sb.append(stepwidthDuration.getSeconds() + "s ");
			}
			sb.replace(sb.length()-1, sb.length(), "");
			break;
		}
		return sb.toString();
	}
	
	private ValuesColumnStringFormatter() {
	}
}
