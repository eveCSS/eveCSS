package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Macro <code>${TIME}</code> resolving to HHmmss.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class TimeMacro extends Macro {

	public TimeMacro() {
		this.setName("${TIME}");
		this.setDescription("time as HHmmss");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("HHmmss").format(Calendar.getInstance()
				.getTime());
	}
}