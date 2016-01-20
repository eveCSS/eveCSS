package de.ptb.epics.eve.data.scandescription.macro;

import java.util.Calendar;

/**
 * Macro <code>${WEEK}</code> resolving to calendar week.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class WeekMacro extends Macro {

	public WeekMacro() {
		this.setName("${WEEK}");
		this.setDescription("calendar week");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		int week = Calendar.getInstance().get(
				Calendar.WEEK_OF_YEAR);
		if (week < 10) {
			return "0" + Integer.toString(week);
		}
		return Integer.toString(week);
	}
}