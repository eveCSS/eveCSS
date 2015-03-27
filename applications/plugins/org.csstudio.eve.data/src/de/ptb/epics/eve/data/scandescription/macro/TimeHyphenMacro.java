package de.ptb.epics.eve.data.scandescription.macro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Macro <code>${TIME-}</code> resolving to HH-mm-ss.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class TimeHyphenMacro extends Macro {

	public TimeHyphenMacro() {
		this.setName("${TIME-}");
		this.setDescription("time as HH-mm-ss");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return new SimpleDateFormat("HH-mm-ss").format(Calendar.getInstance()
				.getTime());
	}
}