package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Macro <code>${MONTH}</code> resolving to MM (month with two digits).
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class MonthMacro extends Macro {

	public MonthMacro() {
		this.setName("${MONTH}");
		this.setDescription("month as MM");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("MM").format(Calendar.getInstance()
				.getTime());
	}
}