package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Macro <code>${DAYSTR}</code> resolving to day as EEE (three letter month).
 * NOTE: Always based on en_US locale!
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class DayStrMacro extends Macro {

	public DayStrMacro() {
		this.setName("${DAYSTR}");
		this.setDescription("day as EEE (e.g., Mon)");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("EEE").format(Calendar
				.getInstance(new Locale("en", "US")).getTime());
	}
}