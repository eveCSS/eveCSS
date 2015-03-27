package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Macro <code>${MONTHSTR}</code> resolving to MMM (three digit string).
 * NOTE: Always based on en_US locale!
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class MonthStrMacro extends Macro {

	public MonthStrMacro() {
		this.setName("${MONTHSTR}");
		this.setDescription("month as MMM (e.g., Jul)");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("MMM").format(Calendar.getInstance(
				new Locale("en", "US")).getTime());
	}
}