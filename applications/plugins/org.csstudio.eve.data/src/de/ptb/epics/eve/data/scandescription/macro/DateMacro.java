package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class DateMacro extends Macro {

	public DateMacro() {
		this.setName("${DATE}");
		this.setDescription("date as yyyyMMdd (e.g., 20111231)");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance()
				.getTime());
	}
}