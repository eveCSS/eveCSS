package de.ptb.epics.eve.data.scandescription.macro;

import java.util.Calendar;

/**
 * Macro <code>${YEAR}</code> resolving to YYYY (year with four digits).
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class YearMacro extends Macro {

	public YearMacro() {
		this.setName("${YEAR}");
		this.setDescription("year as yyyy");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	}
}