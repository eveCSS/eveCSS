package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Marcus Michalsky
 * @since 1.25.1
 */
public class Year2DMacro extends Macro {
	public Year2DMacro() {
		this.setName("${YR}");
		this.setDescription("Year as yy");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("yy").format(Calendar.getInstance().getTime());
	}

}
