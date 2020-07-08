package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.Duration;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ValuesColumnStringFormatter {
	private static final String RIGHT_ARROW = Character.toString('\u2192');
	private static final String SLASH_WITH_SPACE = " / ";
	private static final String LONG_DASH = Character.toString('\u2014');
	
	public static String getValuesString(Axis axis) {
		switch (axis.getStepfunction()) {
		case ADD:
			if (axis.getType().equals(DataTypes.DATETIME)) {
				return getDateString(axis);
			} else {
				return axis.getStart().toString() + RIGHT_ARROW +
						axis.getStop().toString() + SLASH_WITH_SPACE + 
						axis.getStepwidth().toString();
			}
		case FILE:
			if (axis.getFile() != null && axis.getFile().getName() != null) {
				return axis.getFile().getName();
			}
			return "<no file set>";
		case MULTIPLY:
			return axis.getStart().toString() + RIGHT_ARROW +
					axis.getStop().toString() + SLASH_WITH_SPACE + 
					axis.getStepwidth().toString();
		case PLUGIN:
			if (axis.getPluginController() == null || 
				axis.getPluginController().getPlugin() == null) {
					return "<no plugin selected>";
			}
			return "Plugin (" + axis.getPluginController().
				getPlugin().getName() + ")";
		case POSITIONLIST:
			return axis.getPositionlist();
		case RANGE:
			if (axis.getRange() == null) {
				return "";
			}
			return axis.getRange();
		default:
			return LONG_DASH;
		}
	}
	
	private static String getDateString(Axis axis) {
		StringBuilder sb = new StringBuilder();
		switch (axis.getPositionMode()) {
		case ABSOLUTE:
			SimpleDateFormat dateFormat = 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			sb.append(dateFormat.format((Date)axis.getStart()));
			sb.append(RIGHT_ARROW);
			sb.append(dateFormat.format((Date)axis.getStop()));
			sb.append(SLASH_WITH_SPACE);
			sb.append(new SimpleDateFormat("HH:mm:ss.SSS").format(
					(Date)axis.getStepwidth()));
			break;
		case RELATIVE:
			if (!axis.getStart().toString().equals("P0Y0M0DT0H0M0.000S")) {
				// show only if start is not "now" (0)
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
			} else {
				sb.append("0s ");
			}
			sb.append(RIGHT_ARROW + " ");
			
			Duration stopDuration = (Duration)axis.getStop();
			if (stopDuration.getHours() != 0) {
				sb.append(stopDuration.getHours() + "h ");
			}
			if (stopDuration.getMinutes() != 0) {
				sb.append(stopDuration.getMinutes() + "min ");
			}
			if (stopDuration.getSeconds() != 0) {
				sb.append(stopDuration.getSeconds() + "s ");
			}
			sb.replace(sb.length()-1, sb.length(), "");
			
			sb.append(" / ");
			
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
