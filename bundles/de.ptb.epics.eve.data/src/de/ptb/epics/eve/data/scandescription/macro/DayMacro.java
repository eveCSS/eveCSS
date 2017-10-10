package de.ptb.epics.eve.data.scandescription.macro;

import java.util.Calendar;

/**
 * Macro <code>${DAY}</code> resolving to dd (two digit day of month).
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class DayMacro extends Macro {

	public DayMacro() {
		this.setName("${DAY}");
		this.setDescription("day as dd");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return Integer.toString(Calendar.getInstance().get(
				Calendar.DAY_OF_MONTH));
	}
}