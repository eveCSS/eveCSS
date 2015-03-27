package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Macro <code>${DATE-}</code> resolving to yyyy-MM-dd (e.g., 2011-12-31).
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class DateHyphenMacro extends Macro {

	public DateHyphenMacro() {
		this.setName("${DATE-}");
		this.setDescription("date as yyyy-MM-dd (e.g., 2011-12-31)");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance()
				.getTime());
	}
}